package com.gundogar.lineupapp.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity
import com.gundogar.lineupapp.data.model.DrawingStroke
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.SerializableDrawingStroke
import com.gundogar.lineupapp.data.model.TeamConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

data class SavedLineup(
    val id: Long = 0,
    val teamName: String,
    val formationId: String,
    val formationName: String,
    val players: Map<Int, Player>,
    val customPositions: List<Position>? = null,
    val playerCount: Int = 11,
    val teamConfig: TeamConfig,
    val drawingStrokes: List<DrawingStroke> = emptyList(),
    val createdAt: Long,
    val updatedAt: Long
)

@Singleton
class SavedLineupRepository @Inject constructor(private val dao: SavedLineupDao) {

    private val gson = Gson()

    fun getAllLineups(): Flow<List<SavedLineup>> {
        return dao.getAllLineups().map { entities ->
            entities.map { it.toSavedLineup() }
        }
    }

    suspend fun getLineupById(id: Long): SavedLineup? {
        return dao.getLineupById(id)?.toSavedLineup()
    }

    suspend fun saveLineup(
        teamName: String,
        formationId: String,
        formationName: String,
        players: Map<Int, Player>,
        customPositions: List<Position>? = null,
        playerCount: Int = 11,
        teamConfig: TeamConfig,
        drawingStrokes: List<DrawingStroke> = emptyList(),
        existingId: Long? = null
    ): Long {
        // Serialize drawing strokes to JSON using serializable version
        val drawingStrokesJson = if (drawingStrokes.isNotEmpty()) {
            val serializableStrokes = drawingStrokes.map { SerializableDrawingStroke.fromDrawingStroke(it) }
            gson.toJson(serializableStrokes)
        } else {
            null
        }

        val entity = SavedLineupEntity(
            id = existingId ?: 0,
            teamName = teamName,
            formationId = formationId,
            formationName = formationName,
            playersJson = gson.toJson(players),
            positionsJson = customPositions?.let { gson.toJson(it) },
            playerCount = playerCount,
            primaryColor = teamConfig.primaryColor.toArgb().toLong(),
            secondaryColor = teamConfig.secondaryColor.toArgb().toLong(),
            jerseyStyle = teamConfig.jerseyStyle.name,
            drawingStrokesJson = drawingStrokesJson,
            createdAt = if (existingId != null) {
                dao.getLineupById(existingId)?.createdAt ?: System.currentTimeMillis()
            } else {
                System.currentTimeMillis()
            },
            updatedAt = System.currentTimeMillis()
        )
        return dao.insertLineup(entity)
    }

    suspend fun deleteLineup(id: Long) {
        dao.deleteLineupById(id)
    }

    private fun SavedLineupEntity.toSavedLineup(): SavedLineup {
        val playersType = object : TypeToken<Map<Int, Player>>() {}.type
        val players: Map<Int, Player> = try {
            gson.fromJson(playersJson, playersType) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }

        val positionsType = object : TypeToken<List<Position>>() {}.type
        val customPositions: List<Position>? = positionsJson?.let {
            try {
                gson.fromJson(it, positionsType)
            } catch (e: Exception) {
                null
            }
        }

        // Deserialize drawing strokes
        val strokesType = object : TypeToken<List<SerializableDrawingStroke>>() {}.type
        val drawingStrokes: List<DrawingStroke> = drawingStrokesJson?.let { json ->
            try {
                val serializableStrokes: List<SerializableDrawingStroke> = gson.fromJson(json, strokesType)
                serializableStrokes.map { it.toDrawingStroke() }
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()

        val teamConfig = TeamConfig(
            teamName = teamName,
            primaryColor = Color(primaryColor.toInt()),
            secondaryColor = Color(secondaryColor.toInt()),
            jerseyStyle = try {
                JerseyStyle.valueOf(jerseyStyle)
            } catch (e: Exception) {
                JerseyStyle.SOLID
            }
        )

        return SavedLineup(
            id = id,
            teamName = teamName,
            formationId = formationId,
            formationName = formationName,
            players = players,
            customPositions = customPositions,
            playerCount = playerCount,
            teamConfig = teamConfig,
            drawingStrokes = drawingStrokes,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

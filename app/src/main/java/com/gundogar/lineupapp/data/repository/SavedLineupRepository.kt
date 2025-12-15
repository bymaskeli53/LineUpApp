package com.gundogar.lineupapp.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gundogar.lineupapp.data.local.dao.SavedLineupDao
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.TeamConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class SavedLineup(
    val id: Long = 0,
    val teamName: String,
    val formationId: String,
    val formationName: String,
    val players: Map<Int, Player>,
    val teamConfig: TeamConfig,
    val createdAt: Long,
    val updatedAt: Long
)

class SavedLineupRepository(private val dao: SavedLineupDao) {

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
        teamConfig: TeamConfig,
        existingId: Long? = null
    ): Long {
        val entity = SavedLineupEntity(
            id = existingId ?: 0,
            teamName = teamName,
            formationId = formationId,
            formationName = formationName,
            playersJson = gson.toJson(players),
            primaryColor = teamConfig.primaryColor.toArgb().toLong(),
            secondaryColor = teamConfig.secondaryColor.toArgb().toLong(),
            jerseyStyle = teamConfig.jerseyStyle.name,
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
            teamConfig = teamConfig,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

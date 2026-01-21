package com.gundogar.lineupapp.data.repository

import com.gundogar.lineupapp.data.local.dao.TacticDao
import com.gundogar.lineupapp.data.local.entity.TacticEntity
import com.gundogar.lineupapp.data.model.SerializableTacticFrame
import com.gundogar.lineupapp.data.model.Tactic
import com.gundogar.lineupapp.data.model.TacticFrame
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TacticRepository @Inject constructor(private val dao: TacticDao) {

    private val gson = Gson()

    fun getAllTactics(): Flow<List<Tactic>> {
        return dao.getAllTactics().map { entities ->
            entities.mapNotNull { it.toTactic() }
        }
    }

    suspend fun getTacticById(id: String): Tactic? {
        return dao.getTacticById(id)?.toTactic()
    }

    suspend fun saveTactic(
        name: String,
        frames: List<TacticFrame>,
        existingId: String? = null
    ): String {
        val id = existingId ?: UUID.randomUUID().toString()

        val serializableFrames = frames.map { SerializableTacticFrame.fromTacticFrame(it) }
        val framesJson = gson.toJson(serializableFrames)

        val existingTactic = existingId?.let { dao.getTacticById(it) }

        val entity = TacticEntity(
            id = id,
            name = name,
            framesJson = framesJson,
            createdAt = existingTactic?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        dao.insertTactic(entity)
        return id
    }

    suspend fun deleteTactic(id: String) {
        dao.deleteTacticById(id)
    }

    private fun TacticEntity.toTactic(): Tactic? {
        val framesType = object : TypeToken<List<SerializableTacticFrame>>() {}.type
        val frames: List<TacticFrame> = try {
            val serializableFrames: List<SerializableTacticFrame> = gson.fromJson(framesJson, framesType)
            serializableFrames.map { it.toTacticFrame() }
        } catch (e: Exception) {
            return null
        }

        return Tactic(
            id = id,
            name = name,
            frames = frames,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

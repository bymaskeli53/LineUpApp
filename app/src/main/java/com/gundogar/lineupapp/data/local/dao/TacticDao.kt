package com.gundogar.lineupapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gundogar.lineupapp.data.local.entity.TacticEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TacticDao {

    @Query("SELECT * FROM tactics ORDER BY updatedAt DESC")
    fun getAllTactics(): Flow<List<TacticEntity>>

    @Query("SELECT * FROM tactics WHERE id = :id")
    suspend fun getTacticById(id: String): TacticEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTactic(tactic: TacticEntity)

    @Update
    suspend fun updateTactic(tactic: TacticEntity)

    @Delete
    suspend fun deleteTactic(tactic: TacticEntity)

    @Query("DELETE FROM tactics WHERE id = :id")
    suspend fun deleteTacticById(id: String)

    @Query("SELECT COUNT(*) FROM tactics")
    suspend fun getTacticCount(): Int
}

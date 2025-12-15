package com.gundogar.lineupapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLineupDao {

    @Query("SELECT * FROM saved_lineups ORDER BY updatedAt DESC")
    fun getAllLineups(): Flow<List<SavedLineupEntity>>

    @Query("SELECT * FROM saved_lineups WHERE id = :id")
    suspend fun getLineupById(id: Long): SavedLineupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLineup(lineup: SavedLineupEntity): Long

    @Update
    suspend fun updateLineup(lineup: SavedLineupEntity)

    @Delete
    suspend fun deleteLineup(lineup: SavedLineupEntity)

    @Query("DELETE FROM saved_lineups WHERE id = :id")
    suspend fun deleteLineupById(id: Long)

    @Query("SELECT COUNT(*) FROM saved_lineups")
    suspend fun getLineupCount(): Int
}

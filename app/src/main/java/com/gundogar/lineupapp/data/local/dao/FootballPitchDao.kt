package com.gundogar.lineupapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gundogar.lineupapp.data.local.entity.FootballPitchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FootballPitchDao {

    @Query("SELECT * FROM football_pitches")
    fun getAllPitches(): Flow<List<FootballPitchEntity>>

    @Query("SELECT * FROM football_pitches WHERE cachedAt > :minTimestamp")
    suspend fun getRecentPitches(minTimestamp: Long): List<FootballPitchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPitches(pitches: List<FootballPitchEntity>)

    @Query("DELETE FROM football_pitches WHERE cachedAt < :maxAge")
    suspend fun deleteOldCache(maxAge: Long)

    @Query("DELETE FROM football_pitches")
    suspend fun clearAll()
}

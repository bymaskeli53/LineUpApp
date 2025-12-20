package com.gundogar.lineupapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gundogar.lineupapp.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

    @Query("SELECT * FROM matches ORDER BY createdAt DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE tournamentId IS NULL ORDER BY createdAt DESC")
    fun getFriendlyMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchNumber ASC")
    fun getMatchesByTournament(tournamentId: Long): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId ORDER BY matchNumber ASC")
    suspend fun getMatchesByTournamentSync(tournamentId: Long): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE tournamentId = :tournamentId AND tournamentRound = :round ORDER BY matchNumber ASC")
    suspend fun getMatchesByRound(tournamentId: Long, round: String): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE id = :id")
    suspend fun getMatchById(id: Long): MatchEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>): List<Long>

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Query("DELETE FROM matches WHERE id = :id")
    suspend fun deleteMatchById(id: Long)

    @Query("DELETE FROM matches WHERE tournamentId = :tournamentId")
    suspend fun deleteMatchesByTournament(tournamentId: Long)

    @Query("SELECT COUNT(*) FROM matches WHERE isCompleted = 1")
    suspend fun getCompletedMatchCount(): Int

    @Query("SELECT COUNT(*) FROM matches WHERE tournamentId = :tournamentId AND tournamentRound = :round AND isCompleted = 0")
    suspend fun getPendingMatchCountForRound(tournamentId: Long, round: String): Int
}

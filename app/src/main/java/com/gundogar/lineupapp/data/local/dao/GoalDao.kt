package com.gundogar.lineupapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gundogar.lineupapp.data.local.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals WHERE matchId = :matchId ORDER BY minute ASC, createdAt ASC")
    fun getGoalsByMatch(matchId: Long): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE matchId = :matchId ORDER BY minute ASC, createdAt ASC")
    suspend fun getGoalsByMatchSync(matchId: Long): List<GoalEntity>

    @Query("""
        SELECT scorerName, teamName, COUNT(*) as goalCount
        FROM goals
        WHERE matchId IN (SELECT id FROM matches WHERE tournamentId = :tournamentId)
        GROUP BY scorerName, teamName
        ORDER BY goalCount DESC
    """)
    suspend fun getTopScorersByTournament(tournamentId: Long): List<TopScorerResult>

    @Query("""
        SELECT scorerName, teamName, COUNT(*) as goalCount
        FROM goals
        GROUP BY scorerName, teamName
        ORDER BY goalCount DESC
        LIMIT :limit
    """)
    suspend fun getOverallTopScorers(limit: Int = 10): List<TopScorerResult>

    @Query("SELECT COUNT(*) FROM goals WHERE matchId = :matchId AND isHomeTeam = :isHomeTeam")
    suspend fun getGoalCountForTeam(matchId: Long, isHomeTeam: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Query("DELETE FROM goals WHERE matchId = :matchId")
    suspend fun deleteGoalsByMatch(matchId: Long)
}

data class TopScorerResult(
    val scorerName: String,
    val teamName: String,
    val goalCount: Int
)

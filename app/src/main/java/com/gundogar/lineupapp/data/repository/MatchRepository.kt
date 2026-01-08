package com.gundogar.lineupapp.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gundogar.lineupapp.data.local.dao.GoalDao
import com.gundogar.lineupapp.data.local.dao.MatchDao
import com.gundogar.lineupapp.data.local.entity.GoalEntity
import com.gundogar.lineupapp.data.local.entity.MatchEntity
import com.gundogar.lineupapp.data.model.Goal
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.data.model.Match
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.model.TournamentRound
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepository @Inject constructor(
    private val matchDao: MatchDao,
    private val goalDao: GoalDao
) {
    private val gson = Gson()

    fun getAllMatches(): Flow<List<Match>> {
        return matchDao.getAllMatches().map { entities ->
            entities.map { it.toMatch() }
        }
    }

    fun getFriendlyMatches(): Flow<List<Match>> {
        return matchDao.getFriendlyMatches().map { entities ->
            entities.map { it.toMatch() }
        }
    }

    fun getMatchesByTournament(tournamentId: Long): Flow<List<Match>> {
        return matchDao.getMatchesByTournament(tournamentId).map { entities ->
            entities.map { it.toMatch() }
        }
    }

    suspend fun getMatchById(id: Long): Match? {
        val entity = matchDao.getMatchById(id) ?: return null
        val goals = goalDao.getGoalsByMatchSync(id).map { it.toGoal() }
        return entity.toMatch().copy(goals = goals)
    }

    suspend fun createMatch(
        homeTeamName: String,
        awayTeamName: String,
        homeTeamConfig: TeamConfig? = null,
        awayTeamConfig: TeamConfig? = null,
        homePlayers: List<Player> = emptyList(),
        awayPlayers: List<Player> = emptyList(),
        tournamentId: Long? = null,
        tournamentRound: TournamentRound? = null,
        matchNumber: Int? = null
    ): Long {
        val entity = MatchEntity(
            homeTeamName = homeTeamName,
            awayTeamName = awayTeamName,
            homeTeamConfigJson = homeTeamConfig?.let { serializeTeamConfig(it) },
            awayTeamConfigJson = awayTeamConfig?.let { serializeTeamConfig(it) },
            homePlayersJson = if (homePlayers.isNotEmpty()) gson.toJson(homePlayers) else null,
            awayPlayersJson = if (awayPlayers.isNotEmpty()) gson.toJson(awayPlayers) else null,
            tournamentId = tournamentId,
            tournamentRound = tournamentRound?.name,
            matchNumber = matchNumber
        )
        return matchDao.insertMatch(entity)
    }

    suspend fun updateMatchScore(matchId: Long, homeScore: Int, awayScore: Int) {
        val match = matchDao.getMatchById(matchId) ?: return
        matchDao.updateMatch(
            match.copy(
                homeScore = homeScore,
                awayScore = awayScore
            )
        )
    }

    suspend fun addGoal(
        matchId: Long,
        scorerId: Int,
        scorerName: String,
        teamName: String,
        isHomeTeam: Boolean,
        minute: Int? = null
    ): Long {
        val goalEntity = GoalEntity(
            matchId = matchId,
            scorerId = scorerId,
            scorerName = scorerName,
            teamName = teamName,
            isHomeTeam = isHomeTeam,
            minute = minute
        )
        val goalId = goalDao.insertGoal(goalEntity)

        // Update match score
        val match = matchDao.getMatchById(matchId) ?: return goalId
        if (isHomeTeam) {
            matchDao.updateMatch(match.copy(homeScore = match.homeScore + 1))
        } else {
            matchDao.updateMatch(match.copy(awayScore = match.awayScore + 1))
        }
        return goalId
    }

    suspend fun removeGoal(goalId: Long, matchId: Long, isHomeTeam: Boolean) {
        goalDao.deleteGoalById(goalId)

        // Update match score
        val match = matchDao.getMatchById(matchId) ?: return
        if (isHomeTeam) {
            matchDao.updateMatch(match.copy(homeScore = maxOf(0, match.homeScore - 1)))
        } else {
            matchDao.updateMatch(match.copy(awayScore = maxOf(0, match.awayScore - 1)))
        }
    }

    suspend fun completeMatch(matchId: Long) {
        val match = matchDao.getMatchById(matchId) ?: return
        matchDao.updateMatch(
            match.copy(
                isCompleted = true,
                playedAt = System.currentTimeMillis()
            )
        )
    }

    fun getGoalsByMatch(matchId: Long): Flow<List<Goal>> {
        return goalDao.getGoalsByMatch(matchId).map { entities ->
            entities.map { it.toGoal() }
        }
    }

    suspend fun deleteMatch(matchId: Long) {
        matchDao.deleteMatchById(matchId)
    }

    // Serialization helpers
    private fun serializeTeamConfig(config: TeamConfig): String {
        val map = mapOf(
            "teamName" to config.teamName,
            "primaryColor" to config.primaryColor.toArgb().toLong(),
            "secondaryColor" to config.secondaryColor.toArgb().toLong(),
            "jerseyStyle" to config.jerseyStyle.name
        )
        return gson.toJson(map)
    }

    private fun deserializeTeamConfig(json: String): TeamConfig? {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(json, type)
            TeamConfig(
                teamName = map["teamName"] as? String ?: "Team",
                primaryColor = Color((map["primaryColor"] as? Double)?.toLong()?.toInt() ?: 0xFF1E88E5.toInt()),
                secondaryColor = Color((map["secondaryColor"] as? Double)?.toLong()?.toInt() ?: 0xFFFFFFFF.toInt()),
                jerseyStyle = try {
                    JerseyStyle.valueOf(map["jerseyStyle"] as? String ?: "SOLID")
                } catch (e: Exception) {
                    JerseyStyle.SOLID
                }
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun MatchEntity.toMatch(): Match {
        val playersType = object : TypeToken<List<Player>>() {}.type
        return Match(
            id = id,
            homeTeamName = homeTeamName,
            awayTeamName = awayTeamName,
            homeTeamConfig = homeTeamConfigJson?.let { deserializeTeamConfig(it) },
            awayTeamConfig = awayTeamConfigJson?.let { deserializeTeamConfig(it) },
            homeScore = homeScore,
            awayScore = awayScore,
            homePlayers = homePlayersJson?.let { gson.fromJson(it, playersType) } ?: emptyList(),
            awayPlayers = awayPlayersJson?.let { gson.fromJson(it, playersType) } ?: emptyList(),
            tournamentId = tournamentId,
            tournamentRound = tournamentRound?.let { TournamentRound.valueOf(it) },
            matchNumber = matchNumber,
            isBye = isBye,
            isCompleted = isCompleted,
            playedAt = playedAt,
            createdAt = createdAt
        )
    }

    private fun GoalEntity.toGoal(): Goal = Goal(
        id = id,
        matchId = matchId,
        scorerId = scorerId,
        scorerName = scorerName,
        teamName = teamName,
        isHomeTeam = isHomeTeam,
        minute = minute,
        createdAt = createdAt
    )
}

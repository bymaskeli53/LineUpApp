package com.gundogar.lineupapp.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.gundogar.lineupapp.data.local.dao.GoalDao
import com.gundogar.lineupapp.data.local.dao.MatchDao
import com.gundogar.lineupapp.data.local.dao.TournamentDao
import com.gundogar.lineupapp.data.local.dao.TournamentTeamDao
import com.gundogar.lineupapp.data.local.entity.MatchEntity
import com.gundogar.lineupapp.data.local.entity.TournamentEntity
import com.gundogar.lineupapp.data.local.entity.TournamentTeamEntity
import com.gundogar.lineupapp.data.model.Goal
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.data.model.Match
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.PlayerStatistics
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.model.Tournament
import com.gundogar.lineupapp.data.model.TournamentRound
import com.gundogar.lineupapp.data.model.TournamentStatus
import com.gundogar.lineupapp.data.model.TournamentTeam
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TournamentRepository @Inject constructor(
    private val tournamentDao: TournamentDao,
    private val teamDao: TournamentTeamDao,
    private val matchDao: MatchDao,
    private val goalDao: GoalDao
) {
    private val gson = Gson()

    fun getAllTournaments(): Flow<List<Tournament>> {
        return tournamentDao.getAllTournaments().map { entities ->
            entities.map { loadTournamentDetails(it) }
        }
    }

    fun getActiveTournaments(): Flow<List<Tournament>> {
        return tournamentDao.getActiveTournaments().map { entities ->
            entities.map { loadTournamentDetails(it) }
        }
    }

    suspend fun getTournamentById(id: Long): Tournament? {
        val entity = tournamentDao.getTournamentById(id) ?: return null
        return loadTournamentDetails(entity)
    }

    suspend fun createTournament(name: String, teamCount: Int): Long {
        val startingRound = TournamentRound.forTeamCount(teamCount)
        val entity = TournamentEntity(
            name = name,
            teamCount = teamCount,
            currentRound = startingRound.name,
            status = TournamentStatus.SETUP.name
        )
        return tournamentDao.insertTournament(entity)
    }

    suspend fun addTeamToTournament(
        tournamentId: Long,
        teamName: String,
        teamConfig: TeamConfig? = null,
        players: List<Player> = emptyList()
    ): Long {
        val currentCount = teamDao.getTeamCount(tournamentId)
        val entity = TournamentTeamEntity(
            tournamentId = tournamentId,
            teamName = teamName,
            teamConfigJson = teamConfig?.let { serializeTeamConfig(it) },
            playersJson = if (players.isNotEmpty()) gson.toJson(players) else null,
            seedNumber = currentCount + 1
        )
        val teamId = teamDao.insertTeam(entity)

        // Update tournament's updatedAt to trigger Flow refresh
        val tournament = tournamentDao.getTournamentById(tournamentId)
        if (tournament != null) {
            tournamentDao.updateTournament(tournament.copy(updatedAt = System.currentTimeMillis()))
        }

        return teamId
    }

    suspend fun removeTeamFromTournament(teamId: Long) {
        val team = teamDao.getTeamById(teamId) ?: return
        val tournament = tournamentDao.getTournamentById(team.tournamentId)

        // During SETUP, actually delete the team; during tournament, mark as eliminated
        if (tournament != null && tournament.status == TournamentStatus.SETUP.name) {
            teamDao.deleteTeamById(teamId)
            // Re-number remaining teams
            renumberTeams(team.tournamentId)
        } else {
            teamDao.updateTeam(team.copy(isEliminated = true))
        }

        // Update tournament's updatedAt to trigger Flow refresh
        if (tournament != null) {
            tournamentDao.updateTournament(tournament.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    private suspend fun renumberTeams(tournamentId: Long) {
        val teams = teamDao.getTeamsByTournamentSync(tournamentId)
        teams.forEachIndexed { index, team ->
            if (team.seedNumber != index + 1) {
                teamDao.updateTeam(team.copy(seedNumber = index + 1))
            }
        }
    }

    /**
     * Generates tournament bracket with random pairing.
     * Handles non-power-of-2 team counts by assigning byes to first teams (after shuffle).
     * Byes are assigned deterministically to ensure predictable bracket structure.
     */
    suspend fun generateBracket(tournamentId: Long): Boolean {
        val tournament = tournamentDao.getTournamentById(tournamentId) ?: return false
        val teams = teamDao.getActiveTeams(tournamentId)

        if (teams.size < 2) return false

        val startingRound = TournamentRound.forTeamCount(teams.size)
        val bracketSize = nextPowerOfTwo(teams.size)
        val byesNeeded = bracketSize - teams.size

        // Shuffle teams randomly for fair matchups
        val shuffledTeams = teams.shuffled(Random)

        // Create matches
        val matches = mutableListOf<MatchEntity>()
        var matchNumber = 0

        // First, create bye matches for the first 'byesNeeded' teams
        // These teams automatically advance to the next round
        for (i in 0 until byesNeeded) {
            val team = shuffledTeams[i]
            matches.add(
                MatchEntity(
                    homeTeamName = team.teamName,
                    awayTeamName = "BYE",
                    homeTeamConfigJson = team.teamConfigJson,
                    awayTeamConfigJson = null,
                    homePlayersJson = team.playersJson,
                    awayPlayersJson = null,
                    tournamentId = tournamentId,
                    tournamentRound = startingRound.name,
                    matchNumber = matchNumber++,
                    isBye = true,
                    isCompleted = true,
                    homeScore = 1,
                    awayScore = 0,
                    playedAt = System.currentTimeMillis()
                )
            )
        }

        // Then, create regular matches for the remaining teams
        val remainingTeams = shuffledTeams.drop(byesNeeded)
        for (i in remainingTeams.indices step 2) {
            val homeTeam = remainingTeams[i]
            val awayTeam = remainingTeams.getOrNull(i + 1)

            if (awayTeam != null) {
                matches.add(
                    MatchEntity(
                        homeTeamName = homeTeam.teamName,
                        awayTeamName = awayTeam.teamName,
                        homeTeamConfigJson = homeTeam.teamConfigJson,
                        awayTeamConfigJson = awayTeam.teamConfigJson,
                        homePlayersJson = homeTeam.playersJson,
                        awayPlayersJson = awayTeam.playersJson,
                        tournamentId = tournamentId,
                        tournamentRound = startingRound.name,
                        matchNumber = matchNumber++,
                        isBye = false,
                        isCompleted = false,
                        homeScore = 0,
                        awayScore = 0,
                        playedAt = null
                    )
                )
            }
        }

        matchDao.insertMatches(matches)

        // Update tournament status
        tournamentDao.updateTournament(
            tournament.copy(
                status = TournamentStatus.IN_PROGRESS.name,
                currentRound = startingRound.name,
                updatedAt = System.currentTimeMillis()
            )
        )

        return true
    }

    /**
     * Advances tournament to next round if all matches in current round are complete.
     * Returns false if any match ended in a draw (tournaments require definitive winners).
     */
    suspend fun advanceToNextRound(tournamentId: Long): Boolean {
        val tournament = tournamentDao.getTournamentById(tournamentId) ?: return false
        val currentRound = TournamentRound.valueOf(tournament.currentRound)
        val nextRound = TournamentRound.nextRound(currentRound)

        val currentMatches = matchDao.getMatchesByRound(tournamentId, currentRound.name)

        // Check if all matches complete
        if (currentMatches.any { !it.isCompleted }) {
            return false
        }

        // Check for draws - tournament matches must have a winner
        if (currentMatches.any { !it.isBye && it.homeScore == it.awayScore }) {
            return false
        }

        // If no next round, complete the tournament
        if (nextRound == null) {
            return completeTournament(tournamentId)
        }

        // Get winners
        val winners = currentMatches.map { match ->
            if (match.isBye || match.homeScore > match.awayScore) {
                WinnerInfo(match.homeTeamName, match.homeTeamConfigJson, match.homePlayersJson)
            } else {
                WinnerInfo(match.awayTeamName, match.awayTeamConfigJson, match.awayPlayersJson)
            }
        }

        // Eliminate losers
        currentMatches.forEach { match ->
            if (!match.isBye) {
                val loserName = if (match.homeScore < match.awayScore) {
                    match.homeTeamName
                } else {
                    match.awayTeamName
                }
                teamDao.eliminateTeamByName(tournamentId, loserName)
            }
        }

        // Create next round matches
        val nextMatches = mutableListOf<MatchEntity>()
        var matchNumber = 0

        for (i in winners.indices step 2) {
            val home = winners[i]
            val away = winners.getOrNull(i + 1)

            val isBye = away == null
            nextMatches.add(
                MatchEntity(
                    homeTeamName = home.teamName,
                    awayTeamName = away?.teamName ?: "BYE",
                    homeTeamConfigJson = home.configJson,
                    awayTeamConfigJson = away?.configJson,
                    homePlayersJson = home.playersJson,
                    awayPlayersJson = away?.playersJson,
                    tournamentId = tournamentId,
                    tournamentRound = nextRound.name,
                    matchNumber = matchNumber++,
                    isBye = isBye,
                    isCompleted = isBye,
                    homeScore = if (isBye) 1 else 0,
                    awayScore = 0,
                    playedAt = if (isBye) System.currentTimeMillis() else null
                )
            )
        }

        matchDao.insertMatches(nextMatches)

        tournamentDao.updateTournament(
            tournament.copy(
                currentRound = nextRound.name,
                updatedAt = System.currentTimeMillis()
            )
        )

        return true
    }

    private suspend fun completeTournament(tournamentId: Long): Boolean {
        val tournament = tournamentDao.getTournamentById(tournamentId) ?: return false
        val finalMatch = matchDao.getMatchesByRound(tournamentId, TournamentRound.FINAL.name)
            .firstOrNull()

        if (finalMatch?.isCompleted != true) return false

        // Final must have a definitive winner (no draws)
        if (finalMatch.homeScore == finalMatch.awayScore) return false

        val winnerName = if (finalMatch.homeScore > finalMatch.awayScore) {
            finalMatch.homeTeamName
        } else {
            finalMatch.awayTeamName
        }

        val winnerTeam = teamDao.getActiveTeams(tournamentId).find { it.teamName == winnerName }

        tournamentDao.updateTournament(
            tournament.copy(
                status = TournamentStatus.COMPLETED.name,
                winnerId = winnerTeam?.id,
                winnerName = winnerName,
                updatedAt = System.currentTimeMillis()
            )
        )

        return true
    }

    suspend fun getTopScorers(tournamentId: Long): List<PlayerStatistics> {
        return goalDao.getTopScorersByTournament(tournamentId).map { result ->
            PlayerStatistics(
                playerName = result.scorerName,
                teamName = result.teamName,
                totalGoals = result.goalCount
            )
        }
    }

    suspend fun deleteTournament(tournamentId: Long) {
        tournamentDao.deleteTournamentById(tournamentId)
    }

    // Helper functions
    private fun nextPowerOfTwo(n: Int): Int {
        var power = 1
        while (power < n) power *= 2
        return power
    }

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

    private suspend fun loadTournamentDetails(entity: TournamentEntity): Tournament {
        val teams = teamDao.getTeamsByTournamentSync(entity.id).map { it.toTournamentTeam() }
        val matches = matchDao.getMatchesByTournamentSync(entity.id).map { matchEntity ->
            val goals = goalDao.getGoalsByMatchSync(matchEntity.id).map { goalEntity ->
                Goal(
                    id = goalEntity.id,
                    matchId = goalEntity.matchId,
                    scorerId = goalEntity.scorerId,
                    scorerName = goalEntity.scorerName,
                    teamName = goalEntity.teamName,
                    isHomeTeam = goalEntity.isHomeTeam,
                    minute = goalEntity.minute,
                    createdAt = goalEntity.createdAt
                )
            }
            matchEntity.toMatch().copy(goals = goals)
        }

        return Tournament(
            id = entity.id,
            name = entity.name,
            teamCount = entity.teamCount,
            teams = teams,
            matches = matches,
            currentRound = TournamentRound.valueOf(entity.currentRound),
            status = TournamentStatus.valueOf(entity.status),
            winnerId = entity.winnerId,
            winnerName = entity.winnerName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun TournamentTeamEntity.toTournamentTeam(): TournamentTeam {
        val playersType = object : TypeToken<List<Player>>() {}.type
        return TournamentTeam(
            id = id,
            tournamentId = tournamentId,
            teamName = teamName,
            teamConfig = teamConfigJson?.let { deserializeTeamConfig(it) },
            players = playersJson?.let { gson.fromJson(it, playersType) } ?: emptyList(),
            seedNumber = seedNumber,
            isEliminated = isEliminated
        )
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

    private data class WinnerInfo(
        val teamName: String,
        val configJson: String?,
        val playersJson: String?
    )
}

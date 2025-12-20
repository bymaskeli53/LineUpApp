package com.gundogar.lineupapp.data.repository

import com.gundogar.lineupapp.data.local.dao.GoalDao
import com.gundogar.lineupapp.data.local.dao.MatchDao
import com.gundogar.lineupapp.data.local.dao.TournamentDao
import com.gundogar.lineupapp.data.model.MatchStatistics
import com.gundogar.lineupapp.data.model.PlayerStatistics
import com.gundogar.lineupapp.data.model.TournamentStatistics

class StatisticsRepository(
    private val goalDao: GoalDao,
    private val matchDao: MatchDao,
    private val tournamentDao: TournamentDao
) {

    suspend fun getOverallTopScorers(limit: Int = 10): List<PlayerStatistics> {
        return goalDao.getOverallTopScorers(limit).map { result ->
            PlayerStatistics(
                playerName = result.scorerName,
                teamName = result.teamName,
                totalGoals = result.goalCount
            )
        }
    }

    suspend fun getTournamentTopScorers(tournamentId: Long): List<PlayerStatistics> {
        return goalDao.getTopScorersByTournament(tournamentId).map { result ->
            PlayerStatistics(
                playerName = result.scorerName,
                teamName = result.teamName,
                totalGoals = result.goalCount
            )
        }
    }

    suspend fun getOverallMatchStatistics(): MatchStatistics {
        val completedMatchCount = matchDao.getCompletedMatchCount()
        val topScorers = getOverallTopScorers(5)

        return MatchStatistics(
            totalMatchesPlayed = completedMatchCount,
            topScorers = topScorers
        )
    }

    suspend fun getTournamentStatistics(tournamentId: Long): TournamentStatistics {
        val tournament = tournamentDao.getTournamentById(tournamentId) ?: return TournamentStatistics()
        val matches = matchDao.getMatchesByTournamentSync(tournamentId)
        val topScorers = getTournamentTopScorers(tournamentId)

        val completedMatches = matches.filter { it.isCompleted && !it.isBye }
        val totalGoals = completedMatches.sumOf { it.homeScore + it.awayScore }
        val highestScoringMatch = completedMatches.maxByOrNull { it.homeScore + it.awayScore }

        return TournamentStatistics(
            tournamentName = tournament.name,
            totalTeams = tournament.teamCount,
            matchesPlayed = completedMatches.size,
            totalGoals = totalGoals,
            averageGoalsPerMatch = if (completedMatches.isNotEmpty()) {
                totalGoals.toDouble() / completedMatches.size
            } else {
                0.0
            },
            highestScoringMatchId = highestScoringMatch?.id,
            highestScoringMatchScore = highestScoringMatch?.let { "${it.homeScore} - ${it.awayScore}" },
            topScorers = topScorers,
            winnerName = tournament.winnerName
        )
    }
}

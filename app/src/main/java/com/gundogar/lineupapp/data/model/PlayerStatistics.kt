package com.gundogar.lineupapp.data.model

data class PlayerStatistics(
    val playerName: String,
    val teamName: String,
    val totalGoals: Int,
    val matchesPlayed: Int = 0,
    val goalsPerMatch: Double = 0.0
)

data class TournamentStatistics(
    val tournamentName: String = "",
    val totalTeams: Int = 0,
    val matchesPlayed: Int = 0,
    val totalGoals: Int = 0,
    val averageGoalsPerMatch: Double = 0.0,
    val highestScoringMatchId: Long? = null,
    val highestScoringMatchScore: String? = null,
    val topScorers: List<PlayerStatistics> = emptyList(),
    val winnerName: String? = null
)

data class MatchStatistics(
    val totalMatchesPlayed: Int = 0,
    val topScorers: List<PlayerStatistics> = emptyList()
)

data class ScorerSummary(
    val playerName: String,
    val goalCount: Int
)

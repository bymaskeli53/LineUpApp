package com.gundogar.lineupapp.data.model

data class Match(
    val id: Long = 0,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeTeamConfig: TeamConfig? = null,
    val awayTeamConfig: TeamConfig? = null,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val homePlayers: List<Player> = emptyList(),
    val awayPlayers: List<Player> = emptyList(),
    val goals: List<Goal> = emptyList(),
    val tournamentId: Long? = null,
    val tournamentRound: TournamentRound? = null,
    val matchNumber: Int? = null,
    val isBye: Boolean = false,
    val isCompleted: Boolean = false,
    val playedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isDraw: Boolean
        get() = homeScore == awayScore

    val winner: String?
        get() = when {
            !isCompleted -> null
            isBye -> homeTeamName
            homeScore > awayScore -> homeTeamName
            awayScore > homeScore -> awayTeamName
            else -> null // Draw
        }

    val loser: String?
        get() = when {
            !isCompleted -> null
            isBye -> null
            homeScore > awayScore -> awayTeamName
            awayScore > homeScore -> homeTeamName
            else -> null // Draw
        }
}

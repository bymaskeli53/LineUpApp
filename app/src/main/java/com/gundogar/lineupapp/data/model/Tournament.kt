package com.gundogar.lineupapp.data.model

data class Tournament(
    val id: Long = 0,
    val name: String,
    val teamCount: Int,
    val teams: List<TournamentTeam> = emptyList(),
    val matches: List<Match> = emptyList(),
    val currentRound: TournamentRound = TournamentRound.forTeamCount(teamCount),
    val status: TournamentStatus = TournamentStatus.SETUP,
    val winnerId: Long? = null,
    val winnerName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isSetup: Boolean get() = status == TournamentStatus.SETUP
    val isInProgress: Boolean get() = status == TournamentStatus.IN_PROGRESS
    val isCompleted: Boolean get() = status == TournamentStatus.COMPLETED

    val activeTeams: List<TournamentTeam>
        get() = teams.filter { !it.isEliminated }

    val currentRoundMatches: List<Match>
        get() = matches.filter { it.tournamentRound == currentRound }

    val allCurrentRoundMatchesCompleted: Boolean
        get() = currentRoundMatches.isNotEmpty() && currentRoundMatches.all { it.isCompleted }
}

data class TournamentTeam(
    val id: Long = 0,
    val tournamentId: Long,
    val teamName: String,
    val teamConfig: TeamConfig? = null,
    val players: List<Player> = emptyList(),
    val seedNumber: Int? = null,
    val isEliminated: Boolean = false
)

package com.gundogar.lineupapp.data.model

enum class TournamentRound(val displayName: String, val teamsRequired: Int) {
    ROUND_OF_16("Round of 16", 16),
    QUARTERFINALS("Quarterfinals", 8),
    SEMIFINALS("Semifinals", 4),
    FINAL("Final", 2);

    companion object {
        fun forTeamCount(count: Int): TournamentRound {
            return when {
                count > 8 -> ROUND_OF_16
                count > 4 -> QUARTERFINALS
                count > 2 -> SEMIFINALS
                else -> FINAL
            }
        }

        fun nextRound(current: TournamentRound): TournamentRound? {
            return when (current) {
                ROUND_OF_16 -> QUARTERFINALS
                QUARTERFINALS -> SEMIFINALS
                SEMIFINALS -> FINAL
                FINAL -> null
            }
        }
    }
}

enum class TournamentStatus(val displayName: String) {
    SETUP("Setup"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

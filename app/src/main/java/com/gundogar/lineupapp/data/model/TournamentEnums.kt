package com.gundogar.lineupapp.data.model

import androidx.annotation.StringRes
import com.gundogar.lineupapp.R

enum class TournamentRound(
    @StringRes val displayNameResId: Int,
    val teamsRequired: Int
) {
    ROUND_OF_16(R.string.tournament_round_of_16, 16),
    QUARTERFINALS(R.string.tournament_quarterfinals, 8),
    SEMIFINALS(R.string.tournament_semifinals, 4),
    FINAL(R.string.tournament_final, 2);

    // Fallback for non-UI contexts (database, etc.)
    val displayName: String
        get() = when (this) {
            ROUND_OF_16 -> "Round of 16"
            QUARTERFINALS -> "Quarterfinals"
            SEMIFINALS -> "Semifinals"
            FINAL -> "Final"
        }

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

enum class TournamentStatus(@StringRes val displayNameResId: Int) {
    SETUP(R.string.tournament_status_setup),
    IN_PROGRESS(R.string.tournament_status_in_progress),
    COMPLETED(R.string.tournament_status_completed);

    // Fallback for non-UI contexts
    val displayName: String
        get() = when (this) {
            SETUP -> "Setup"
            IN_PROGRESS -> "In Progress"
            COMPLETED -> "Completed"
        }
}

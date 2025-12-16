package com.gundogar.lineupapp.ui.navigation

sealed class Screen(val route: String) {
    object TeamSizeSelection : Screen("team_size_selection")

    object FormationSelection : Screen("formation_selection")

    object SavedLineups : Screen("saved_lineups")

    object Lineup : Screen("lineup/{formationId}?lineupId={lineupId}&playerCount={playerCount}") {
        fun createRoute(
            formationId: String,
            lineupId: Long? = null,
            playerCount: Int? = null
        ): String {
            val base = "lineup/$formationId"
            val params = mutableListOf<String>()
            lineupId?.let { params.add("lineupId=$it") }
            playerCount?.let { params.add("playerCount=$it") }
            return if (params.isNotEmpty()) {
                "$base?${params.joinToString("&")}"
            } else {
                base
            }
        }
    }
}

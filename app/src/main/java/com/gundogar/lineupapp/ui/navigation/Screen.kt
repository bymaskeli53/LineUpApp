package com.gundogar.lineupapp.ui.navigation

sealed class Screen(val route: String) {
    object FormationSelection : Screen("formation_selection")

    object SavedLineups : Screen("saved_lineups")

    object Lineup : Screen("lineup/{formationId}?lineupId={lineupId}") {
        fun createRoute(formationId: String, lineupId: Long? = null): String {
            return if (lineupId != null) {
                "lineup/$formationId?lineupId=$lineupId"
            } else {
                "lineup/$formationId"
            }
        }
    }
}

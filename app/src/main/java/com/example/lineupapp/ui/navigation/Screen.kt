package com.example.lineupapp.ui.navigation

sealed class Screen(val route: String) {
    object FormationSelection : Screen("formation_selection")

    object Lineup : Screen("lineup/{formationId}") {
        fun createRoute(formationId: String) = "lineup/$formationId"
    }
}

package com.gundogar.lineupapp.ui.navigation

sealed class Screen(val route: String) {
    object OnBoarding : Screen("onboarding")
    object TeamSizeSelection : Screen("team_size_selection")

    object FormationSelection : Screen("formation_selection")

    object SavedLineups : Screen("saved_lineups")

    object NearbyPitches : Screen("nearby_pitches")

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

    // Match screens
    object MatchList : Screen("match_list")
    object CreateMatch : Screen("create_match")
    object MatchScoring : Screen("match_scoring/{matchId}") {
        fun createRoute(matchId: Long): String = "match_scoring/$matchId"
    }

    // Tournament screens
    object TournamentList : Screen("tournament_list")
    object CreateTournament : Screen("create_tournament")
    object TournamentDetail : Screen("tournament_detail/{tournamentId}") {
        fun createRoute(tournamentId: Long): String = "tournament_detail/$tournamentId"
    }
}

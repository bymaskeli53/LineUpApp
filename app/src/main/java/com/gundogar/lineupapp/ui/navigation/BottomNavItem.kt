package com.gundogar.lineupapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.vector.ImageVector
import com.gundogar.lineupapp.R

sealed class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = Screen.TeamSizeSelection.route,
        titleResId = R.string.nav_create,
        icon = Icons.Default.Home
    )

    data object SavedLineups : BottomNavItem(
        route = Screen.SavedLineups.route,
        titleResId = R.string.nav_saved,
        icon = Icons.Default.List
    )

    data object Matches : BottomNavItem(
        route = Screen.MatchList.route,
        titleResId = R.string.nav_matches,
        icon = Icons.Default.DateRange
    )

    data object Tournaments : BottomNavItem(
        route = Screen.TournamentList.route,
        titleResId = R.string.nav_tournaments,
        icon = Icons.Default.Star
    )

    data object NearbyPitches : BottomNavItem(
        route = Screen.NearbyPitches.route,
        titleResId = R.string.nav_pitches,
        icon = Icons.Default.LocationOn
    )
}

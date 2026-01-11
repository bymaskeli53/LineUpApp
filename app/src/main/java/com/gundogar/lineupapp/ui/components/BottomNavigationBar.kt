package com.gundogar.lineupapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gundogar.lineupapp.ui.navigation.BottomNavItem
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import java.util.Locale

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.SavedLineups,
        BottomNavItem.Matches,
        BottomNavItem.Tournaments,
        BottomNavItem.NearbyPitches
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = GrassGreenDark
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = {
                    Text(
                        text = stringResource(item.titleResId),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall // Daha küçük bir font kullanın
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SecondaryGold,
                    selectedTextColor = SecondaryGold,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = GrassGreenDark.copy(alpha = 0.3f)
                )
            )
        }
    }
}

// Helper to determine if bottom nav should be shown
fun shouldShowBottomNav(route: String?): Boolean {
    return route in listOf(
        BottomNavItem.Home.route,
        BottomNavItem.SavedLineups.route,
        BottomNavItem.Matches.route,
        BottomNavItem.Tournaments.route,
        BottomNavItem.NearbyPitches.route
    )
}

@Preview(name = "Small Phone", widthDp = 320)
@Preview(name = "Turkce", locale = "TR", widthDp = 320)
@Preview(name = "Normal Phone", widthDp = 360)
@Preview(name = "Large Phone", widthDp = 411)
@Preview(name = "Tablet", widthDp = 800)
@Preview(name = "Phone", device = Devices.PIXEL_4)
@Preview(name = "Phone Small", device = Devices.PIXEL_2)
@Preview(name = "Tablet Device", device = Devices.PIXEL_TABLET)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Preview(name = "Large Font", fontScale = 1.5f)
@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    LineUpAppTheme {
        BottomNavigationBar(navController = rememberNavController())
    }
}

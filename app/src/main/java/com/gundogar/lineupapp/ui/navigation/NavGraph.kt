package com.gundogar.lineupapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gundogar.lineupapp.ui.screens.formation.FormationSelectionScreen
import com.gundogar.lineupapp.ui.screens.lineup.LineupScreen
import com.gundogar.lineupapp.ui.screens.saved.SavedLineupsScreen
import com.gundogar.lineupapp.ui.screens.teamsize.TeamSizeSelectionScreen

@Composable
fun LineUpNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TeamSizeSelection.route
    ) {
        // Team Size Selection Screen (new entry point)
        composable(
            route = Screen.TeamSizeSelection.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            }
        ) {
            TeamSizeSelectionScreen(
                onTeamSizeSelected = { playerCount ->
                    if (playerCount == 11) {
                        navController.navigate(Screen.FormationSelection.route)
                    } else {
                        navController.navigate(
                            Screen.Lineup.createRoute(
                                formationId = "custom_$playerCount",
                                playerCount = playerCount
                            )
                        )
                    }
                },
                onViewSavedLineups = {
                    navController.navigate(Screen.SavedLineups.route)
                }
            )
        }

        // Formation Selection Screen (only for 11 players)
        composable(
            route = Screen.FormationSelection.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            FormationSelectionScreen(
                onFormationSelected = { formationId ->
                    navController.navigate(Screen.Lineup.createRoute(formationId))
                },
                onViewSavedLineups = {
                    navController.navigate(Screen.SavedLineups.route)
                }
            )
        }

        // Saved Lineups Screen
        composable(
            route = Screen.SavedLineups.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            SavedLineupsScreen(
                onNavigateBack = { navController.popBackStack() },
                onCreateNew = {
                    navController.navigate(Screen.TeamSizeSelection.route) {
                        popUpTo(Screen.TeamSizeSelection.route) { inclusive = true }
                    }
                },
                onOpenLineup = { lineupId ->
                    navController.navigate(Screen.Lineup.createRoute("saved", lineupId))
                },
                onEditLineup = { lineupId ->
                    navController.navigate(Screen.Lineup.createRoute("saved", lineupId))
                }
            )
        }

        // Lineup Screen (for both new and editing saved lineups)
        composable(
            route = Screen.Lineup.route,
            arguments = listOf(
                navArgument("formationId") { type = NavType.StringType },
                navArgument("lineupId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("playerCount") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val formationId = backStackEntry.arguments?.getString("formationId") ?: "442"
            val lineupIdStr = backStackEntry.arguments?.getString("lineupId")
            val lineupId = lineupIdStr?.toLongOrNull()
            val playerCountStr = backStackEntry.arguments?.getString("playerCount")
            val playerCount = playerCountStr?.toIntOrNull()

            LineupScreen(
                formationId = formationId,
                savedLineupId = lineupId,
                playerCount = playerCount,
                onNavigateBack = { navController.popBackStack() },
                onLineupSaved = {
                    navController.navigate(Screen.SavedLineups.route) {
                        popUpTo(Screen.TeamSizeSelection.route)
                    }
                }
            )
        }
    }
}

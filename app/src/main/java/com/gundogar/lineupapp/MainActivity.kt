package com.gundogar.lineupapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gundogar.lineupapp.ui.components.BottomNavigationBar
import com.gundogar.lineupapp.ui.components.shouldShowBottomNav
import com.gundogar.lineupapp.ui.navigation.LineUpNavGraph
import com.gundogar.lineupapp.ui.screens.SplashViewModel
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            LineUpAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoading) {
                        Scaffold(
                            bottomBar = {
                                if (shouldShowBottomNav(currentRoute)) {
                                    BottomNavigationBar(navController = navController)
                                }
                            }
                        ) { paddingValues ->
                            LineUpNavGraph(
                                navController = navController,
                                startDestination = startDestination
                            )
                        }
                    }
                }
            }
        }
    }
}

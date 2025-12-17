package com.gundogar.lineupapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gundogar.lineupapp.data.preferences.OnboardingPreferences
import com.gundogar.lineupapp.ui.navigation.LineUpNavGraph
import com.gundogar.lineupapp.ui.screens.SplashViewModel
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = SplashViewModel(OnboardingPreferences(this))

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

            LineUpAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoading) {
                        LineUpNavGraph(startDestination = startDestination)
                    }
                }
            }
        }
    }
}

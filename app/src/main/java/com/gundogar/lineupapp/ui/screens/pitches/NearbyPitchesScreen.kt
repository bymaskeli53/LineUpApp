package com.gundogar.lineupapp.ui.screens.pitches

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.ui.screens.pitches.components.LocationPermissionContent
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPitchesScreen(
    viewModel: NearbyPitchesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    LaunchedEffect(state.hasLocationPermission) {
        if (state.hasLocationPermission && state.pitches.isEmpty() && !state.isLoading) {
            viewModel.fetchCurrentLocationAndPitches()
        }
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.screen_title_nearby_pitches)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GrassGreenDark,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (!state.hasLocationPermission) {
            LocationPermissionContent(
                onRequestPermission = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Row
                TabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = GrassGreenDark,
                    contentColor = SecondaryGold
                ) {
                    Tab(
                        selected = state.selectedTab == 0,
                        onClick = { viewModel.setSelectedTab(0) },
                        text = { Text(stringResource(R.string.tab_list)) },
                        icon = { Icon(Icons.Default.List, contentDescription = null) },
                        selectedContentColor = SecondaryGold,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f)
                    )
                    Tab(
                        selected = state.selectedTab == 1,
                        onClick = { viewModel.setSelectedTab(1) },
                        text = { Text(stringResource(R.string.tab_map)) },
                        icon = { Icon(Icons.Default.Place, contentDescription = null) },
                        selectedContentColor = SecondaryGold,
                        unselectedContentColor = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Content based on selected tab
                when (state.selectedTab) {
                    0 -> PitchListView(
                        pitches = state.pitches,
                        isLoading = state.isLoading,
                        error = state.error,
                        onRefresh = { viewModel.refresh() }
                    )
                    1 -> PitchMapView(
                        pitches = state.pitches,
                        userLocation = state.userLocation,
                        isLoading = state.isLoading
                    )
                }
            }
        }
    }
}

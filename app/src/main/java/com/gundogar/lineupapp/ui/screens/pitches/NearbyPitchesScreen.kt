package com.gundogar.lineupapp.ui.screens.pitches

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.ui.screens.pitches.components.LocationPermissionContent
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearbyPitchesScreen(
    viewModel: NearbyPitchesViewModel = hiltViewModel()
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
                        isLoading = state.isLoading,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(name = "Small Phone", widthDp = 320, heightDp = 568)
@Preview(name = "Normal Phone", widthDp = 360, heightDp = 640)
@Preview(name = "Large Phone", widthDp = 411, heightDp = 891)
@Preview(name = "Tablet", widthDp = 800, heightDp = 1280)
@Preview(name = "Phone", device = Devices.PIXEL_4)
@Preview(name = "Phone Small", device = Devices.PIXEL_2)
@Preview(name = "Tablet Device", device = Devices.PIXEL_TABLET)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
@Preview(name = "Large Font", fontScale = 1.5f)
@Preview(showBackground = true)
@Composable
private fun NearbyPitchesScreenPreview() {
    LineUpAppTheme {
        NearbyPitchesScreen()
    }
}

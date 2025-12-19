package com.gundogar.lineupapp.ui.screens.pitches

import android.location.Location
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.gundogar.lineupapp.data.model.FootballPitch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PitchMapView(
    pitches: List<FootballPitch>,
    userLocation: Location?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(14.0)
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        AndroidView(
            factory = { mapView },
            update = { map ->
                map.overlays.clear()

                // Center on user location if available
                userLocation?.let { loc ->
                    map.controller.setZoom(16.0)
                    map.controller.setCenter(GeoPoint(loc.latitude, loc.longitude))

                    // Add user location marker
                    val userMarker = Marker(map).apply {
                        position = GeoPoint(loc.latitude, loc.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Your Location"
                        icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
                    }
                    map.overlays.add(userMarker)
                }

                // Add pitch markers
                pitches.forEach { pitch ->
                    val marker = Marker(map).apply {
                        position = GeoPoint(pitch.latitude, pitch.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = pitch.name ?: "Football Pitch"
                        snippet = buildString {
                            pitch.surface?.let { append("Surface: $it") }
                            pitch.distanceMeters?.let {
                                if (isNotEmpty()) append("\n")
                                append("Distance: ${formatDistance(it)}")
                            }
                        }
                    }
                    map.overlays.add(marker)
                }

                map.invalidate()
            },
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun formatDistance(meters: Float): String {
    return if (meters < 1000) {
        "${meters.toInt()}m"
    } else {
        String.format("%.1f km", meters / 1000)
    }
}

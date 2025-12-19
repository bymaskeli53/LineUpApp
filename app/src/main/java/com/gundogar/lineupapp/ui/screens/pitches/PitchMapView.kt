package com.gundogar.lineupapp.ui.screens.pitches

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
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
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.FootballPitch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
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

    // More detailed tile source (OpenStreetMap DE shows more POI details)
    val detailedTileSource = remember {
        object : OnlineTileSourceBase(
            "OpenStreetMap DE",
            0, 19, 256, ".png",
            arrayOf("https://tile.openstreetmap.de/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val zoom = MapTileIndex.getZoom(pMapTileIndex)
                val x = MapTileIndex.getX(pMapTileIndex)
                val y = MapTileIndex.getY(pMapTileIndex)
                return "$baseUrl$zoom/$x/$y$mImageFilenameEnding"
            }
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(detailedTileSource)
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

                    // Add user location marker with label
                    val userMarker = Marker(map).apply {
                        position = GeoPoint(loc.latitude, loc.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = context.getString(R.string.your_location_without_icon)
                        icon = createMarkerWithLabel(
                            context,
                            context.getString(R.string.your_location),
                            Color.parseColor("#4285F4"),
                            Color.WHITE
                        )
                    }
                    map.overlays.add(userMarker)
                }

                // Add pitch markers with text labels
                pitches.forEach { pitch ->
                    val pitchName = pitch.name ?: "Halı Saha"
                    val marker = Marker(map).apply {
                        position = GeoPoint(pitch.latitude, pitch.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = pitchName
                        snippet = buildString {
                            pitch.surface?.let { append("Surface: $it") }
                            pitch.distanceMeters?.let {
                                if (isNotEmpty()) append("\n")
                                append("Distance: ${formatDistance(it)}")
                            }
                        }
                        icon = createMarkerWithLabel(
                            context,
                            "⚽ $pitchName",
                            Color.parseColor("#2E7D32"),
                            Color.WHITE
                        )
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
        String.format(java.util.Locale.getDefault(), "%.1f km", meters / 1000)
    }
}

private fun createMarkerWithLabel(
    context: Context,
    text: String,
    backgroundColor: Int,
    textColor: Int
): BitmapDrawable {
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = 36f
        isFakeBoldText = true
    }

    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    val padding = 16f
    val cornerRadius = 12f
    val textWidth = textPaint.measureText(text)
    val textHeight = textPaint.fontMetrics.let { it.descent - it.ascent }

    val bitmapWidth = (textWidth + padding * 2).toInt()
    val bitmapHeight = (textHeight + padding * 2).toInt()

    val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Draw rounded rectangle background
    val rectF = android.graphics.RectF(0f, 0f, bitmapWidth.toFloat(), bitmapHeight.toFloat())
    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, backgroundPaint)
    canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)

    // Draw text
    val textX = padding
    val textY = padding - textPaint.fontMetrics.ascent
    canvas.drawText(text, textX, textY, textPaint)

    return BitmapDrawable(context.resources, bitmap)
}

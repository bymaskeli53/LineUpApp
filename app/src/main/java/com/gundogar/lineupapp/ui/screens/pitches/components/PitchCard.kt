package com.gundogar.lineupapp.ui.screens.pitches.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gundogar.lineupapp.data.model.FootballPitch
import com.gundogar.lineupapp.ui.theme.GrassGreen
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun PitchCard(pitch: FootballPitch) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pitch.name ?: "Football Pitch",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                pitch.distanceMeters?.let { distance ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = GrassGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDistance(distance),
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrassGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pitch.surface?.let { surface ->
                    AssistChip(
                        onClick = { },
                        label = { Text(formatSurface(surface)) }
                    )
                }

                pitch.lit?.let { hasLighting ->
                    if (hasLighting) {
                        AssistChip(
                            onClick = { },
                            label = { Text("Lit") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SecondaryGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                pitch.access?.let { access ->
                    if (access.isNotBlank()) {
                        AssistChip(
                            onClick = { },
                            label = { Text(access.replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }
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

private fun formatSurface(surface: String): String {
    return when (surface.lowercase()) {
        "grass" -> "Grass"
        "artificial_turf" -> "Artificial Turf"
        "sand" -> "Sand"
        "asphalt" -> "Asphalt"
        "concrete" -> "Concrete"
        else -> surface.replaceFirstChar { it.uppercase() }
    }
}

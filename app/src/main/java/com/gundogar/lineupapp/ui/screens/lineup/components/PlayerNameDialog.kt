package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.PositionRole
import com.gundogar.lineupapp.ui.theme.GrassGreen
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun PlayerNameDialog(
    position: Position,
    currentName: String,
    currentNumber: Int?,
    currentRating: Double?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, number: Int?, rating: Double?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var numberText by remember { mutableStateOf(currentNumber?.toString() ?: "") }
    var rating by remember { mutableFloatStateOf((currentRating ?: 7.5).toFloat()) }
    var ratingEnabled by remember { mutableStateOf(currentRating != null) }

    val positionName = getPositionNameString(position.role)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.player_details),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.player_position_format, positionName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.player_name_label)) },
                    placeholder = { Text(stringResource(R.string.player_name_placeholder)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = numberText,
                        onValueChange = { input ->
                            if (input.isEmpty() || (input.all { it.isDigit() } && input.length <= 2)) {
                                numberText = input
                            }
                        },
                        label = { Text(stringResource(R.string.player_number_label)) },
                        placeholder = { Text(stringResource(R.string.player_number_placeholder)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Section
                Text(
                    text = stringResource(R.string.player_rating),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating Badge
                    RatingBadge(
                        rating = rating.toDouble(),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Rating Slider
                    Column(modifier = Modifier.weight(1f)) {
                        Slider(
                            value = rating,
                            onValueChange = {
                                // Round to 1 decimal place
                                rating = (Math.round(it * 10) / 10f)
                                ratingEnabled = true
                            },
                            valueRange = 0f..10f,
                            colors = SliderDefaults.colors(
                                thumbColor = getRatingColor(rating.toDouble()),
                                activeTrackColor = getRatingColor(rating.toDouble()),
                                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "10",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val number = numberText.toIntOrNull()?.takeIf { it in 1..99 }
                    val finalRating = if (ratingEnabled) rating.toDouble() else null
                    onConfirm(name.trim(), number, finalRating)
                }
            ) {
                Text(
                    text = stringResource(R.string.btn_save),
                    color = SecondaryGold,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}

@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(getRatingColor(rating)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formatRating(rating),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

fun formatRating(rating: Double): String {
    return String.format("%.1f", rating)
}

fun getRatingColor(rating: Double): Color {
    return when {
        rating >= 9.0 -> Color(0xFF2196F3) // Green - Elite
        rating >= 8.0 -> Color(0xFF00ACC1) // Light Green - World Class
        rating >= 7.0 -> Color(0xFF4CAF50) // Yellow - Good
        rating >= 6.0 -> Color(0xFFDCCB0F) // Orange - Average
        rating >= 5.0 -> Color(0xFFFF5722) // Deep Orange - Below Average
        else -> Color(0xFFA71B11) // Red - Poor
    }
}

@Composable
private fun getPositionNameString(role: PositionRole): String {
    return when (role) {
        PositionRole.GOALKEEPER -> stringResource(R.string.position_goalkeeper)
        PositionRole.DEFENDER -> stringResource(R.string.position_defender)
        PositionRole.MIDFIELDER -> stringResource(R.string.position_midfielder)
        PositionRole.FORWARD -> stringResource(R.string.position_forward)
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerNameDialogPreview() {
    LineUpAppTheme {
        PlayerNameDialog(
            position = Position(10, PositionRole.FORWARD, 0.5f, 0.75f),
            currentName = "Ronaldo",
            currentNumber = 10,
            currentRating = 9.3,
            onDismiss = {},
            onConfirm = { _, _, _ -> }
        )
    }
}

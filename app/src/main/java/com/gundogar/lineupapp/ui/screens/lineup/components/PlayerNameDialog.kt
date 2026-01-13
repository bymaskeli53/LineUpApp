package com.gundogar.lineupapp.ui.screens.lineup.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.PositionRole
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import java.io.File

@Composable
fun PlayerNameDialog(
    position: Position,
    currentName: String,
    currentNumber: Int?,
    currentRating: Double?,
    currentImageUri: String?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, number: Int?, rating: Double?, pendingImageUri: Uri?, existingImagePath: String?) -> Unit,
    isNumberAlreadyUsed: (Int) -> Boolean = { false }
) {
    var name by remember { mutableStateOf(currentName) }
    var numberText by remember { mutableStateOf(currentNumber?.toString() ?: "") }
    var rating by remember { mutableFloatStateOf((currentRating ?: 7.5).toFloat()) }
    var ratingEnabled by remember { mutableStateOf(currentRating != null) }
    var selectedImageUri by remember { mutableStateOf(currentImageUri) }
    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageRemoved by remember { mutableStateOf(false) }

    // Check if the entered number is already used by another player
    val isDuplicateNumber = remember(numberText) {
        val number = numberText.toIntOrNull()
        number != null && isNumberAlreadyUsed(number)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            pendingImageUri = it
            imageRemoved = false
        }
    }

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
                modifier = Modifier
                    .padding(top = 8.dp)
                    .heightIn(max = 390.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.player_position_format, positionName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Player Photo Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Image preview or placeholder
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(2.dp, SecondaryGold, CircleShape)
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                pendingImageUri != null -> {
                                    AsyncImage(
                                        model = pendingImageUri,
                                        contentDescription = stringResource(R.string.cd_player_photo),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                                selectedImageUri != null && !imageRemoved -> {
                                    AsyncImage(
                                        model = File(selectedImageUri!!),
                                        contentDescription = stringResource(R.string.cd_player_photo),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = stringResource(R.string.player_add_photo),
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add/Change/Remove photo buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            ) {
                                Text(
                                    text = stringResource(
                                        if (selectedImageUri != null && !imageRemoved || pendingImageUri != null)
                                            R.string.player_change_photo
                                        else
                                            R.string.player_add_photo
                                    ),
                                    color = SecondaryGold,
                                    fontSize = 12.sp
                                )
                            }

                            if ((selectedImageUri != null && !imageRemoved) || pendingImageUri != null) {
                                TextButton(
                                    onClick = {
                                        pendingImageUri = null
                                        imageRemoved = true
                                    }
                                ) {
                                    Text(
                                        text = stringResource(R.string.player_remove_photo),
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

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
                        ),
                        isError = isDuplicateNumber,
                        supportingText = if (isDuplicateNumber) {
                            { Text(stringResource(R.string.player_number_duplicate_error)) }
                        } else null
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
                    val existingPath = if (imageRemoved) null else selectedImageUri
                    onConfirm(name.trim(), number, finalRating, pendingImageUri, existingPath)
                },
                enabled = !isDuplicateNumber
            ) {
                Text(
                    text = stringResource(R.string.btn_save),
                    color = if (isDuplicateNumber) Color.Gray else SecondaryGold,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        },

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
@Preview(name = "Small Phone", widthDp = 320)
@Composable
private fun PlayerNameDialogPreview() {
    LineUpAppTheme {
        PlayerNameDialog(
            position = Position(10, PositionRole.FORWARD, 0.5f, 0.75f),
            currentName = "Ronaldo",
            currentNumber = 10,
            currentRating = 9.3,
            currentImageUri = null,
            onDismiss = {},
            onConfirm = { _, _, _, _, _ -> }
        )
    }
}

@Preview(name = "Keyboard Open - Small Phone", widthDp = 320, heightDp = 350)
@Preview(name = "Keyboard Open - Normal Phone", widthDp = 360, heightDp = 400)
@Composable
private fun PlayerNameDialogKeyboardOpenPreview() {
    LineUpAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            PlayerNameDialog(
                position = Position(10, PositionRole.FORWARD, 0.5f, 0.75f),
                currentName = "Ronaldo",
                currentNumber = 10,
                currentRating = 9.3,
                currentImageUri = null,
                onDismiss = {},
                onConfirm = { _, _, _, _, _ -> }
            )
        }
    }
}

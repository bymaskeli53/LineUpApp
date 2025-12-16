package com.gundogar.lineupapp.ui.screens.lineup

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gundogar.lineupapp.ui.screens.customization.TeamCustomizationSheet
import com.gundogar.lineupapp.ui.screens.lineup.components.DraggablePlayerJersey
import com.gundogar.lineupapp.ui.screens.lineup.components.FootballPitch
import com.gundogar.lineupapp.ui.screens.lineup.components.PlayerJersey
import com.gundogar.lineupapp.ui.screens.lineup.components.PlayerNameDialog
import com.gundogar.lineupapp.ui.theme.GrassGreen
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import com.gundogar.lineupapp.util.ShareUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineupScreen(
    formationId: String,
    savedLineupId: Long? = null,
    playerCount: Int? = null,
    onNavigateBack: () -> Unit,
    onLineupSaved: () -> Unit = {},
    viewModel: LineupViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val graphicsLayer = rememberGraphicsLayer()
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(formationId, savedLineupId, playerCount) {
        when {
            savedLineupId != null -> viewModel.loadSavedLineup(savedLineupId)
            playerCount != null && playerCount in 5..10 -> viewModel.loadCustomLayout(playerCount)
            else -> viewModel.loadFormation(formationId)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.teamConfig.teamName,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GrassGreenDark,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SecondaryGold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GrassGreenDark, GrassGreen, GrassGreenDark)
                        )
                    )
            ) {
                // Pitch with players
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val pitchWidth = maxWidth
                        val pitchHeight = pitchWidth / 0.65f

                        Box(
                            modifier = Modifier
                                .width(pitchWidth)
                                .height(if (pitchHeight > maxHeight) maxHeight else pitchHeight)
                        ) {
                            // Draw the pitch
                            FootballPitch(
                                modifier = Modifier.fillMaxSize()
                            )

                            // Position players on the pitch
                            BoxWithConstraints(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val pitchWidthPx = maxWidth.value * LocalDensity.current.density
                                val pitchHeightPx = maxHeight.value * LocalDensity.current.density

                                state.effectivePositions.forEach { position ->
                                    val player = state.players[position.id]
                                    val xOffset = (position.xPercent * maxWidth.value - 24).dp
                                    val yOffset = ((1f - position.yPercent) * maxHeight.value - 30).dp

                                    if (state.isCustomizable) {
                                        DraggablePlayerJersey(
                                            position = position,
                                            player = player,
                                            teamConfig = state.teamConfig,
                                            pitchWidthPx = pitchWidthPx,
                                            pitchHeightPx = pitchHeightPx,
                                            onPositionDrag = { positionId, newXPercent, newYPercent ->
                                                viewModel.updatePositionCoordinates(positionId, newXPercent, newYPercent)
                                            },
                                            onClick = { viewModel.onPlayerClick(position) },
                                            modifier = Modifier.offset {
                                                IntOffset(xOffset.roundToPx(), yOffset.roundToPx())
                                            }
                                        )
                                    } else {
                                        PlayerJersey(
                                            position = position,
                                            player = player,
                                            teamConfig = state.teamConfig,
                                            onClick = { viewModel.onPlayerClick(position) },
                                            modifier = Modifier.offset {
                                                IntOffset(xOffset.roundToPx(), yOffset.roundToPx())
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom action bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GrassGreenDark.copy(alpha = 0.9f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.showCustomizationSheet() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryGold,
                            contentColor = GrassGreenDark
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Customize", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.saveLineupToDatabase(onSuccess = onLineupSaved)
                        },
                        enabled = !state.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = GrassGreenDark
                        )
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(20.dp).width(20.dp),
                                strokeWidth = 2.dp,
                                color = GrassGreenDark
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.savedLineupId != null) "Update" else "Save",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                ShareUtil.shareLineupImage(context, bitmap, state.teamConfig.teamName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Player name dialog
        if (state.showPlayerDialog && state.selectedPosition != null) {
            val currentPlayer = state.players[state.selectedPosition!!.id]
            PlayerNameDialog(
                position = state.selectedPosition!!,
                currentName = currentPlayer?.name ?: "",
                currentNumber = currentPlayer?.number,
                currentRating = currentPlayer?.rating,
                onDismiss = { viewModel.onPlayerDialogDismiss() },
                onConfirm = { name, number, rating ->
                    viewModel.onPlayerSave(name, number, rating)
                }
            )
        }

        // Team customization bottom sheet
        if (state.showCustomizationSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.hideCustomizationSheet() },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                TeamCustomizationSheet(
                    teamConfig = state.teamConfig,
                    onTeamConfigChange = { viewModel.updateTeamConfig(it) },
                    onDismiss = { viewModel.hideCustomizationSheet() }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LineupScreenPreview() {
    LineUpAppTheme {
        LineupScreen(
            formationId = "442",
            onNavigateBack = {}
        )
    }
}

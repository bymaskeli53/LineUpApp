package com.gundogar.lineupapp.ui.screens.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.ui.screens.match.components.TeamImportDialog
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(
    viewModel: CreateMatchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onMatchCreated: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showHomeImportDialog by remember { mutableStateOf(false) }
    var showAwayImportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.createdMatchId) {
        state.createdMatchId?.let { matchId ->
            onMatchCreated(matchId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_title_create_match)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrassGreenDark,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrassGreenDark)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Home Team Card
            TeamInputCard(
                title = stringResource(R.string.match_home_team),
                teamName = state.homeTeamName,
                onTeamNameChange = viewModel::setHomeTeamName,
                onImportClick = { showHomeImportDialog = true },
                hasImportedPlayers = state.homePlayers.isNotEmpty()
            )

            Text(
                text = stringResource(R.string.match_vs),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Away Team Card
            TeamInputCard(
                title = stringResource(R.string.match_away_team),
                teamName = state.awayTeamName,
                onTeamNameChange = viewModel::setAwayTeamName,
                onImportClick = { showAwayImportDialog = true },
                hasImportedPlayers = state.awayPlayers.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Match Button
            Button(
                onClick = { viewModel.createMatch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = viewModel.isValid() && !state.isCreating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SecondaryGold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.match_create),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Import dialogs
    if (showHomeImportDialog) {
        TeamImportDialog(
            onDismiss = { showHomeImportDialog = false },
            onTeamSelected = { config, players ->
                viewModel.setHomeTeam(config, players)
                showHomeImportDialog = false
            }
        )
    }

    if (showAwayImportDialog) {
        TeamImportDialog(
            onDismiss = { showAwayImportDialog = false },
            onTeamSelected = { config, players ->
                viewModel.setAwayTeam(config, players)
                showAwayImportDialog = false
            }
        )
    }
}

@Composable
private fun TeamInputCard(
    title: String,
    teamName: String,
    onTeamNameChange: (String) -> Unit,
    onImportClick: () -> Unit,
    hasImportedPlayers: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryGold
            )

            OutlinedTextField(
                value = teamName,
                onValueChange = onTeamNameChange,
                label = { Text(stringResource(R.string.match_team_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryGold,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedLabelColor = SecondaryGold,
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    cursorColor = SecondaryGold,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            OutlinedButton(
                onClick = onImportClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (hasImportedPlayers) SecondaryGold else Color.White
                )
            ) {
                Text(
                    text = if (hasImportedPlayers) {
                        "Players Imported"
                    } else {
                        stringResource(R.string.match_import_lineup)
                    }
                )
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
private fun CreateMatchScreenPreview() {
    LineUpAppTheme {
        CreateMatchScreen(
            onNavigateBack = {},
            onMatchCreated = {}
        )
    }
}

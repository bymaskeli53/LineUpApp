package com.gundogar.lineupapp.ui.screens.tournament

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Tournament
import com.gundogar.lineupapp.data.model.TournamentStatus
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListScreen(
    viewModel: TournamentListViewModel = hiltViewModel(),
    onCreateTournament: () -> Unit,
    onTournamentClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.screen_title_tournaments)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GrassGreenDark,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTournament,
                containerColor = SecondaryGold,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.screen_title_create_tournament))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrassGreenDark)
                .padding(paddingValues)
        ) {
            if (state.tournaments.isEmpty() && !state.isLoading) {
                EmptyTournamentsContent()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(state.tournaments) { tournament ->
                        TournamentCard(
                            tournament = tournament,
                            onClick = { onTournamentClick(tournament.id) },
                            onDelete = { viewModel.deleteTournament(tournament.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyTournamentsContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.tournament_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tournament_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TournamentCard(
    tournament: Tournament,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = SecondaryGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = tournament.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Status badge
                val statusColor = when (tournament.status) {
                    TournamentStatus.SETUP -> Color.Gray
                    TournamentStatus.IN_PROGRESS -> SecondaryGold
                    TournamentStatus.COMPLETED -> Color.Green.copy(alpha = 0.7f)
                }
                val statusText = when (tournament.status) {
                    TournamentStatus.SETUP -> stringResource(R.string.tournament_status_setup)
                    TournamentStatus.IN_PROGRESS -> stringResource(R.string.tournament_status_in_progress)
                    TournamentStatus.COMPLETED -> stringResource(R.string.tournament_status_completed)
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )

                Text(
                    text = stringResource(R.string.tournament_teams_count, tournament.teams.size, tournament.teamCount),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Show current round for in-progress tournaments
            if (tournament.status == TournamentStatus.IN_PROGRESS) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(tournament.currentRound.displayNameResId),
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryGold
                )
            }

            // Show winner for completed tournaments
            tournament.winnerName?.let { winner ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.tournament_winner, winner),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryGold,
                    fontWeight = FontWeight.Bold
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
private fun TournamentListScreenPreview() {
    LineUpAppTheme {
        TournamentListScreen(
            onCreateTournament = {},
            onTournamentClick = {}
        )
    }
}

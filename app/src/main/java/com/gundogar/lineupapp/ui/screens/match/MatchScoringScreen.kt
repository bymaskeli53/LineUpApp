package com.gundogar.lineupapp.ui.screens.match

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Goal
import com.gundogar.lineupapp.data.model.Match
import com.gundogar.lineupapp.ui.screens.match.components.PlayerSelectorDialog
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScoringScreen(
    viewModel: MatchScoringViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val match = state.match

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.screen_title_match_scoring)) },
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
        if (match == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GrassGreenDark)
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = Color.White)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GrassGreenDark)
                    .padding(paddingValues)
            ) {
                // Scoreboard
                ScoreBoard(
                    match = match,
                    onHomeClick = { viewModel.showScorerDialog(true) },
                    onAwayClick = { viewModel.showScorerDialog(false) },
                    isCompleted = match.isCompleted
                )

                // No-draw warning for knockout matches
                if (state.isKnockout && match.homeScore == match.awayScore && !match.isCompleted) {
                    NoDrawWarning()
                }

                // Goals list
                GoalsList(
                    goals = state.goals,
                    homeTeamName = match.homeTeamName,
                    awayTeamName = match.awayTeamName,
                    onRemoveGoal = { viewModel.removeGoal(it) },
                    isCompleted = match.isCompleted
                )

                Spacer(modifier = Modifier.weight(1f))

                // Complete Match Button
                if (!match.isCompleted) {
                    Button(
                        onClick = { viewModel.completeMatch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        enabled = viewModel.canComplete(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryGold,
                            contentColor = Color.Black,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.match_complete),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // Player selector dialog
    if (state.showScorerDialog && match != null) {
        val players = if (state.selectedTeamIsHome) match.homePlayers else match.awayPlayers
        val teamName = if (state.selectedTeamIsHome) match.homeTeamName else match.awayTeamName

        PlayerSelectorDialog(
            teamName = teamName,
            players = players,
            onDismiss = { viewModel.hideScorerDialog() },
            onPlayerSelected = { player, minute ->
                viewModel.addGoal(player.positionId, player.name, minute)
            },
            onManualEntry = { scorerName, minute ->
                viewModel.addGoal(0, scorerName, minute)
            }
        )
    }
}

@Composable
private fun ScoreBoard(
    match: Match,
    onHomeClick: () -> Unit,
    onAwayClick: () -> Unit,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home team
            TeamScoreSection(
                teamName = match.homeTeamName,
                score = match.homeScore,
                onClick = onHomeClick,
                isClickable = !isCompleted,
                modifier = Modifier.weight(1f)
            )

            // VS divider
            Text(
                text = "-",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White.copy(alpha = 0.5f)
            )

            // Away team
            TeamScoreSection(
                teamName = match.awayTeamName,
                score = match.awayScore,
                onClick = onAwayClick,
                isClickable = !isCompleted,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TeamScoreSection(
    teamName: String,
    score: Int,
    onClick: () -> Unit,
    isClickable: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = if (isClickable) SecondaryGold.copy(alpha = 0.2f) else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$score",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (isClickable) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = SecondaryGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.match_add_goal),
                    style = MaterialTheme.typography.labelSmall,
                    color = SecondaryGold
                )
            }
        }
    }
}

@Composable
private fun NoDrawWarning() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFA000).copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFFA000),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.match_no_draw_warning),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFA000)
            )
        }
    }
}

@Composable
private fun GoalsList(
    goals: List<Goal>,
    homeTeamName: String,
    awayTeamName: String,
    onRemoveGoal: (Goal) -> Unit,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.match_goals),
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (goals.isEmpty()) {
                Text(
                    text = stringResource(R.string.match_no_goals),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height((goals.size * 56).coerceAtMost(300).dp)
                ) {
                    items(goals) { goal ->
                        GoalItem(
                            goal = goal,
                            isHomeGoal = goal.isHomeTeam,
                            onRemove = { onRemoveGoal(goal) },
                            canRemove = !isCompleted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalItem(
    goal: Goal,
    isHomeGoal: Boolean,
    onRemove: () -> Unit,
    canRemove: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = if (isHomeGoal) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isHomeGoal) {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (canRemove && isHomeGoal) {
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.btn_delete),
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Icon(
            Icons.Default.SportsSoccer,
            contentDescription = null,
            tint = if (isHomeGoal) SecondaryGold else Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = goal.scorerName,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            goal.minute?.let { minute ->
                Text(
                    text = "$minute'",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        if (canRemove && !isHomeGoal) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.btn_delete),
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        if (!isHomeGoal) {
            // No need for additional spacer
        } else {
            Spacer(modifier = Modifier.weight(1f))
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
private fun MatchScoringScreenPreview() {
    LineUpAppTheme {
        MatchScoringScreen(
            onNavigateBack = {}
        )
    }
}

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Match
import com.gundogar.lineupapp.data.model.PlayerStatistics
import com.gundogar.lineupapp.data.model.TournamentStatus
import com.gundogar.lineupapp.data.model.TournamentTeam
import com.gundogar.lineupapp.ui.screens.tournament.components.AddTeamDialog
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    viewModel: TournamentDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onMatchClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val tournament = state.tournament

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tournament?.name ?: stringResource(R.string.screen_title_tournament_detail)) },
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
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrassGreenDark)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.loading), color = Color.White)
                }
            }
            state.error != null -> {
                val errorMessage = state.error ?: ""
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrassGreenDark)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryGold,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(stringResource(R.string.btn_go_back))
                        }
                    }
                }
            }
            tournament == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrassGreenDark)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.tournament_error_not_found), color = Color.White)
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(GrassGreenDark)
                        .padding(paddingValues)
                ) {
                    // Status header
                    StatusHeader(
                        status = tournament.status,
                        currentRound = tournament.currentRound,
                        winnerName = tournament.winnerName
                    )

                    // Tabs
                    val tabs = listOf(
                        stringResource(R.string.tournament_tab_teams),
                        stringResource(R.string.tournament_tab_bracket),
                        stringResource(R.string.tournament_tab_stats)
                    )

                    TabRow(
                        selectedTabIndex = state.selectedTab,
                        containerColor = GrassGreenDark,
                        contentColor = Color.White,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedTab]),
                                color = SecondaryGold
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = state.selectedTab == index,
                                onClick = { viewModel.setSelectedTab(index) },
                                text = {
                                    Text(
                                        text = title,
                                        color = if (state.selectedTab == index) SecondaryGold else Color.White.copy(alpha = 0.7f)
                                    )
                                }
                            )
                        }
                    }

                    // Tab content - wrap in Box with weight to leave room for buttons
                    Box(modifier = Modifier.weight(1f)) {
                        when (state.selectedTab) {
                            0 -> TeamsTab(
                                teams = tournament.teams,
                                canAddTeam = tournament.status == TournamentStatus.SETUP && tournament.teams.size < tournament.teamCount,
                                canRemoveTeam = tournament.status == TournamentStatus.SETUP,
                                onAddTeam = { viewModel.showAddTeamDialog() },
                                onRemoveTeam = { viewModel.removeTeam(it) }
                            )
                            1 -> BracketTab(
                                matches = tournament.matches,
                                currentRound = tournament.currentRound,
                                onMatchClick = onMatchClick
                            )
                            2 -> StatsTab(topScorers = state.topScorers)
                        }
                    }

                    // Draws warning
                    if (state.hasDraws) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.tournament_draws_warning),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Action buttons - compute canStart directly from tournament data
                    val activeTeams = tournament.teams.filter { !it.isEliminated }
                    val canStartTournament = tournament.status == TournamentStatus.SETUP && activeTeams.size >= 2

                    if (canStartTournament) {
                        ActionButton(
                            text = stringResource(R.string.tournament_start),
                            icon = Icons.Default.PlayArrow,
                            onClick = { viewModel.startTournament() }
                        )
                    } else if (tournament.status == TournamentStatus.SETUP) {
                        // Show hint when not enough teams
                        val teamsNeeded = 2 - activeTeams.size
                        if (teamsNeeded > 0) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.tournament_need_teams, teamsNeeded),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }

                    if (state.canAdvance) {
                        ActionButton(
                            text = stringResource(R.string.tournament_advance_round),
                            icon = Icons.Default.ArrowForward,
                            onClick = { viewModel.advanceRound() }
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // Add team dialog
    if (state.showAddTeamDialog) {
        AddTeamDialog(
            onDismiss = { viewModel.hideAddTeamDialog() },
            onTeamAdded = { name, config, players ->
                viewModel.addTeam(name, config, players)
            }
        )
    }
}

@Composable
private fun StatusHeader(
    status: TournamentStatus,
    currentRound: com.gundogar.lineupapp.data.model.TournamentRound,
    winnerName: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        if (winnerName != null) {
            // ŞAMPİYONLUK BÖLÜMÜ - Bir karttan ziyade bir "başarı sertifikası" gibi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Sol tarafta kalın bir altın şerit
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(40.dp)
                        .background(SecondaryGold, RoundedCornerShape(2.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = stringResource(R.string.tournament_completed),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Light
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = winnerName.uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = SecondaryGold,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = SecondaryGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        } else {
            // DEVAM EDEN TURNUVA - Skor tabelası estetiği
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.curren_status),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = stringResource(currentRound.displayNameResId),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }

                // Sağ tarafa durum göstergesi (Daha teknik bir görünüm)
                Column(horizontalAlignment = Alignment.End) {
                    val statusColor = when (status) {
                        TournamentStatus.SETUP -> Color.Gray
                        TournamentStatus.IN_PROGRESS -> SecondaryGold
                        TournamentStatus.COMPLETED -> Color.Green
                    }

                    // Küçük bir canlılık noktası ve metin
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(statusColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(status.displayNameResId),
                            style = MaterialTheme.typography.bodyMedium,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamsTab(
    teams: List<TournamentTeam>,
    canAddTeam: Boolean,
    canRemoveTeam: Boolean,
    onAddTeam: () -> Unit,
    onRemoveTeam: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(teams.filter { !it.isEliminated }) { team ->
            TeamItem(
                team = team,
                canRemove = canRemoveTeam,
                onRemove = { onRemoveTeam(team.id) }
            )
        }

        if (canAddTeam) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onAddTeam),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SecondaryGold.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = SecondaryGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.tournament_add_team),
                            color = SecondaryGold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TeamItem(
    team: TournamentTeam,
    canRemove: Boolean,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(SecondaryGold.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${team.seedNumber ?: ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryGold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = team.teamName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    if (team.players.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.tournament_players_count, team.players.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            if (canRemove) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.btn_delete),
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BracketTab(
    matches: List<Match>,
    currentRound: com.gundogar.lineupapp.data.model.TournamentRound,
    onMatchClick: (Long) -> Unit
) {
    if (matches.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.tournament_bracket_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Group matches by round
            val matchesByRound = matches.groupBy { it.tournamentRound }

            matchesByRound.entries.sortedByDescending { it.key?.teamsRequired ?: 0 }.forEach { (round, roundMatches) ->
                item {
                    Text(
                        text = round?.let { stringResource(it.displayNameResId) } ?: stringResource(R.string.tournament_round_fallback),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (round == currentRound) SecondaryGold else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(roundMatches) { match ->
                    BracketMatchItem(
                        match = match,
                        isCurrentRound = round == currentRound,
                        onClick = { if (!match.isBye && !match.isCompleted) onMatchClick(match.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun BracketMatchItem(
    match: Match,
    isCurrentRound: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = isCurrentRound && !match.isBye && !match.isCompleted,
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                match.isCompleted -> Color.White.copy(alpha = 0.05f)
                isCurrentRound -> Color.White.copy(alpha = 0.15f)
                else -> Color.White.copy(alpha = 0.08f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Home team
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.homeTeamName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (match.isCompleted && match.homeScore > match.awayScore) SecondaryGold else Color.White,
                    fontWeight = if (match.isCompleted && match.homeScore > match.awayScore) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${match.homeScore}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Away team
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = match.awayTeamName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (match.isCompleted && match.awayScore > match.homeScore) SecondaryGold
                           else if (match.isBye) Color.White.copy(alpha = 0.4f)
                           else Color.White,
                    fontWeight = if (match.isCompleted && match.awayScore > match.homeScore) FontWeight.Bold else FontWeight.Normal
                )
                if (!match.isBye) {
                    Text(
                        text = "${match.awayScore}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (match.isBye) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.tournament_bye),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun StatsTab(topScorers: List<PlayerStatistics>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                text = stringResource(R.string.tournament_golden_boot),
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryGold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (topScorers.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.tournament_no_goals),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        } else {
            itemsIndexed(topScorers) { index, scorer ->
                TopScorerItem(
                    rank = index + 1,
                    scorer = scorer
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TopScorerItem(
    rank: Int,
    scorer: PlayerStatistics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    color = when (rank) {
                        1 -> SecondaryGold
                        2 -> Color(0xFFC0C0C0)
                        3 -> Color(0xFFCD7F32)
                        else -> Color.White.copy(alpha = 0.7f)
                    },
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = scorer.playerName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = scorer.teamName,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = SecondaryGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${scorer.totalGoals}",
                    style = MaterialTheme.typography.titleMedium,
                    color = SecondaryGold,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SecondaryGold,
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

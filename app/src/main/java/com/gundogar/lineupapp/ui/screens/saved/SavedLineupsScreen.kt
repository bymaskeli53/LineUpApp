package com.gundogar.lineupapp.ui.screens.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.ui.screens.saved.components.SavedLineupCard
import com.gundogar.lineupapp.ui.theme.GrassGreen
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLineupsScreen(
    onNavigateBack: () -> Unit,
    onCreateNew: () -> Unit,
    onOpenLineup: (Long) -> Unit,
    onEditLineup: (Long) -> Unit,
    viewModel: SavedLineupsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.screen_title_my_lineups),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GrassGreenDark,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateNew,
                containerColor = SecondaryGold,
                contentColor = GrassGreenDark
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_create_new_lineup)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GrassGreenDark, GrassGreen, GrassGreenDark)
                    )
                )
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SecondaryGold)
                    }
                }

                state.savedLineups.isEmpty() -> {
                    EmptyState(onCreateNew = onCreateNew)
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.savedLineups,
                            key = { it.id }
                        ) { lineup ->
                            SavedLineupCard(
                                lineup = lineup,
                                onClick = { onOpenLineup(lineup.id) },
                                onEditClick = { onEditLineup(lineup.id) },
                                onDeleteClick = { viewModel.showDeleteDialog(lineup) }
                            )
                        }

                        // Bottom spacing for FAB
                        item {
                            Spacer(modifier = Modifier.height(72.dp))
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (state.showDeleteDialog && state.lineupToDelete != null) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteDialog() },
                title = {
                    Text(
                        text = stringResource(R.string.saved_lineups_delete_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(stringResource(R.string.saved_lineups_delete_message, state.lineupToDelete?.teamName ?: ""))
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.deleteLineup() }
                    ) {
                        Text(
                            text = stringResource(R.string.btn_delete),
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyState(onCreateNew: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.White.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.saved_lineups_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.saved_lineups_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedLineupsScreenPreview() {
    LineUpAppTheme {
        SavedLineupsScreen(
            onNavigateBack = {},
            onCreateNew = {},
            onOpenLineup = {},
            onEditLineup = {}
        )
    }
}

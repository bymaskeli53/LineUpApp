package com.gundogar.lineupapp.ui.screens.match.components

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.local.entity.SavedLineupEntity
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamImportViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LineupDatabase.getDatabase(application)
    private val _lineups = MutableStateFlow<List<SavedLineupEntity>>(emptyList())
    val lineups: StateFlow<List<SavedLineupEntity>> = _lineups

    init {
        loadLineups()
    }

    private fun loadLineups() {
        viewModelScope.launch {
            database.savedLineupDao().getAllLineups().collect {
                _lineups.value = it
            }
        }
    }
}

@Composable
fun TeamImportDialog(
    viewModel: TeamImportViewModel = viewModel(),
    onDismiss: () -> Unit,
    onTeamSelected: (TeamConfig?, List<Player>) -> Unit
) {
    val lineups by remember { viewModel.lineups }.let {
        var state by remember { mutableStateOf<List<SavedLineupEntity>>(emptyList()) }
        LaunchedEffect(Unit) {
            viewModel.lineups.collect { state = it }
        }
        mutableStateOf(state)
    }

    val gson = remember { Gson() }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = GrassGreenDark
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.match_import_lineup),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (lineups.isEmpty()) {
                    Text(
                        text = stringResource(R.string.saved_lineups_empty_title),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(lineups) { lineup ->
                            LineupImportItem(
                                lineup = lineup,
                                onClick = {
                                    val playersType = object : TypeToken<List<Player>>() {}.type
                                    val players: List<Player> = try {
                                        gson.fromJson(lineup.playersJson, playersType)
                                    } catch (e: Exception) {
                                        emptyList()
                                    }

                                    val teamConfig = TeamConfig(
                                        teamName = lineup.teamName,
                                        primaryColor = androidx.compose.ui.graphics.Color(lineup.primaryColor.toInt()),
                                        secondaryColor = androidx.compose.ui.graphics.Color(lineup.secondaryColor.toInt()),
                                        jerseyStyle = try {
                                            JerseyStyle.valueOf(lineup.jerseyStyle)
                                        } catch (e: Exception) {
                                            JerseyStyle.SOLID
                                        }
                                    )

                                    onTeamSelected(teamConfig, players)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.btn_cancel), color = Color.White.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
private fun LineupImportItem(
    lineup: SavedLineupEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lineup.teamName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lineup.formationId,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
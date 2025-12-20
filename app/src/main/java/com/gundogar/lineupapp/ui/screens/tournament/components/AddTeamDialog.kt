package com.gundogar.lineupapp.ui.screens.tournament.components

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

class AddTeamDialogViewModel(application: Application) : AndroidViewModel(application) {
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
fun AddTeamDialog(
    viewModel: AddTeamDialogViewModel = viewModel(),
    onDismiss: () -> Unit,
    onTeamAdded: (String, TeamConfig?, List<Player>) -> Unit
) {
    var showManualEntry by remember { mutableStateOf(false) }
    var manualTeamName by remember { mutableStateOf("") }
    var lineups by remember { mutableStateOf<List<SavedLineupEntity>>(emptyList()) }

    val gson = remember { Gson() }

    LaunchedEffect(Unit) {
        viewModel.lineups.collect { lineups = it }
    }

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
                    text = stringResource(R.string.tournament_add_team),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (showManualEntry) {
                    // Manual entry mode
                    OutlinedTextField(
                        value = manualTeamName,
                        onValueChange = { manualTeamName = it },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { showManualEntry = false }) {
                            Text(stringResource(R.string.tournament_add_team_import), color = SecondaryGold)
                        }

                        Button(
                            onClick = {
                                if (manualTeamName.isNotBlank()) {
                                    onTeamAdded(manualTeamName, null, emptyList())
                                }
                            },
                            enabled = manualTeamName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryGold,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(stringResource(R.string.btn_apply))
                        }
                    }
                } else {
                    // Option buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showManualEntry = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text(stringResource(R.string.tournament_add_team_manual))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.tournament_add_team_import),
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (lineups.isEmpty()) {
                        Text(
                            text = stringResource(R.string.saved_lineups_empty_title),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(lineups) { lineup ->
                                LineupItem(
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

                                        onTeamAdded(lineup.teamName, teamConfig, players)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

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
private fun LineupItem(
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = lineup.teamName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
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

package com.gundogar.lineupapp.ui.screens.match.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun PlayerSelectorDialog(
    teamName: String,
    players: List<Player>,
    onDismiss: () -> Unit,
    onPlayerSelected: (Player, Int?) -> Unit,
    onManualEntry: (String, Int?) -> Unit
) {
    var showManualEntry by remember { mutableStateOf(players.isEmpty()) }
    var manualName by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

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
                    text = stringResource(R.string.match_select_scorer),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = teamName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryGold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Minute input
                OutlinedTextField(
                    value = minute,
                    onValueChange = { minute = it.filter { c -> c.isDigit() }.take(3) },
                    label = { Text(stringResource(R.string.match_goal_minute)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                if (showManualEntry || players.isEmpty()) {
                    // Manual entry mode
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = { Text(stringResource(R.string.player_name_label)) },
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
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (players.isNotEmpty()) {
                            TextButton(onClick = { showManualEntry = false }) {
                                Text("Select from list", color = SecondaryGold)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (manualName.isNotBlank()) {
                                    onManualEntry(manualName, minute.toIntOrNull())
                                }
                            },
                            enabled = manualName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryGold,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(stringResource(R.string.match_add_goal))
                        }
                    }
                } else {
                    // Player list mode
                    LazyColumn(
                        modifier = Modifier.height(250.dp)
                    ) {
                        items(players) { player ->
                            PlayerListItem(
                                player = player,
                                onClick = { onPlayerSelected(player, minute.toIntOrNull()) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { showManualEntry = true },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Enter name manually", color = SecondaryGold)
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
private fun PlayerListItem(
    player: Player,
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
            Text(
                text = "#${player.number}",
                style = MaterialTheme.typography.titleMedium,
                color = SecondaryGold,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                player.number?.let { num ->
                    Text(
                        text = "Position ${player.positionId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

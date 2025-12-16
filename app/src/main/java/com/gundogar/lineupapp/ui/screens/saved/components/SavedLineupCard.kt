package com.gundogar.lineupapp.ui.screens.saved.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.JerseyStyle
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.repository.SavedLineup
import com.gundogar.lineupapp.ui.components.jersey.JerseyIcon
import com.gundogar.lineupapp.ui.theme.GrassGreen
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedLineupCard(
    lineup: SavedLineup,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Jersey preview
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GrassGreenDark),
                contentAlignment = Alignment.Center
            ) {
                JerseyIcon(
                    primaryColor = lineup.teamConfig.primaryColor,
                    secondaryColor = lineup.teamConfig.secondaryColor,
                    style = lineup.teamConfig.jerseyStyle,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Team info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lineup.teamName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lineup.formationName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryGold,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = " • ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val playerCount = lineup.players.count { it.value.name.isNotBlank() }
                    Text(
                        text = stringResource(R.string.saved_lineups_players_format, playerCount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatDate(lineup.updatedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // Action buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.cd_edit),
                        tint = GrassGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.cd_delete),
                        tint = Color.Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
private fun SavedLineupCardPreview() {
    LineUpAppTheme {
        SavedLineupCard(
            lineup = SavedLineup(
                id = 1,
                teamName = "FC Barcelona",
                formationId = "433",
                formationName = "4-3-3",
                players = mapOf(
                    1 to Player(1, "Ter Stegen", 1),
                    10 to Player(10, "Messi", 10)
                ),
                teamConfig = TeamConfig(
                    teamName = "FC Barcelona",
                    primaryColor = Color(0xFFA50044),
                    secondaryColor = Color(0xFF004D98),
                    jerseyStyle = JerseyStyle.VERTICAL_STRIPES
                ),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            onClick = {},
            onEditClick = {},
            onDeleteClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

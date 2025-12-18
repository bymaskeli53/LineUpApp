package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.PositionRole
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.ui.components.jersey.JerseyIcon
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme

@Composable
fun PlayerJersey(
    position: Position,
    player: Player?,
    teamConfig: TeamConfig,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(stiffness = 400f),
        label = "jerseyScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(4.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Main content: Jersey + Name (this is the anchor, position doesn't change)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Jersey with rating badge
            Box {
                JerseyIcon(
                    primaryColor = teamConfig.primaryColor,
                    secondaryColor = teamConfig.secondaryColor,
                    style = teamConfig.jerseyStyle,
                    number = player?.number,
                    modifier = Modifier.size(44.dp)
                )

                // Rating badge - positioned at top-right of jersey
                player?.rating?.let { rating ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = (-4).dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(getRatingColor(rating))
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formatRating(rating),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Text(
                text = player?.name?.takeIf { it.isNotBlank() } ?: stringResource(R.string.player_tap_to_add),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (player?.name?.isNotBlank() == true) FontWeight.Medium else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .widthIn(max = 70.dp)
                    .padding(top = 2.dp)
            )
        }

        // Player image - positioned above jersey without affecting layout
        player?.imageUri?.let { imagePath ->
            PlayerImage(
                imagePath = imagePath,
                size = 28.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-26).dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2E7D32)
@Composable
private fun PlayerJerseyPreview() {
    LineUpAppTheme {
        PlayerJersey(
            position = Position(10, PositionRole.FORWARD, 0.5f, 0.75f),
            player = Player(10, "Messi", 10, 9.3),
            teamConfig = TeamConfig(),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2E7D32)
@Composable
private fun PlayerJerseyEmptyPreview() {
    LineUpAppTheme {
        PlayerJersey(
            position = Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),
            player = null,
            teamConfig = TeamConfig(),
            onClick = {}
        )
    }
}

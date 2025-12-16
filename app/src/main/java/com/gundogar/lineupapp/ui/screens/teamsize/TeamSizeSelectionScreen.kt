package com.gundogar.lineupapp.ui.screens.teamsize

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.ui.theme.GrassGreen
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

data class TeamSizeOption(
    val playerCount: Int,
    val description: String,
    val subtitle: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSizeSelectionScreen(
    onTeamSizeSelected: (Int) -> Unit,
    onViewSavedLineups: () -> Unit
) {
    val teamSizes = listOf(
        TeamSizeOption(5, "5-a-side", "Drag & drop"),
        TeamSizeOption(6, "6-a-side", "Drag & drop"),
        TeamSizeOption(7, "7-a-side", "Drag & drop"),
        TeamSizeOption(8, "8-a-side", "Drag & drop"),
        TeamSizeOption(9, "9-a-side", "Drag & drop"),
        TeamSizeOption(10, "10-a-side", "Drag & drop"),
        TeamSizeOption(11, "Full 11", "Fixed formations")
    )
    var selectedSize by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "LineUp App",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onViewSavedLineups) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "My Lineups",
                            tint = SecondaryGold
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
            AnimatedVisibility(
                visible = selectedSize > 0,
                enter = fadeIn() + slideInVertically { it }
            ) {
                ExtendedFloatingActionButton(
                    onClick = { onTeamSizeSelected(selectedSize) },
                    containerColor = SecondaryGold,
                    contentColor = GrassGreenDark
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Continue", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GrassGreenDark, GrassGreen, GrassGreenDark)
                    )
                )
                .padding(16.dp)
        ) {
            Text(
                text = "How many players?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(teamSizes) { option ->
                    TeamSizeCard(
                        option = option,
                        isSelected = selectedSize == option.playerCount,
                        onClick = { selectedSize = option.playerCount }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamSizeCard(
    option: TeamSizeOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                SecondaryGold.copy(alpha = 0.15f)
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        border = if (isSelected) BorderStroke(2.dp, SecondaryGold) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = option.playerCount.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = option.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isSelected)
                    SecondaryGold
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = option.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamSizeSelectionScreenPreview() {
    LineUpAppTheme {
        TeamSizeSelectionScreen(
            onTeamSizeSelected = {},
            onViewSavedLineups = {}
        )
    }
}

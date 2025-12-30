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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
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
    val dragDropSubtitle = stringResource(R.string.team_size_drag_drop)
    val fixedFormationsSubtitle = stringResource(R.string.team_size_fixed_formations)

    val teamSizes = listOf(
        TeamSizeOption(5, stringResource(R.string.team_size_5_side), dragDropSubtitle),
        TeamSizeOption(6, stringResource(R.string.team_size_6_side), dragDropSubtitle),
        TeamSizeOption(7, stringResource(R.string.team_size_7_side), dragDropSubtitle),
        TeamSizeOption(8, stringResource(R.string.team_size_8_side), dragDropSubtitle),
        TeamSizeOption(9, stringResource(R.string.team_size_9_side), dragDropSubtitle),
        TeamSizeOption(10, stringResource(R.string.team_size_10_side), dragDropSubtitle),
        TeamSizeOption(11, stringResource(R.string.team_size_full_11), fixedFormationsSubtitle)
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
                        text = stringResource(R.string.screen_title_lineup_app),
                        fontWeight = FontWeight.Bold
                    )
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
                    contentColor = GrassGreenDark,
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_continue), fontWeight = FontWeight.Bold)
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
                text = stringResource(R.string.team_size_title),
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
            .heightIn(min = 120.dp),
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
                .fillMaxWidth()
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

@Preview(name = "Card - Normal", fontScale = 1.0f)
@Preview(name = "Card - Large Font", fontScale = 1.3f)
@Preview(name = "Card - Huge Font", fontScale = 1.5f)
@Preview(name = "Card - Narrow", widthDp = 150)

@Preview(name = "Small Phone", widthDp = 320, heightDp = 568)
@Preview(name = "Normal Phone", widthDp = 360, heightDp = 640)
@Preview(name = "Large Phone", widthDp = 411, heightDp = 891)
@Preview(name = "Tablet", widthDp = 800, heightDp = 1280)
@Preview(name = "Phone", device = Devices.PIXEL_4)
@Preview(name = "Phone Small", device = Devices.PIXEL_2)
@Preview(name = "Tablet", device = Devices.PIXEL_TABLET)
@Preview(name = "Foldable", device = Devices.FOLDABLE)
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

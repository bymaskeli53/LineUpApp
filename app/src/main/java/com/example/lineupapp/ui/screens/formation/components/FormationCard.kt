package com.example.lineupapp.ui.screens.formation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lineupapp.data.model.Formation
import com.example.lineupapp.data.model.Position
import com.example.lineupapp.data.model.PositionRole
import com.example.lineupapp.ui.theme.LineUpAppTheme
import com.example.lineupapp.ui.theme.SecondaryGold
import com.example.lineupapp.ui.theme.SelectionBorder

@Composable
fun FormationCard(
    formation: Formation,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(stiffness = 300f),
        label = "scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) SelectionBorder else Color.Transparent,
        label = "borderColor"
    )

    val elevation by animateFloatAsState(
        targetValue = if (isSelected) 8f else 2f,
        label = "elevation"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        border = if (isSelected) BorderStroke(2.dp, borderColor) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Formation name
            Text(
                text = formation.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = formation.displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mini pitch preview
            FormationPreview(
                formation = formation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormationCardPreview() {
    LineUpAppTheme {
        FormationCard(
            formation = Formation(
                id = "442",
                name = "4-4-2",
                displayName = "4-4-2 Classic",
                positions = listOf(
                    Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),
                    Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),
                    Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),
                    Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),
                    Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),
                    Position(6, PositionRole.MIDFIELDER, 0.12f, 0.50f),
                    Position(7, PositionRole.MIDFIELDER, 0.37f, 0.47f),
                    Position(8, PositionRole.MIDFIELDER, 0.63f, 0.47f),
                    Position(9, PositionRole.MIDFIELDER, 0.88f, 0.50f),
                    Position(10, PositionRole.FORWARD, 0.35f, 0.75f),
                    Position(11, PositionRole.FORWARD, 0.65f, 0.75f)
                )
            ),
            isSelected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormationCardUnselectedPreview() {
    LineUpAppTheme {
        FormationCard(
            formation = Formation(
                id = "433",
                name = "4-3-3",
                displayName = "4-3-3 Attack",
                positions = emptyList()
            ),
            isSelected = false,
            onClick = {}
        )
    }
}

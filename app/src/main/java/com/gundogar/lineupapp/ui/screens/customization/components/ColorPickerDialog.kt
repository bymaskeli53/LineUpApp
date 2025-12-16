package com.gundogar.lineupapp.ui.screens.customization.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold
import com.gundogar.lineupapp.ui.theme.TeamBlack
import com.gundogar.lineupapp.ui.theme.TeamBlue
import com.gundogar.lineupapp.ui.theme.TeamBrown
import com.gundogar.lineupapp.ui.theme.TeamGold
import com.gundogar.lineupapp.ui.theme.TeamGray
import com.gundogar.lineupapp.ui.theme.TeamGreen
import com.gundogar.lineupapp.ui.theme.TeamMaroon
import com.gundogar.lineupapp.ui.theme.TeamNavy
import com.gundogar.lineupapp.ui.theme.TeamOrange
import com.gundogar.lineupapp.ui.theme.TeamPink
import com.gundogar.lineupapp.ui.theme.TeamPurple
import com.gundogar.lineupapp.ui.theme.TeamRed
import com.gundogar.lineupapp.ui.theme.TeamSkyBlue
import com.gundogar.lineupapp.ui.theme.TeamTeal
import com.gundogar.lineupapp.ui.theme.TeamWhite
import com.gundogar.lineupapp.ui.theme.TeamYellow

val teamColors = listOf(
    TeamRed,
    TeamBlue,
    TeamNavy,
    TeamSkyBlue,
    TeamGreen,
    TeamYellow,
    TeamOrange,
    TeamPurple,
    TeamPink,
    TeamWhite,
    TeamBlack,
    TeamGray,
    TeamMaroon,
    TeamTeal,
    TeamGold,
    TeamBrown
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    title: String,
    currentColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(currentColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    teamColors.forEach { color ->
                        ColorOption(
                            color = color,
                            isSelected = selectedColor == color,
                            onClick = { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                }
            ) {
                Text(
                    text = stringResource(R.string.btn_select),
                    color = SecondaryGold,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_cancel))
            }
        }
    )
}

@Composable
private fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) SecondaryGold else Color.Gray.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.cd_selected),
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

@Preview(showBackground = true)
@Composable
private fun ColorPickerDialogPreview() {
    LineUpAppTheme {
        ColorPickerDialog(
            title = "Select Primary Color",
            currentColor = TeamBlue,
            onColorSelected = {},
            onDismiss = {}
        )
    }
}

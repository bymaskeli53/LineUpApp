package com.gundogar.lineupapp.ui.screens.customization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.ui.components.jersey.JerseyIcon
import com.gundogar.lineupapp.ui.screens.customization.components.ColorPickerDialog
import com.gundogar.lineupapp.ui.screens.customization.components.JerseyStylePicker
import com.gundogar.lineupapp.ui.theme.GrassGreenDark
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun TeamCustomizationSheet(
    teamConfig: TeamConfig,
    onTeamConfigChange: (TeamConfig) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPrimaryColorPicker by remember { mutableStateOf(false) }
    var showSecondaryColorPicker by remember { mutableStateOf(false) }
    var teamName by remember { mutableStateOf(teamConfig.teamName) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Text(
            text = "Team Customization",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Team Name Section
        Text(
            text = "Team Name",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = teamName,
            onValueChange = {
                teamName = it
                onTeamConfigChange(teamConfig.copy(teamName = it))
            },
            label = { Text("Enter team name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        // Jersey Colors Section
        Text(
            text = "Jersey Colors",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Primary Color
        ColorRow(
            label = "Primary Color",
            color = teamConfig.primaryColor,
            onClick = { showPrimaryColorPicker = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Secondary Color
        ColorRow(
            label = "Secondary Color",
            color = teamConfig.secondaryColor,
            onClick = { showSecondaryColorPicker = true }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        // Jersey Style Section
        Text(
            text = "Jersey Style",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        JerseyStylePicker(
            selectedStyle = teamConfig.jerseyStyle,
            primaryColor = teamConfig.primaryColor,
            secondaryColor = teamConfig.secondaryColor,
            onStyleSelected = { style ->
                onTeamConfigChange(teamConfig.copy(jerseyStyle = style))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        // Preview Section
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(GrassGreenDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            JerseyIcon(
                primaryColor = teamConfig.primaryColor,
                secondaryColor = teamConfig.secondaryColor,
                style = teamConfig.jerseyStyle,
                number = 10,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Apply Button
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = SecondaryGold,
                contentColor = GrassGreenDark
            )
        ) {
            Text(
                text = "Apply",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    // Color Picker Dialogs
    if (showPrimaryColorPicker) {
        ColorPickerDialog(
            title = "Select Primary Color",
            currentColor = teamConfig.primaryColor,
            onColorSelected = { color ->
                onTeamConfigChange(teamConfig.copy(primaryColor = color))
            },
            onDismiss = { showPrimaryColorPicker = false }
        )
    }

    if (showSecondaryColorPicker) {
        ColorPickerDialog(
            title = "Select Secondary Color",
            currentColor = teamConfig.secondaryColor,
            onColorSelected = { color ->
                onTeamConfigChange(teamConfig.copy(secondaryColor = color))
            },
            onDismiss = { showSecondaryColorPicker = false }
        )
    }
}

@Composable
private fun ColorRow(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Select",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TeamCustomizationSheetPreview() {
    LineUpAppTheme {
        TeamCustomizationSheet(
            teamConfig = TeamConfig(),
            onTeamConfigChange = {},
            onDismiss = {}
        )
    }
}

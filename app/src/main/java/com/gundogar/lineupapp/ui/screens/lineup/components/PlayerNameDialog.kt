package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.PositionRole
import com.gundogar.lineupapp.ui.theme.LineUpAppTheme
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun PlayerNameDialog(
    position: Position,
    currentName: String,
    currentNumber: Int?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, number: Int?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var numberText by remember { mutableStateOf(currentNumber?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Player Details",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = "Position: ${getPositionName(position.role)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Player Name") },
                    placeholder = { Text("Enter name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = numberText,
                    onValueChange = { input ->
                        // Only allow numbers 1-99
                        if (input.isEmpty() || (input.all { it.isDigit() } && input.length <= 2)) {
                            numberText = input
                        }
                    },
                    label = { Text("Jersey Number") },
                    placeholder = { Text("1-99") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val number = numberText.toIntOrNull()?.takeIf { it in 1..99 }
                    onConfirm(name.trim(), number)
                }
            ) {
                Text(
                    text = "Save",
                    color = SecondaryGold,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun getPositionName(role: PositionRole): String {
    return when (role) {
        PositionRole.GOALKEEPER -> "Goalkeeper"
        PositionRole.DEFENDER -> "Defender"
        PositionRole.MIDFIELDER -> "Midfielder"
        PositionRole.FORWARD -> "Forward"
    }
}

@Preview(showBackground = true)
@Composable
private fun PlayerNameDialogPreview() {
    LineUpAppTheme {
        PlayerNameDialog(
            position = Position(10, PositionRole.FORWARD, 0.5f, 0.75f),
            currentName = "Messi",
            currentNumber = 10,
            onDismiss = {},
            onConfirm = { _, _ -> }
        )
    }
}

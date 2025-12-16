package com.gundogar.lineupapp.ui.screens.lineup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.DrawingColors
import com.gundogar.lineupapp.data.model.DrawingTool
import com.gundogar.lineupapp.data.model.StrokeWidths
import com.gundogar.lineupapp.ui.theme.SecondaryGold

@Composable
fun DrawingToolbar(
    currentTool: DrawingTool,
    currentColor: Color,
    currentStrokeWidth: Float,
    canUndo: Boolean,
    canRedo: Boolean,
    onToolSelected: (DrawingTool) -> Unit,
    onColorSelected: (Color) -> Unit,
    onStrokeWidthChanged: (Float) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClear: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    var showStrokeWidthPicker by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Main toolbar row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Drawing tools
                ToolButton(
                    icon = Icons.Default.Create,
                    contentDescription = stringResource(R.string.tool_pen),
                    isSelected = currentTool == DrawingTool.PEN,
                    onClick = { onToolSelected(DrawingTool.PEN) }
                )

                ToolButton(
                    icon = Icons.Default.KeyboardArrowRight,
                    contentDescription = stringResource(R.string.tool_arrow),
                    isSelected = currentTool == DrawingTool.ARROW,
                    onClick = { onToolSelected(DrawingTool.ARROW) }
                )

                ToolButton(
                    icon = Icons.Default.Create,
                    contentDescription = stringResource(R.string.tool_line),
                    isSelected = currentTool == DrawingTool.LINE,
                    onClick = { onToolSelected(DrawingTool.LINE) }
                )

                ToolButton(
                    icon = Icons.Default.Add,
                    contentDescription = stringResource(R.string.tool_circle),
                    isSelected = currentTool == DrawingTool.CIRCLE,
                    onClick = { onToolSelected(DrawingTool.CIRCLE) }
                )

                ToolButton(
                    icon = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.tool_eraser),
                    isSelected = currentTool == DrawingTool.ERASER,
                    onClick = { onToolSelected(DrawingTool.ERASER) }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Color picker button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(currentColor)
                        .border(
                            width = 2.dp,
                            color = if (showColorPicker) SecondaryGold else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { showColorPicker = !showColorPicker }
                )

                Spacer(modifier = Modifier.width(4.dp))

                // Stroke width button
                StrokeWidthButton(
                    currentWidth = currentStrokeWidth,
                    isSelected = showStrokeWidthPicker,
                    onClick = { showStrokeWidthPicker = !showStrokeWidthPicker }
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Undo/Redo
                IconButton(
                    onClick = onUndo,
                    enabled = canUndo,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.drawing_undo),
                        tint = if (canUndo) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }

                IconButton(
                    onClick = onRedo,
                    enabled = canRedo,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.drawing_redo),
                        tint = if (canRedo) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }

                // Clear all
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.drawing_clear),
                        tint = Color.Red.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Close button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.btn_cancel),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Color picker row (expandable)
            if (showColorPicker) {
                Spacer(modifier = Modifier.height(8.dp))
                ColorPickerRow(
                    currentColor = currentColor,
                    onColorSelected = { color ->
                        onColorSelected(color)
                        showColorPicker = false
                    }
                )
            }

            // Stroke width picker row (expandable)
            if (showStrokeWidthPicker) {
                Spacer(modifier = Modifier.height(8.dp))
                StrokeWidthPickerRow(
                    currentWidth = currentStrokeWidth,
                    onWidthSelected = { width ->
                        onStrokeWidthChanged(width)
                        showStrokeWidthPicker = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) SecondaryGold.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) SecondaryGold else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun StrokeWidthButton(
    currentWidth: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displaySize = when {
        currentWidth <= StrokeWidths.THIN -> 8.dp
        currentWidth <= StrokeWidths.MEDIUM -> 12.dp
        else -> 16.dp
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) SecondaryGold.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SecondaryGold else Color.Gray,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(displaySize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
private fun ColorPickerRow(
    currentColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DrawingColors.all.forEach { color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (currentColor == color) 3.dp else 1.dp,
                        color = if (currentColor == color) SecondaryGold else Color.Gray.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

@Composable
private fun StrokeWidthPickerRow(
    currentWidth: Float,
    onWidthSelected: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StrokeWidthOption(
            width = StrokeWidths.THIN,
            label = stringResource(R.string.stroke_thin),
            isSelected = currentWidth == StrokeWidths.THIN,
            onClick = { onWidthSelected(StrokeWidths.THIN) }
        )

        StrokeWidthOption(
            width = StrokeWidths.MEDIUM,
            label = stringResource(R.string.stroke_medium),
            isSelected = currentWidth == StrokeWidths.MEDIUM,
            onClick = { onWidthSelected(StrokeWidths.MEDIUM) }
        )

        StrokeWidthOption(
            width = StrokeWidths.THICK,
            label = stringResource(R.string.stroke_thick),
            isSelected = currentWidth == StrokeWidths.THICK,
            onClick = { onWidthSelected(StrokeWidths.THICK) }
        )
    }
}

@Composable
private fun StrokeWidthOption(
    width: Float,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displaySize = when (width) {
        StrokeWidths.THIN -> 8.dp
        StrokeWidths.MEDIUM -> 12.dp
        else -> 18.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) SecondaryGold.copy(alpha = 0.2f)
                    else Color.Transparent
                )
                .border(
                    width = if (isSelected) 2.dp else 0.dp,
                    color = if (isSelected) SecondaryGold else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(displaySize)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface
                    )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) SecondaryGold else MaterialTheme.colorScheme.onSurface
        )
    }
}

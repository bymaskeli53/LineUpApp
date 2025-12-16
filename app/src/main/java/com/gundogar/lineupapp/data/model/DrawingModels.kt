package com.gundogar.lineupapp.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.util.UUID

/**
 * Available drawing tools for tactics overlay
 */
enum class DrawingTool {
    PEN,        // Freehand drawing
    ARROW,      // Straight arrow with head
    LINE,       // Straight line
    CIRCLE,     // Circle/ellipse
    ERASER      // Erase strokes
}

/**
 * Represents a single drawing stroke on the pitch
 * All coordinates are stored as percentages (0-1) for screen-size independence
 */
data class DrawingStroke(
    val id: String = UUID.randomUUID().toString(),
    val tool: DrawingTool,
    val points: List<Offset>,  // For PEN: all points; For others: start/end
    val color: Color,
    val strokeWidth: Float,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Serializable version of DrawingStroke for database persistence
 * Uses primitive types instead of Compose types
 */
data class SerializableDrawingStroke(
    val id: String,
    val tool: String,
    val points: List<List<Float>>,  // List of [x, y] pairs
    val colorValue: Long,
    val strokeWidth: Float,
    val timestamp: Long
) {
    companion object {
        fun fromDrawingStroke(stroke: DrawingStroke): SerializableDrawingStroke {
            return SerializableDrawingStroke(
                id = stroke.id,
                tool = stroke.tool.name,
                points = stroke.points.map { listOf(it.x, it.y) },
                colorValue = stroke.color.value.toLong(),
                strokeWidth = stroke.strokeWidth,
                timestamp = stroke.timestamp
            )
        }
    }

    fun toDrawingStroke(): DrawingStroke {
        return DrawingStroke(
            id = id,
            tool = DrawingTool.valueOf(tool),
            points = points.map { Offset(it[0], it[1]) },
            color = Color(colorValue.toULong()),
            strokeWidth = strokeWidth,
            timestamp = timestamp
        )
    }
}

/**
 * Holds the complete drawing state for the lineup screen
 */
data class DrawingState(
    val strokes: List<DrawingStroke> = emptyList(),
    val undoStack: List<DrawingStroke> = emptyList(),  // For redo functionality
    val currentTool: DrawingTool = DrawingTool.PEN,
    val currentColor: Color = Color.White,
    val currentStrokeWidth: Float = 4f,
    val isDrawingMode: Boolean = false
)

/**
 * Predefined colors for the drawing toolbar
 */
object DrawingColors {
    val White = Color.White
    val Yellow = Color(0xFFFFEB3B)
    val Red = Color(0xFFF44336)
    val Blue = Color(0xFF2196F3)
    val Green = Color(0xFF4CAF50)
    val Orange = Color(0xFFFF9800)
    val Black = Color.Black

    val all = listOf(White, Yellow, Red, Blue, Green, Orange, Black)
}

/**
 * Predefined stroke widths
 */
object StrokeWidths {
    const val THIN = 2f
    const val MEDIUM = 4f
    const val THICK = 8f

    val all = listOf(THIN, MEDIUM, THICK)
}

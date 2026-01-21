package com.gundogar.lineupapp.data.model

import java.util.UUID

/**
 * Represents a single frame in the tactic timeline
 * Each frame contains player positions, ball position, and drawings
 */
data class TacticFrame(
    val id: String = UUID.randomUUID().toString(),
    val index: Int,
    val playerPositions: Map<Int, FramePosition>,
    val ballPosition: BallPosition?,
    val strokes: List<DrawingStroke>,
    val duration: Long = 1000L
)

/**
 * Position of a player within a frame
 * Uses percentage-based coordinates for screen-size independence
 */
data class FramePosition(
    val positionId: Int,
    val xPercent: Float,
    val yPercent: Float
)

/**
 * Position and visibility of the ball within a frame
 */
data class BallPosition(
    val xPercent: Float,
    val yPercent: Float,
    val isVisible: Boolean = true
)

/**
 * Complete tactic with all frames and metadata
 */
data class Tactic(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val frames: List<TacticFrame>,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Current state of tactic playback
 */
data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentFrameIndex: Int = 0,
    val playbackSpeed: PlaybackSpeed = PlaybackSpeed.NORMAL,
    val progress: Float = 0f
)

/**
 * Available playback speed options
 */
enum class PlaybackSpeed(val multiplier: Float, val label: String) {
    SLOW(0.5f, "0.5x"),
    NORMAL(1f, "1x"),
    FAST(2f, "2x")
}

/**
 * Serializable version of TacticFrame for database persistence
 */
data class SerializableTacticFrame(
    val id: String,
    val index: Int,
    val playerPositions: List<SerializableFramePosition>,
    val ballPosition: SerializableBallPosition?,
    val strokes: List<SerializableDrawingStroke>,
    val duration: Long
) {
    companion object {
        fun fromTacticFrame(frame: TacticFrame): SerializableTacticFrame {
            return SerializableTacticFrame(
                id = frame.id,
                index = frame.index,
                playerPositions = frame.playerPositions.values.map {
                    SerializableFramePosition(it.positionId, it.xPercent, it.yPercent)
                },
                ballPosition = frame.ballPosition?.let {
                    SerializableBallPosition(it.xPercent, it.yPercent, it.isVisible)
                },
                strokes = frame.strokes.map { SerializableDrawingStroke.fromDrawingStroke(it) },
                duration = frame.duration
            )
        }
    }

    fun toTacticFrame(): TacticFrame {
        return TacticFrame(
            id = id,
            index = index,
            playerPositions = playerPositions.associate {
                it.positionId to FramePosition(it.positionId, it.xPercent, it.yPercent)
            },
            ballPosition = ballPosition?.let {
                BallPosition(it.xPercent, it.yPercent, it.isVisible)
            },
            strokes = strokes.map { it.toDrawingStroke() },
            duration = duration
        )
    }
}

/**
 * Serializable version of FramePosition
 */
data class SerializableFramePosition(
    val positionId: Int,
    val xPercent: Float,
    val yPercent: Float
)

/**
 * Serializable version of BallPosition
 */
data class SerializableBallPosition(
    val xPercent: Float,
    val yPercent: Float,
    val isVisible: Boolean
)

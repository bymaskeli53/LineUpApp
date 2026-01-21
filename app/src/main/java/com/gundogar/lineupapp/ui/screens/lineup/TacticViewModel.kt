package com.gundogar.lineupapp.ui.screens.lineup

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.model.BallPosition
import com.gundogar.lineupapp.data.model.DrawingStroke
import com.gundogar.lineupapp.data.model.DrawingTool
import com.gundogar.lineupapp.data.model.FramePosition
import com.gundogar.lineupapp.data.model.PlaybackSpeed
import com.gundogar.lineupapp.data.model.PlaybackState
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.Tactic
import com.gundogar.lineupapp.data.model.TacticFrame
import com.gundogar.lineupapp.data.repository.TacticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TacticState(
    val tacticId: String? = null,
    val tacticName: String = "New Tactic",
    val frames: List<TacticFrame> = emptyList(),
    val currentFrameIndex: Int = 0,
    val isTacticMode: Boolean = false,
    // Preview mode: user is setting up the next frame position
    // Edit mode (isPreviewMode = false): user is editing an existing frame
    val isPreviewMode: Boolean = true,
    val previewPlayerPositions: Map<Int, FramePosition> = emptyMap(),
    val previewBallPosition: BallPosition = BallPosition(0.5f, 0.5f, true),
    val playbackState: PlaybackState = PlaybackState(),
    val interpolatedPositions: Map<Int, FramePosition>? = null,
    val interpolatedBallPosition: BallPosition? = null,
    val interpolatedStrokes: List<DrawingStroke>? = null,
    val currentTool: DrawingTool = DrawingTool.PEN,
    val currentColor: Color = Color.White,
    val currentStrokeWidth: Float = 4f,
    val isDrawingMode: Boolean = false,
    val undoStack: List<DrawingStroke> = emptyList(),
    val isSaving: Boolean = false,
    val isLoading: Boolean = false
) {
    val currentFrame: TacticFrame?
        get() = if (isPreviewMode) null else frames.getOrNull(currentFrameIndex)

    val effectivePositions: Map<Int, FramePosition>
        get() = interpolatedPositions ?: if (isPreviewMode) previewPlayerPositions else currentFrame?.playerPositions ?: previewPlayerPositions

    val effectiveBallPosition: BallPosition?
        get() = interpolatedBallPosition ?: if (isPreviewMode) previewBallPosition else currentFrame?.ballPosition

    val effectiveStrokes: List<DrawingStroke>
        get() = interpolatedStrokes ?: currentFrame?.strokes ?: emptyList()

    val canUndo: Boolean
        get() = currentFrame?.strokes?.isNotEmpty() == true

    val canRedo: Boolean
        get() = undoStack.isNotEmpty()
}

@HiltViewModel
class TacticViewModel @Inject constructor(
    private val repository: TacticRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TacticState())
    val state: StateFlow<TacticState> = _state.asStateFlow()

    private var playbackJob: Job? = null

    // Frame Management

    fun createNewTactic(basePositions: List<Position>) {
        val initialPlayerPositions = basePositions.associate { position ->
            position.id to FramePosition(
                positionId = position.id,
                xPercent = position.xPercent,
                yPercent = position.yPercent
            )
        }

        _state.update {
            it.copy(
                tacticId = null,
                tacticName = "New Tactic",
                frames = emptyList(),
                currentFrameIndex = 0,
                isTacticMode = true,
                previewPlayerPositions = initialPlayerPositions,
                previewBallPosition = BallPosition(xPercent = 0.5f, yPercent = 0.5f, isVisible = true),
                playbackState = PlaybackState()
            )
        }
    }

    fun enterTacticMode(basePositions: List<Position>) {
        if (_state.value.frames.isEmpty()) {
            createNewTactic(basePositions)
        } else {
            _state.update { it.copy(isTacticMode = true) }
        }
    }

    fun exitTacticMode() {
        stopPlayback()
        _state.update {
            it.copy(
                isTacticMode = false,
                isDrawingMode = false
            )
        }
    }

    fun addFrame() {
        _state.update { currentState ->
            // Always create frame from preview state (current working position)
            val newFrame = TacticFrame(
                id = UUID.randomUUID().toString(),
                index = currentState.frames.size,
                playerPositions = currentState.previewPlayerPositions.toMap(),
                ballPosition = currentState.previewBallPosition.copy(),
                strokes = emptyList(),
                duration = 1000L
            )

            val updatedFrames = currentState.frames + newFrame

            // Stay in preview mode, ready to set up next frame position
            // Preview state keeps current position so user can continue from there
            currentState.copy(
                frames = updatedFrames,
                isPreviewMode = true,
                interpolatedPositions = null,
                interpolatedBallPosition = null,
                interpolatedStrokes = null
            )
        }
    }

    fun duplicateFrame(index: Int) {
        _state.update { currentState ->
            val frameToDuplicate = currentState.frames.getOrNull(index)
                ?: return@update currentState

            val newFrame = TacticFrame(
                id = UUID.randomUUID().toString(),
                index = currentState.frames.size,
                playerPositions = frameToDuplicate.playerPositions.toMap(),
                ballPosition = frameToDuplicate.ballPosition?.copy(),
                strokes = frameToDuplicate.strokes.map { it.copy(id = UUID.randomUUID().toString()) },
                duration = frameToDuplicate.duration
            )

            val updatedFrames = currentState.frames + newFrame
            currentState.copy(
                frames = updatedFrames,
                currentFrameIndex = updatedFrames.size - 1,
                interpolatedPositions = null,
                interpolatedBallPosition = null,
                interpolatedStrokes = null
            )
        }
    }

    fun deleteFrame(index: Int) {
        if (_state.value.frames.size <= 1) return

        _state.update { currentState ->
            val updatedFrames = currentState.frames.filterIndexed { i, _ -> i != index }
                .mapIndexed { i, frame -> frame.copy(index = i) }

            val newIndex = when {
                index >= updatedFrames.size -> updatedFrames.size - 1
                else -> index
            }.coerceAtLeast(0)

            currentState.copy(
                frames = updatedFrames,
                currentFrameIndex = newIndex,
                interpolatedPositions = null,
                interpolatedBallPosition = null,
                interpolatedStrokes = null
            )
        }
    }

    fun selectFrame(index: Int) {
        if (index in _state.value.frames.indices && !_state.value.playbackState.isPlaying) {
            _state.update {
                it.copy(
                    currentFrameIndex = index,
                    isPreviewMode = false, // Switch to edit mode
                    undoStack = emptyList(),
                    interpolatedPositions = null,
                    interpolatedBallPosition = null,
                    interpolatedStrokes = null
                )
            }
        }
    }

    fun enterPreviewMode() {
        if (_state.value.playbackState.isPlaying) return

        _state.update { currentState ->
            // Copy last frame's position to preview for continuity
            val lastFrame = currentState.frames.lastOrNull()
            currentState.copy(
                isPreviewMode = true,
                previewPlayerPositions = lastFrame?.playerPositions ?: currentState.previewPlayerPositions,
                previewBallPosition = lastFrame?.ballPosition ?: currentState.previewBallPosition,
                interpolatedPositions = null,
                interpolatedBallPosition = null,
                interpolatedStrokes = null
            )
        }
    }

    // Position Updates

    fun updatePlayerPosition(positionId: Int, xPercent: Float, yPercent: Float) {
        if (_state.value.playbackState.isPlaying) return

        _state.update { currentState ->
            if (currentState.isPreviewMode) {
                // Update preview state (setting up next frame)
                val updatedPositions = currentState.previewPlayerPositions.toMutableMap()
                updatedPositions[positionId] = FramePosition(
                    positionId = positionId,
                    xPercent = xPercent.coerceIn(0.05f, 0.95f),
                    yPercent = yPercent.coerceIn(0.05f, 0.95f)
                )
                currentState.copy(previewPlayerPositions = updatedPositions)
            } else {
                // Update current frame (editing existing frame)
                val currentFrame = currentState.frames.getOrNull(currentState.currentFrameIndex)
                    ?: return@update currentState
                val updatedPositions = currentFrame.playerPositions.toMutableMap()
                updatedPositions[positionId] = FramePosition(
                    positionId = positionId,
                    xPercent = xPercent.coerceIn(0.05f, 0.95f),
                    yPercent = yPercent.coerceIn(0.05f, 0.95f)
                )

                val updatedFrame = currentFrame.copy(playerPositions = updatedPositions)
                val updatedFrames = currentState.frames.toMutableList()
                updatedFrames[currentState.currentFrameIndex] = updatedFrame

                currentState.copy(frames = updatedFrames)
            }
        }
    }

    fun updateBallPosition(xPercent: Float, yPercent: Float) {
        if (_state.value.playbackState.isPlaying) return

        _state.update { currentState ->
            if (currentState.isPreviewMode) {
                // Update preview state (setting up next frame)
                val updatedBall = currentState.previewBallPosition.copy(
                    xPercent = xPercent.coerceIn(0.02f, 0.98f),
                    yPercent = yPercent.coerceIn(0.02f, 0.98f)
                )
                currentState.copy(previewBallPosition = updatedBall)
            } else {
                // Update current frame (editing existing frame)
                val currentFrame = currentState.frames.getOrNull(currentState.currentFrameIndex)
                    ?: return@update currentState
                val updatedBall = currentFrame.ballPosition?.copy(
                    xPercent = xPercent.coerceIn(0.02f, 0.98f),
                    yPercent = yPercent.coerceIn(0.02f, 0.98f)
                ) ?: BallPosition(
                    xPercent = xPercent.coerceIn(0.02f, 0.98f),
                    yPercent = yPercent.coerceIn(0.02f, 0.98f)
                )

                val updatedFrame = currentFrame.copy(ballPosition = updatedBall)
                val updatedFrames = currentState.frames.toMutableList()
                updatedFrames[currentState.currentFrameIndex] = updatedFrame

                currentState.copy(frames = updatedFrames)
            }
        }
    }

    fun toggleBallVisibility() {
        _state.update { currentState ->
            val currentFrame = currentState.currentFrame ?: return@update currentState
            val currentBall = currentFrame.ballPosition ?: BallPosition(0.5f, 0.5f, true)
            val updatedBall = currentBall.copy(isVisible = !currentBall.isVisible)

            val updatedFrame = currentFrame.copy(ballPosition = updatedBall)
            val updatedFrames = currentState.frames.toMutableList()
            updatedFrames[currentState.currentFrameIndex] = updatedFrame

            currentState.copy(frames = updatedFrames)
        }
    }

    // Drawing (per frame)

    fun toggleDrawingMode() {
        _state.update { it.copy(isDrawingMode = !it.isDrawingMode) }
    }

    fun exitDrawingMode() {
        _state.update { it.copy(isDrawingMode = false) }
    }

    fun setDrawingTool(tool: DrawingTool) {
        _state.update { it.copy(currentTool = tool) }
    }

    fun setDrawingColor(color: Color) {
        _state.update { it.copy(currentColor = color) }
    }

    fun setStrokeWidth(width: Float) {
        _state.update { it.copy(currentStrokeWidth = width) }
    }

    fun addStrokeToCurrentFrame(stroke: DrawingStroke) {
        _state.update { currentState ->
            val currentFrame = currentState.currentFrame ?: return@update currentState
            val updatedStrokes = currentFrame.strokes + stroke

            val updatedFrame = currentFrame.copy(strokes = updatedStrokes)
            val updatedFrames = currentState.frames.toMutableList()
            updatedFrames[currentState.currentFrameIndex] = updatedFrame

            currentState.copy(frames = updatedFrames, undoStack = emptyList())
        }
    }

    fun eraseStrokeFromCurrentFrame(strokeId: String) {
        _state.update { currentState ->
            val currentFrame = currentState.currentFrame ?: return@update currentState
            val strokeToRemove = currentFrame.strokes.find { it.id == strokeId }
            val updatedStrokes = currentFrame.strokes.filter { it.id != strokeId }

            val updatedFrame = currentFrame.copy(strokes = updatedStrokes)
            val updatedFrames = currentState.frames.toMutableList()
            updatedFrames[currentState.currentFrameIndex] = updatedFrame

            currentState.copy(
                frames = updatedFrames,
                undoStack = if (strokeToRemove != null) {
                    currentState.undoStack + strokeToRemove
                } else {
                    currentState.undoStack
                }
            )
        }
    }

    fun undoInCurrentFrame() {
        _state.update { currentState ->
            val currentFrame = currentState.currentFrame ?: return@update currentState
            val strokes = currentFrame.strokes
            if (strokes.isEmpty()) return@update currentState

            val lastStroke = strokes.last()
            val updatedStrokes = strokes.dropLast(1)

            val updatedFrame = currentFrame.copy(strokes = updatedStrokes)
            val updatedFrames = currentState.frames.toMutableList()
            updatedFrames[currentState.currentFrameIndex] = updatedFrame

            currentState.copy(
                frames = updatedFrames,
                undoStack = currentState.undoStack + lastStroke
            )
        }
    }

    fun redoInCurrentFrame() {
        _state.update { currentState ->
            val undoStack = currentState.undoStack
            if (undoStack.isEmpty()) return@update currentState

            val currentFrame = currentState.currentFrame ?: return@update currentState
            val strokeToRestore = undoStack.last()
            val updatedStrokes = currentFrame.strokes + strokeToRestore

            val updatedFrame = currentFrame.copy(strokes = updatedStrokes)
            val updatedFrames = currentState.frames.toMutableList()
            updatedFrames[currentState.currentFrameIndex] = updatedFrame

            currentState.copy(
                frames = updatedFrames,
                undoStack = undoStack.dropLast(1)
            )
        }
    }

    fun clearDrawingsInCurrentFrame() {
        _state.update { currentState ->
            val currentFrame = currentState.currentFrame ?: return@update currentState
            val previousStrokes = currentFrame.strokes

            val updatedFrame = currentFrame.copy(strokes = emptyList())
            val updatedFrames = currentState.frames.toMutableList()
            updatedFrames[currentState.currentFrameIndex] = updatedFrame

            currentState.copy(
                frames = updatedFrames,
                undoStack = previousStrokes
            )
        }
    }

    // Playback

    fun startPlayback() {
        if (_state.value.frames.size < 2) return

        stopPlayback()

        _state.update {
            it.copy(
                playbackState = it.playbackState.copy(
                    isPlaying = true,
                    currentFrameIndex = 0,
                    progress = 0f
                ),
                isDrawingMode = false
            )
        }

        playbackJob = viewModelScope.launch {
            animatePlayback()
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null

        _state.update {
            it.copy(
                playbackState = it.playbackState.copy(
                    isPlaying = false,
                    progress = 0f
                ),
                interpolatedPositions = null,
                interpolatedBallPosition = null,
                interpolatedStrokes = null
            )
        }
    }

    fun setPlaybackSpeed(speed: PlaybackSpeed) {
        _state.update {
            it.copy(playbackState = it.playbackState.copy(playbackSpeed = speed))
        }
    }

    private suspend fun animatePlayback() {
        val frameTimeMs = 16L // ~60fps

        while (_state.value.playbackState.isPlaying) {
            val currentState = _state.value
            val frames = currentState.frames
            if (frames.size < 2) {
                stopPlayback()
                return
            }

            val playbackState = currentState.playbackState
            val currentFrameIdx = playbackState.currentFrameIndex
            val isLastFrame = currentFrameIdx == frames.size - 1
            val nextFrameIdx = if (isLastFrame) currentFrameIdx else currentFrameIdx + 1

            val currentFrame = frames[currentFrameIdx]
            val nextFrame = frames[nextFrameIdx]

            val frameDuration = currentFrame.duration / playbackState.playbackSpeed.multiplier
            val progressIncrement = frameTimeMs / frameDuration

            val newProgress = playbackState.progress + progressIncrement

            if (newProgress >= 1f) {
                if (isLastFrame) {
                    // On last frame - stop playback
                    stopPlayback()
                    return
                }

                // Move to next frame
                _state.update {
                    it.copy(
                        currentFrameIndex = nextFrameIdx,
                        playbackState = it.playbackState.copy(
                            currentFrameIndex = nextFrameIdx,
                            progress = 0f
                        ),
                        interpolatedPositions = null,
                        interpolatedBallPosition = null,
                        interpolatedStrokes = null
                    )
                }
            } else {
                if (isLastFrame) {
                    // On last frame - just show current frame, no interpolation
                    _state.update {
                        it.copy(
                            playbackState = it.playbackState.copy(progress = newProgress),
                            interpolatedPositions = null,
                            interpolatedBallPosition = null,
                            interpolatedStrokes = currentFrame.strokes
                        )
                    }
                } else {
                    // Interpolate positions toward next frame
                    val interpolatedPositions = interpolatePositions(
                        currentFrame.playerPositions,
                        nextFrame.playerPositions,
                        newProgress
                    )

                    val interpolatedBall = interpolateBallPosition(
                        currentFrame.ballPosition,
                        nextFrame.ballPosition,
                        newProgress
                    )

                    // Show current frame strokes during playback (no interpolation for strokes)
                    val currentStrokes = currentFrame.strokes

                    _state.update {
                        it.copy(
                            playbackState = it.playbackState.copy(progress = newProgress),
                            interpolatedPositions = interpolatedPositions,
                            interpolatedBallPosition = interpolatedBall,
                            interpolatedStrokes = currentStrokes
                        )
                    }
                }
            }

            delay(frameTimeMs)
        }
    }

    private fun interpolatePositions(
        from: Map<Int, FramePosition>,
        to: Map<Int, FramePosition>,
        progress: Float
    ): Map<Int, FramePosition> {
        return from.mapValues { (positionId, fromPos) ->
            val toPos = to[positionId] ?: fromPos
            FramePosition(
                positionId = positionId,
                xPercent = lerp(fromPos.xPercent, toPos.xPercent, progress),
                yPercent = lerp(fromPos.yPercent, toPos.yPercent, progress)
            )
        }
    }

    private fun interpolateBallPosition(
        from: BallPosition?,
        to: BallPosition?,
        progress: Float
    ): BallPosition? {
        if (from == null && to == null) return null
        if (from == null) return to
        if (to == null) return from

        return BallPosition(
            xPercent = lerp(from.xPercent, to.xPercent, progress),
            yPercent = lerp(from.yPercent, to.yPercent, progress),
            isVisible = from.isVisible && to.isVisible
        )
    }

    private fun lerp(start: Float, end: Float, fraction: Float): Float {
        return start + (end - start) * fraction
    }

    // Persistence

    fun saveTactic(name: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, tacticName = name) }

            try {
                val tacticId = repository.saveTactic(
                    name = name,
                    frames = _state.value.frames,
                    existingId = _state.value.tacticId
                )

                _state.update {
                    it.copy(
                        tacticId = tacticId,
                        isSaving = false
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun loadTactic(tacticId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val tactic = repository.getTacticById(tacticId)
                if (tactic != null) {
                    _state.update {
                        it.copy(
                            tacticId = tactic.id,
                            tacticName = tactic.name,
                            frames = tactic.frames,
                            currentFrameIndex = 0,
                            isTacticMode = true,
                            isLoading = false,
                            playbackState = PlaybackState()
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateTacticName(name: String) {
        _state.update { it.copy(tacticName = name) }
    }

    fun getAllTactics() = repository.getAllTactics()

    fun deleteTactic(tacticId: String) {
        viewModelScope.launch {
            repository.deleteTactic(tacticId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
    }
}

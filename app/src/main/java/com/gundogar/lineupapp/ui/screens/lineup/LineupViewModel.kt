package com.gundogar.lineupapp.ui.screens.lineup

import android.app.Application
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.model.DrawingState
import com.gundogar.lineupapp.data.model.DrawingStroke
import com.gundogar.lineupapp.data.model.DrawingTool
import com.gundogar.lineupapp.data.model.Formation
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.repository.FormationRepository
import com.gundogar.lineupapp.data.repository.SavedLineupRepository
import com.gundogar.lineupapp.util.ImageStorageUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LineupScreenState(
    val formation: Formation? = null,
    val players: Map<Int, Player> = emptyMap(),
    val customPositions: List<Position>? = null,
    val isCustomizable: Boolean = false,
    val playerCount: Int = 11,
    val teamConfig: TeamConfig = TeamConfig(),
    val selectedPosition: Position? = null,
    val showPlayerDialog: Boolean = false,
    val showCustomizationSheet: Boolean = false,
    val isSaving: Boolean = false,
    val savedLineupId: Long? = null,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val drawingState: DrawingState = DrawingState()
) {
    val effectivePositions: List<Position>
        get() = customPositions ?: formation?.positions ?: emptyList()
}

class LineupViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LineupDatabase.getDatabase(application)
    private val repository = SavedLineupRepository(database.savedLineupDao())

    private val _state = MutableStateFlow(LineupScreenState())
    val state: StateFlow<LineupScreenState> = _state.asStateFlow()

    fun loadFormation(formationId: String) {
        val formation = FormationRepository.getFormationById(formationId)
        _state.update {
            it.copy(
                formation = formation,
                isCustomizable = formation?.isCustomizable ?: false,
                playerCount = formation?.playerCount ?: 11,
                customPositions = if (formation?.isCustomizable == true) formation.positions else null
            )
        }
    }

    fun loadCustomLayout(playerCount: Int) {
        val layout = FormationRepository.getDefaultCustomLayout(playerCount)
        _state.update {
            it.copy(
                formation = layout,
                customPositions = layout.positions,
                isCustomizable = true,
                playerCount = playerCount
            )
        }
    }

    fun updatePositionCoordinates(positionId: Int, newXPercent: Float, newYPercent: Float) {
        _state.update { currentState ->
            val updatedPositions = currentState.customPositions?.map { position ->
                if (position.id == positionId) {
                    position.copy(
                        xPercent = newXPercent.coerceIn(0.05f, 0.95f),
                        yPercent = newYPercent.coerceIn(0.05f, 0.95f)
                    )
                } else {
                    position
                }
            }
            currentState.copy(customPositions = updatedPositions)
        }
    }

    fun loadSavedLineup(lineupId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val savedLineup = repository.getLineupById(lineupId)
            if (savedLineup != null) {
                val isCustomizable = savedLineup.playerCount in 5..10
                val formation = if (isCustomizable) {
                    FormationRepository.getDefaultCustomLayout(savedLineup.playerCount).let { defaultLayout ->
                        defaultLayout.copy(
                            positions = savedLineup.customPositions ?: defaultLayout.positions
                        )
                    }
                } else {
                    FormationRepository.getFormationById(savedLineup.formationId)
                }

                _state.update {
                    it.copy(
                        formation = formation,
                        players = savedLineup.players,
                        customPositions = savedLineup.customPositions,
                        isCustomizable = isCustomizable,
                        playerCount = savedLineup.playerCount,
                        teamConfig = savedLineup.teamConfig,
                        savedLineupId = lineupId,
                        isLoading = false,
                        drawingState = it.drawingState.copy(strokes = savedLineup.drawingStrokes)
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onPlayerClick(position: Position) {
        _state.update {
            it.copy(
                selectedPosition = position,
                showPlayerDialog = true
            )
        }
    }

    fun onPlayerDialogDismiss() {
        _state.update {
            it.copy(
                selectedPosition = null,
                showPlayerDialog = false
            )
        }
    }

    fun onPlayerSave(
        name: String,
        number: Int?,
        rating: Double?,
        pendingImageUri: Uri?,
        existingImagePath: String?
    ) {
        val position = _state.value.selectedPosition ?: return

        viewModelScope.launch {
            // Handle image: copy new image or keep existing
            val imageUri = when {
                pendingImageUri != null -> {
                    // Delete old image if being replaced
                    val currentPlayer = _state.value.players[position.id]
                    if (currentPlayer?.imageUri != null) {
                        ImageStorageUtil.deleteImage(currentPlayer.imageUri)
                    }
                    // Copy new image to app storage
                    ImageStorageUtil.copyImageToAppStorage(getApplication(), pendingImageUri)
                }
                existingImagePath != null -> existingImagePath
                else -> {
                    // Image was removed - delete old image
                    val currentPlayer = _state.value.players[position.id]
                    if (currentPlayer?.imageUri != null) {
                        ImageStorageUtil.deleteImage(currentPlayer.imageUri)
                    }
                    null
                }
            }

            val player = Player(
                positionId = position.id,
                name = name,
                number = number,
                rating = rating,
                imageUri = imageUri
            )

            _state.update { currentState ->
                val updatedPlayers = currentState.players.toMutableMap()
                updatedPlayers[position.id] = player

                currentState.copy(
                    players = updatedPlayers,
                    selectedPosition = null,
                    showPlayerDialog = false
                )
            }
        }
    }

    fun showCustomizationSheet() {
        _state.update { it.copy(showCustomizationSheet = true) }
    }

    fun hideCustomizationSheet() {
        _state.update { it.copy(showCustomizationSheet = false) }
    }

    fun updateTeamConfig(teamConfig: TeamConfig) {
        _state.update { it.copy(teamConfig = teamConfig) }
    }

    fun updateTeamName(name: String) {
        _state.update { currentState ->
            currentState.copy(
                teamConfig = currentState.teamConfig.copy(teamName = name)
            )
        }
    }

    fun saveLineupToDatabase(onSuccess: () -> Unit) {
        val currentState = _state.value
        val formation = currentState.formation ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            try {
                val savedId = repository.saveLineup(
                    teamName = currentState.teamConfig.teamName,
                    formationId = formation.id,
                    formationName = formation.name,
                    players = currentState.players,
                    customPositions = if (currentState.isCustomizable) currentState.customPositions else null,
                    playerCount = currentState.playerCount,
                    teamConfig = currentState.teamConfig,
                    drawingStrokes = currentState.drawingState.strokes,
                    existingId = currentState.savedLineupId
                )

                _state.update {
                    it.copy(
                        isSaving = false,
                        savedLineupId = savedId,
                        saveSuccess = true
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    fun getPlayerForPosition(positionId: Int): Player? {
        return _state.value.players[positionId]
    }

    // Drawing methods
    fun toggleDrawingMode() {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(
                    isDrawingMode = !currentState.drawingState.isDrawingMode
                )
            )
        }
    }

    fun exitDrawingMode() {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(isDrawingMode = false)
            )
        }
    }

    fun setDrawingTool(tool: DrawingTool) {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(currentTool = tool)
            )
        }
    }

    fun setDrawingColor(color: Color) {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(currentColor = color)
            )
        }
    }

    fun setStrokeWidth(width: Float) {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(currentStrokeWidth = width)
            )
        }
    }

    fun addStroke(stroke: DrawingStroke) {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(
                    strokes = currentState.drawingState.strokes + stroke,
                    undoStack = emptyList() // Clear redo stack when new stroke is added
                )
            )
        }
    }

    fun eraseStroke(strokeId: String) {
        _state.update { currentState ->
            val strokeToRemove = currentState.drawingState.strokes.find { it.id == strokeId }
            currentState.copy(
                drawingState = currentState.drawingState.copy(
                    strokes = currentState.drawingState.strokes.filter { it.id != strokeId },
                    undoStack = if (strokeToRemove != null) {
                        currentState.drawingState.undoStack + strokeToRemove
                    } else {
                        currentState.drawingState.undoStack
                    }
                )
            )
        }
    }

    fun undo() {
        _state.update { currentState ->
            val strokes = currentState.drawingState.strokes
            if (strokes.isEmpty()) return@update currentState

            val lastStroke = strokes.last()
            currentState.copy(
                drawingState = currentState.drawingState.copy(
                    strokes = strokes.dropLast(1),
                    undoStack = currentState.drawingState.undoStack + lastStroke
                )
            )
        }
    }

    fun redo() {
        _state.update { currentState ->
            val undoStack = currentState.drawingState.undoStack
            if (undoStack.isEmpty()) return@update currentState

            val strokeToRestore = undoStack.last()
            currentState.copy(
                drawingState = currentState.drawingState.copy(
                    strokes = currentState.drawingState.strokes + strokeToRestore,
                    undoStack = undoStack.dropLast(1)
                )
            )
        }
    }

    fun clearAllDrawings() {
        _state.update { currentState ->
            currentState.copy(
                drawingState = currentState.drawingState.copy(
                    strokes = emptyList(),
                    undoStack = currentState.drawingState.strokes // Allow undo of clear
                )
            )
        }
    }
}

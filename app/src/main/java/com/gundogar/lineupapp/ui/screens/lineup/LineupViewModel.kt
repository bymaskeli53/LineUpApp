package com.gundogar.lineupapp.ui.screens.lineup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.model.Formation
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.repository.FormationRepository
import com.gundogar.lineupapp.data.repository.SavedLineupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LineupScreenState(
    val formation: Formation? = null,
    val players: Map<Int, Player> = emptyMap(),
    val teamConfig: TeamConfig = TeamConfig(),
    val selectedPosition: Position? = null,
    val showPlayerDialog: Boolean = false,
    val showCustomizationSheet: Boolean = false,
    val isSaving: Boolean = false,
    val savedLineupId: Long? = null,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false
)

class LineupViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LineupDatabase.getDatabase(application)
    private val repository = SavedLineupRepository(database.savedLineupDao())

    private val _state = MutableStateFlow(LineupScreenState())
    val state: StateFlow<LineupScreenState> = _state.asStateFlow()

    fun loadFormation(formationId: String) {
        val formation = FormationRepository.getFormationById(formationId)
        _state.update { it.copy(formation = formation) }
    }

    fun loadSavedLineup(lineupId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val savedLineup = repository.getLineupById(lineupId)
            if (savedLineup != null) {
                val formation = FormationRepository.getFormationById(savedLineup.formationId)
                _state.update {
                    it.copy(
                        formation = formation,
                        players = savedLineup.players,
                        teamConfig = savedLineup.teamConfig,
                        savedLineupId = lineupId,
                        isLoading = false
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

    fun onPlayerSave(name: String, number: Int?, rating: Double?) {
        val position = _state.value.selectedPosition ?: return

        val player = Player(
            positionId = position.id,
            name = name,
            number = number,
            rating = rating
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
                    teamConfig = currentState.teamConfig,
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
}

package com.example.lineupapp.ui.screens.lineup

import androidx.lifecycle.ViewModel
import com.example.lineupapp.data.model.Formation
import com.example.lineupapp.data.model.Player
import com.example.lineupapp.data.model.Position
import com.example.lineupapp.data.model.TeamConfig
import com.example.lineupapp.data.repository.FormationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LineupScreenState(
    val formation: Formation? = null,
    val players: Map<Int, Player> = emptyMap(),
    val teamConfig: TeamConfig = TeamConfig(),
    val selectedPosition: Position? = null,
    val showPlayerDialog: Boolean = false,
    val showCustomizationSheet: Boolean = false,
    val isSaving: Boolean = false
)

class LineupViewModel : ViewModel() {

    private val _state = MutableStateFlow(LineupScreenState())
    val state: StateFlow<LineupScreenState> = _state.asStateFlow()

    fun loadFormation(formationId: String) {
        val formation = FormationRepository.getFormationById(formationId)
        _state.update { it.copy(formation = formation) }
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

    fun onPlayerSave(name: String, number: Int?) {
        val position = _state.value.selectedPosition ?: return

        val player = Player(
            positionId = position.id,
            name = name,
            number = number
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

    fun getPlayerForPosition(positionId: Int): Player? {
        return _state.value.players[positionId]
    }
}

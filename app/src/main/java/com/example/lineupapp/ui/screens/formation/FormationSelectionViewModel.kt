package com.example.lineupapp.ui.screens.formation

import androidx.lifecycle.ViewModel
import com.example.lineupapp.data.model.Formation
import com.example.lineupapp.data.repository.FormationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FormationSelectionState(
    val formations: List<Formation> = emptyList(),
    val selectedFormationId: String? = null
)

class FormationSelectionViewModel : ViewModel() {

    private val _state = MutableStateFlow(FormationSelectionState())
    val state: StateFlow<FormationSelectionState> = _state.asStateFlow()

    init {
        loadFormations()
    }

    private fun loadFormations() {
        _state.update { it.copy(formations = FormationRepository.getAllFormations()) }
    }

    fun selectFormation(formationId: String) {
        _state.update { it.copy(selectedFormationId = formationId) }
    }

    fun getSelectedFormationId(): String? = _state.value.selectedFormationId
}

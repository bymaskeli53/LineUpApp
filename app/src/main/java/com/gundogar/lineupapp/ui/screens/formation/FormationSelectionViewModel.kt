package com.gundogar.lineupapp.ui.screens.formation

import androidx.lifecycle.ViewModel
import com.gundogar.lineupapp.data.model.Formation
import com.gundogar.lineupapp.data.repository.FormationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class FormationSelectionState(
    val formations: List<Formation> = emptyList(),
    val selectedFormationId: String? = null
)

@HiltViewModel
class FormationSelectionViewModel @Inject constructor() : ViewModel() {

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

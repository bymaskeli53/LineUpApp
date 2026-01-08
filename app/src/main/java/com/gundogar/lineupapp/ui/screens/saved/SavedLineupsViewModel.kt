package com.gundogar.lineupapp.ui.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.repository.SavedLineup
import com.gundogar.lineupapp.data.repository.SavedLineupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SavedLineupsState(
    val savedLineups: List<SavedLineup> = emptyList(),
    val isLoading: Boolean = true,
    val lineupToDelete: SavedLineup? = null,
    val showDeleteDialog: Boolean = false
)

@HiltViewModel
class SavedLineupsViewModel @Inject constructor(
    private val repository: SavedLineupRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SavedLineupsState())
    val state: StateFlow<SavedLineupsState> = _state.asStateFlow()

    init {
        loadSavedLineups()
    }

    private fun loadSavedLineups() {
        viewModelScope.launch {
            repository.getAllLineups().collect { lineups ->
                _state.update {
                    it.copy(
                        savedLineups = lineups,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun showDeleteDialog(lineup: SavedLineup) {
        _state.update {
            it.copy(
                lineupToDelete = lineup,
                showDeleteDialog = true
            )
        }
    }

    fun hideDeleteDialog() {
        _state.update {
            it.copy(
                lineupToDelete = null,
                showDeleteDialog = false
            )
        }
    }

    fun deleteLineup() {
        val lineup = _state.value.lineupToDelete ?: return
        viewModelScope.launch {
            repository.deleteLineup(lineup.id)
            hideDeleteDialog()
        }
    }
}

package com.gundogar.lineupapp.ui.screens.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.repository.SavedLineup
import com.gundogar.lineupapp.data.repository.SavedLineupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SavedLineupsState(
    val savedLineups: List<SavedLineup> = emptyList(),
    val isLoading: Boolean = true,
    val lineupToDelete: SavedLineup? = null,
    val showDeleteDialog: Boolean = false
)

class SavedLineupsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LineupDatabase.getDatabase(application)
    private val repository = SavedLineupRepository(database.savedLineupDao())

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

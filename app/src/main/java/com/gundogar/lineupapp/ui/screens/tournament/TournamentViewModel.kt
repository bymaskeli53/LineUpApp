package com.gundogar.lineupapp.ui.screens.tournament

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.model.Match
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.PlayerStatistics
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.model.Tournament
import com.gundogar.lineupapp.data.model.TournamentStatus
import com.gundogar.lineupapp.data.repository.TournamentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TournamentListState(
    val tournaments: List<Tournament> = emptyList(),
    val isLoading: Boolean = false
)

data class CreateTournamentState(
    val name: String = "",
    val teamCount: Int = 4,
    val isCreating: Boolean = false,
    val createdTournamentId: Long? = null
)

data class TournamentDetailState(
    val tournament: Tournament? = null,
    val topScorers: List<PlayerStatistics> = emptyList(),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddTeamDialog: Boolean = false,
    val canStart: Boolean = false,
    val canAdvance: Boolean = false,
    val hasDraws: Boolean = false
)

@HiltViewModel
class TournamentListViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TournamentListState())
    val state: StateFlow<TournamentListState> = _state.asStateFlow()

    init {
        loadTournaments()
    }

    private fun loadTournaments() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            tournamentRepository.getAllTournaments().collect { tournaments ->
                _state.update { it.copy(tournaments = tournaments, isLoading = false) }
            }
        }
    }

    fun deleteTournament(tournamentId: Long) {
        viewModelScope.launch {
            tournamentRepository.deleteTournament(tournamentId)
        }
    }
}

@HiltViewModel
class CreateTournamentViewModel @Inject constructor(
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateTournamentState())
    val state: StateFlow<CreateTournamentState> = _state.asStateFlow()

    fun setName(name: String) {
        _state.update { it.copy(name = name) }
    }

    fun setTeamCount(count: Int) {
        _state.update { it.copy(teamCount = count) }
    }

    fun createTournament() {
        val currentState = _state.value
        if (currentState.name.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isCreating = true) }
            val id = tournamentRepository.createTournament(currentState.name, currentState.teamCount)
            _state.update { it.copy(isCreating = false, createdTournamentId = id) }
        }
    }

    fun isValid(): Boolean = _state.value.name.isNotBlank()
}

@HiltViewModel
class TournamentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tournamentRepository: TournamentRepository
) : ViewModel() {

    private val tournamentId: Long = checkNotNull(savedStateHandle["tournamentId"])

    private val _state = MutableStateFlow(TournamentDetailState())
    val state: StateFlow<TournamentDetailState> = _state.asStateFlow()

    init {
        loadTournament()
    }

    fun loadTournament() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val tournament = tournamentRepository.getTournamentById(tournamentId)

                if (tournament == null) {
                    _state.update { it.copy(isLoading = false, error = "Tournament not found") }
                    return@launch
                }

                val topScorers = if (tournament.status != TournamentStatus.SETUP) {
                    tournamentRepository.getTopScorers(tournamentId)
                } else {
                    emptyList()
                }

                val activeTeams = tournament.teams.filter { !it.isEliminated }
                val canStart = tournament.status == TournamentStatus.SETUP && activeTeams.size >= 2

                val currentRoundMatches = tournament.matches.filter { m -> m.tournamentRound == tournament.currentRound }
                val hasDraws = tournament.status == TournamentStatus.IN_PROGRESS &&
                        currentRoundMatches.any { m -> m.isCompleted && !m.isBye && m.homeScore == m.awayScore }

                val canAdvance = tournament.status == TournamentStatus.IN_PROGRESS &&
                        currentRoundMatches.all { m -> m.isCompleted } &&
                        !hasDraws

                _state.update {
                    it.copy(
                        tournament = tournament,
                        topScorers = topScorers,
                        isLoading = false,
                        error = null,
                        canStart = canStart,
                        canAdvance = canAdvance,
                        hasDraws = hasDraws
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Failed to load tournament") }
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun showAddTeamDialog() {
        _state.update { it.copy(showAddTeamDialog = true) }
    }

    fun hideAddTeamDialog() {
        _state.update { it.copy(showAddTeamDialog = false) }
    }

    fun addTeam(teamName: String, teamConfig: TeamConfig? = null, players: List<Player> = emptyList()) {
        viewModelScope.launch {
            tournamentRepository.addTeamToTournament(
                tournamentId = tournamentId,
                teamName = teamName,
                teamConfig = teamConfig,
                players = players
            )
            hideAddTeamDialog()
            loadTournament()
        }
    }

    fun removeTeam(teamId: Long) {
        viewModelScope.launch {
            tournamentRepository.removeTeamFromTournament(teamId)
            loadTournament()
        }
    }

    fun startTournament() {
        viewModelScope.launch {
            val success = tournamentRepository.generateBracket(tournamentId)
            if (success) {
                loadTournament()
            }
        }
    }

    fun advanceRound() {
        viewModelScope.launch {
            val success = tournamentRepository.advanceToNextRound(tournamentId)
            if (success) {
                loadTournament()
            }
        }
    }

    fun getMatchesForCurrentRound(): List<Match> {
        val tournament = _state.value.tournament ?: return emptyList()
        return tournament.matches.filter { it.tournamentRound == tournament.currentRound }
    }
}

package com.gundogar.lineupapp.ui.screens.match

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.model.Goal
import com.gundogar.lineupapp.data.model.Match
import com.gundogar.lineupapp.data.model.Player
import com.gundogar.lineupapp.data.model.TeamConfig
import com.gundogar.lineupapp.data.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MatchListState(
    val matches: List<Match> = emptyList(),
    val filter: MatchFilter = MatchFilter.ALL,
    val isLoading: Boolean = false
)

enum class MatchFilter {
    ALL, FRIENDLY, TOURNAMENT
}

data class CreateMatchState(
    val homeTeamName: String = "",
    val awayTeamName: String = "",
    val homeTeamConfig: TeamConfig? = null,
    val awayTeamConfig: TeamConfig? = null,
    val homePlayers: List<Player> = emptyList(),
    val awayPlayers: List<Player> = emptyList(),
    val isCreating: Boolean = false,
    val createdMatchId: Long? = null
)

data class MatchScoringState(
    val match: Match? = null,
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val showScorerDialog: Boolean = false,
    val selectedTeamIsHome: Boolean = true,
    val isKnockout: Boolean = false,
    val canComplete: Boolean = true
)

class MatchListViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LineupDatabase.getDatabase(application)
    private val matchRepository = MatchRepository(database.matchDao(), database.goalDao())

    private val _state = MutableStateFlow(MatchListState())
    val state: StateFlow<MatchListState> = _state.asStateFlow()

    init {
        loadMatches()
    }

    private fun loadMatches() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            matchRepository.getAllMatches().collect { matches ->
                val filteredMatches = when (_state.value.filter) {
                    MatchFilter.ALL -> matches
                    MatchFilter.FRIENDLY -> matches.filter { it.tournamentId == null }
                    MatchFilter.TOURNAMENT -> matches.filter { it.tournamentId != null }
                }
                _state.update { it.copy(matches = filteredMatches, isLoading = false) }
            }
        }
    }

    fun setFilter(filter: MatchFilter) {
        _state.update { it.copy(filter = filter) }
        loadMatches()
    }

    fun deleteMatch(matchId: Long) {
        viewModelScope.launch {
            matchRepository.deleteMatch(matchId)
        }
    }
}

class CreateMatchViewModel(application: Application) : AndroidViewModel(application) {
    private val database = LineupDatabase.getDatabase(application)
    private val matchRepository = MatchRepository(database.matchDao(), database.goalDao())

    private val _state = MutableStateFlow(CreateMatchState())
    val state: StateFlow<CreateMatchState> = _state.asStateFlow()

    fun setHomeTeamName(name: String) {
        _state.update { it.copy(homeTeamName = name) }
    }

    fun setAwayTeamName(name: String) {
        _state.update { it.copy(awayTeamName = name) }
    }

    fun setHomeTeam(config: TeamConfig?, players: List<Player>) {
        _state.update { it.copy(homeTeamConfig = config, homePlayers = players, homeTeamName = config?.teamName ?: it.homeTeamName) }
    }

    fun setAwayTeam(config: TeamConfig?, players: List<Player>) {
        _state.update { it.copy(awayTeamConfig = config, awayPlayers = players, awayTeamName = config?.teamName ?: it.awayTeamName) }
    }

    fun createMatch() {
        val currentState = _state.value
        if (currentState.homeTeamName.isBlank() || currentState.awayTeamName.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isCreating = true) }
            val matchId = matchRepository.createMatch(
                homeTeamName = currentState.homeTeamName,
                awayTeamName = currentState.awayTeamName,
                homeTeamConfig = currentState.homeTeamConfig,
                awayTeamConfig = currentState.awayTeamConfig,
                homePlayers = currentState.homePlayers,
                awayPlayers = currentState.awayPlayers
            )
            _state.update { it.copy(isCreating = false, createdMatchId = matchId) }
        }
    }

    fun isValid(): Boolean {
        val state = _state.value
        return state.homeTeamName.isNotBlank() && state.awayTeamName.isNotBlank()
    }
}

class MatchScoringViewModel(
    application: Application,
    private val matchId: Long
) : AndroidViewModel(application) {
    private val database = LineupDatabase.getDatabase(application)
    private val matchRepository = MatchRepository(database.matchDao(), database.goalDao())

    private val _state = MutableStateFlow(MatchScoringState())
    val state: StateFlow<MatchScoringState> = _state.asStateFlow()

    init {
        loadMatch()
        observeGoals()
    }

    private fun loadMatch() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val match = matchRepository.getMatchById(matchId)
            val isKnockout = match?.tournamentId != null
            _state.update {
                it.copy(
                    match = match,
                    isLoading = false,
                    isKnockout = isKnockout,
                    canComplete = !isKnockout || (match?.homeScore != match?.awayScore)
                )
            }
        }
    }

    private fun observeGoals() {
        viewModelScope.launch {
            matchRepository.getGoalsByMatch(matchId).collect { goals ->
                _state.update { it.copy(goals = goals) }
            }
        }
    }

    fun showScorerDialog(isHomeTeam: Boolean) {
        _state.update { it.copy(showScorerDialog = true, selectedTeamIsHome = isHomeTeam) }
    }

    fun hideScorerDialog() {
        _state.update { it.copy(showScorerDialog = false) }
    }

    fun addGoal(scorerId: Int, scorerName: String, minute: Int? = null) {
        val match = _state.value.match ?: return
        val isHomeTeam = _state.value.selectedTeamIsHome
        val teamName = if (isHomeTeam) match.homeTeamName else match.awayTeamName

        viewModelScope.launch {
            matchRepository.addGoal(
                matchId = matchId,
                scorerId = scorerId,
                scorerName = scorerName,
                teamName = teamName,
                isHomeTeam = isHomeTeam,
                minute = minute
            )
            loadMatch() // Refresh match to update score
            hideScorerDialog()
        }
    }

    fun removeGoal(goal: Goal) {
        viewModelScope.launch {
            matchRepository.removeGoal(goal.id, matchId, goal.isHomeTeam)
            loadMatch() // Refresh match to update score
        }
    }

    fun completeMatch() {
        viewModelScope.launch {
            matchRepository.completeMatch(matchId)
            loadMatch()
        }
    }

    fun canComplete(): Boolean {
        val match = _state.value.match ?: return false
        return if (_state.value.isKnockout) {
            match.homeScore != match.awayScore
        } else {
            true
        }
    }
}

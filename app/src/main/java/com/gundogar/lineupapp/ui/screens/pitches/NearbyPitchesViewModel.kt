package com.gundogar.lineupapp.ui.screens.pitches

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.local.LineupDatabase
import com.gundogar.lineupapp.data.model.FootballPitch
import com.gundogar.lineupapp.data.remote.NetworkModule
import com.gundogar.lineupapp.data.repository.FootballPitchRepository
import com.gundogar.lineupapp.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NearbyPitchesState(
    val isLoading: Boolean = false,
    val pitches: List<FootballPitch> = emptyList(),
    val userLocation: Location? = null,
    val error: String? = null,
    val hasLocationPermission: Boolean = false,
    val selectedTab: Int = 0, // 0 = List, 1 = Map
    val searchRadiusMeters: Int = 5000
)

class NearbyPitchesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = LineupDatabase.getDatabase(application)
    private val repository = FootballPitchRepository(
        api = NetworkModule.overpassApi,
        dao = database.footballPitchDao()
    )
    private val locationHelper = LocationHelper(application)

    private val _state = MutableStateFlow(NearbyPitchesState())
    val state: StateFlow<NearbyPitchesState> = _state.asStateFlow()

    init {
        checkLocationPermission()
    }

    fun checkLocationPermission() {
        _state.update { it.copy(hasLocationPermission = locationHelper.hasLocationPermission()) }
    }

    fun onPermissionGranted() {
        _state.update { it.copy(hasLocationPermission = true) }
        fetchCurrentLocationAndPitches()
    }

    fun onPermissionDenied() {
        _state.update {
            it.copy(
                hasLocationPermission = false,
                error = "Location permission is required to find nearby pitches"
            )
        }
    }

    fun fetchCurrentLocationAndPitches(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val location = locationHelper.getCurrentLocation()
            if (location == null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to get current location. Please check your location settings."
                    )
                }
                return@launch
            }

            _state.update { it.copy(userLocation = location) }

            val result = repository.searchNearbyPitches(
                latitude = location.latitude,
                longitude = location.longitude,
                radiusMeters = _state.value.searchRadiusMeters,
                forceRefresh = forceRefresh
            )

            result.fold(
                onSuccess = { pitches ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            pitches = pitches,
                            error = null
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to fetch nearby pitches"
                        )
                    }
                }
            )
        }
    }

    fun setSelectedTab(tab: Int) {
        _state.update { it.copy(selectedTab = tab) }
    }

    fun setSearchRadius(radiusMeters: Int) {
        _state.update { it.copy(searchRadiusMeters = radiusMeters) }
        fetchCurrentLocationAndPitches(forceRefresh = true)
    }

    fun refresh() {
        fetchCurrentLocationAndPitches(forceRefresh = true)
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

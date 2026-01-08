package com.gundogar.lineupapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.preferences.OnboardingPreferences
import com.gundogar.lineupapp.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow(Screen.OnBoarding.route)
    val startDestination: StateFlow<String> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val completed = onboardingPreferences.hasCompletedOnboarding.first()

            _startDestination.value = if (completed) {
                Screen.TeamSizeSelection.route
            } else {
                Screen.OnBoarding.route
            }

            _isLoading.value = false
        }
    }
}
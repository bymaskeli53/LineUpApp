package com.gundogar.lineupapp.ui.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gundogar.lineupapp.data.preferences.OnboardingPreferences
import com.gundogar.lineupapp.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val onboardingPreferences: OnboardingPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _startDestination = MutableStateFlow(Screen.OnBoarding.route)
    val startDestination: StateFlow<String> = _startDestination

    init {
        viewModelScope.launch {
            val completed = onboardingPreferences.hasCompletedOnboarding.first() // ✅ Tek değer al ve devam et

            _startDestination.value = if (completed) {
                Screen.TeamSizeSelection.route
            } else {
                Screen.OnBoarding.route
            }

            _isLoading.value = false // ✅ Artık buraya ulaşılacak
        }
    }
}
package com.example.dailyweighttracker_kotlin.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyweighttracker_kotlin.data.GoalRepository
import com.example.dailyweighttracker_kotlin.data.SessionManager
import com.example.dailyweighttracker_kotlin.data.UserPreferencesDAO
import com.example.dailyweighttracker_kotlin.data.WeightRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel for the Home screen
class HomeViewModel(
    private val weightsRepository: WeightRepository,
    private val goalRepository: GoalRepository,
    private val userPreferencesDAO: UserPreferencesDAO
) : ViewModel() {

    // State for the Home screen
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()


    // Data collection job
    private var dataCollectionJob: Job? = null

    init {
        viewModelScope.launch {
            // Collect the currentUser
            SessionManager.currentUser.collect { user ->
                // Cancel any previous collection job if user changes
                dataCollectionJob?.cancel()

                if (user == null) {
                    // Reset the UI state if user is not logged in
                    _homeUiState.value = HomeUiState()
                } else {
                    // If user is logged in, start collecting their data
                    dataCollectionJob = launch {
                        combine(
                            weightsRepository.getAllWeightsStream(user.id),
                            goalRepository.getGoalForUserFlow(user.id),
                            userPreferencesDAO.getUnitPreference(user.id)
                        ) { weights, goal, unit ->
                            Triple(weights, goal, unit)
                        }.collect { (weights, goal, unit) ->

                            // Find the most recent weight
                            val mostRecent = weights
                                .sortedWith(
                                    compareByDescending<com.example.dailyweighttracker_kotlin.data.room.Weight> { it.date }
                                        .thenByDescending { it.id }
                                )
                                .firstOrNull()
                            // Update the UI state with the collected data
                            _homeUiState.update { currentState ->
                                currentState.copy(
                                    weightList = weights,
                                    mostRecentWeight = mostRecent,
                                    goalWeight = goal?.goalWeight,
                                    weightUnit = unit
                                )
                            }
                        }
                    }

                }
            }
        }
    }

    // Dialog functions
    fun onAddWeightClick() = _homeUiState.update { it.copy(showAddWeightDialog = true) }
    fun onAddWeightDialogDismiss() = _homeUiState.update { it.copy(showAddWeightDialog = false) }
    fun onSetGoalClick() = _homeUiState.update { it.copy(showSetGoalDialog = true) }
    fun onSetGoalDialogDismiss() = _homeUiState.update { it.copy(showSetGoalDialog = false) }
}

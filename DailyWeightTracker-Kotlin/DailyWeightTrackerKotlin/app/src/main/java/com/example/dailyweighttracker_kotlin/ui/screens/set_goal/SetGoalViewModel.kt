package com.example.dailyweighttracker_kotlin.ui.screens.set_goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyweighttracker_kotlin.data.GoalRepository
import com.example.dailyweighttracker_kotlin.data.SessionManager
import com.example.dailyweighttracker_kotlin.data.UserPreferencesDAO
import com.example.dailyweighttracker_kotlin.data.WeightUnit
import com.example.dailyweighttracker_kotlin.data.room.Goal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI state for the Set Goal dialog
data class SetGoalUiState(
    val goalWeight: String = "",
    val selectedUnit: WeightUnit = WeightUnit.LBS
)

// ViewModel for the Set Goal dialog
class SetGoalViewModel(
    private val goalRepository: GoalRepository,
    private val userPreferencesDAO: UserPreferencesDAO
) : ViewModel() {

    // UI state
    private val _uiState = MutableStateFlow(SetGoalUiState())
    val uiState: StateFlow<SetGoalUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {

            // Get the current user and their preferences
            val user = SessionManager.currentUser.first() ?: return@launch
            val initialGoal = goalRepository.getGoalForUserFlow(user.id).first()
            val initialUnit = userPreferencesDAO.getUnitPreference(user.id).first()

            // Update the UI state with the initial values
            _uiState.update {
                it.copy(
                    goalWeight = initialGoal?.goalWeight?.let { goalValue -> "%.1f".format(goalValue) } ?: "",
                    selectedUnit = initialUnit
                )
            }
        }
    }


    // Update the goal weight
    fun onGoalWeightChange(goalWeight: String) {
        _uiState.value = _uiState.value.copy(goalWeight = goalWeight)
    }


    // Saves and updates the goal weight
    fun saveGoalWeight() {
        viewModelScope.launch {
            val user = SessionManager.currentUser.first() ?: return@launch

            val weightValue = _uiState.value.goalWeight.toDoubleOrNull() ?: return@launch


            val existingGoal = goalRepository.getGoalForUserFlow(user.id).first()


            val goal = existingGoal?.copy(goalWeight = weightValue) ?: Goal(
                userId = user.id,
                goalWeight = weightValue
            )


            goalRepository.setGoal(goal)


            _uiState.update {
                it.copy(goalWeight = "")
            }
        }
    }
}
package com.example.dailyweighttracker_kotlin.ui.screens.add_weight

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyweighttracker_kotlin.data.GoalNotificationHelper
import com.example.dailyweighttracker_kotlin.data.SessionManager
import com.example.dailyweighttracker_kotlin.data.UserPreferencesDAO
import com.example.dailyweighttracker_kotlin.data.WeightRepository
import com.example.dailyweighttracker_kotlin.data.WeightUnit
import com.example.dailyweighttracker_kotlin.data.room.GoalDao
import com.example.dailyweighttracker_kotlin.data.room.Weight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate


// UI state for the add weight dialog
data class AddWeightUiState(
    val weight: String = "",
    val selectedUnit: WeightUnit = WeightUnit.LBS,
    val date: LocalDate = LocalDate.now()
)

// ViewModel for adding a weight
class AddWeightViewModel(
    private val weightsRepository: WeightRepository,
    private val userPreferencesDAO: UserPreferencesDAO,
    private val goalDao: GoalDao
) : ViewModel() {

    // Holds the current UI state
    private val _uiState = MutableStateFlow(AddWeightUiState())
    val uiState: StateFlow<AddWeightUiState> = _uiState.asStateFlow()

    init {

        // Load the user's weight unit preference
        viewModelScope.launch {

            val user = SessionManager.currentUser.first()
            if (user != null) {

                val initialUnit = userPreferencesDAO.getUnitPreference(user.id).first()
                _uiState.update { it.copy(selectedUnit = initialUnit) }
            }
        }
    }

    // Updates the weight input
    fun onWeightChange(weight: String) {
        _uiState.value = _uiState.value.copy(weight = weight)
    }

    // Saves new weight
    fun saveWeight(context: Context) {
        viewModelScope.launch {

            val user = SessionManager.currentUser.first() ?: return@launch

            val weightValue = uiState.value.weight.toDoubleOrNull() ?: return@launch


            val weight = Weight(
                userId = user.id,
                weight = weightValue,
                date = uiState.value.date
            )


            weightsRepository.insertWeight(weight)

            // Get the current goal weight
            val currentGoal = goalDao.getGoalForUserFlow(user.id).first()

            // Send notification if the new weight is less than or equal to the goal weight.
            if (currentGoal != null && weightValue <= currentGoal.goalWeight) {
                GoalNotificationHelper.sendGoalNotification(context, currentGoal.goalWeight)
            }
            // Clears input field after saving weight
            _uiState.update { it.copy(weight = "") }
        }
    }
}
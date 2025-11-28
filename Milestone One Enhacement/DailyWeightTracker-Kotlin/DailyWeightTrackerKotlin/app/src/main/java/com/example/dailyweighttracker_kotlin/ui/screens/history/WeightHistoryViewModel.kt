package com.example.dailyweighttracker_kotlin.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyweighttracker_kotlin.data.SessionManager
import com.example.dailyweighttracker_kotlin.data.UserPreferencesDAO
import com.example.dailyweighttracker_kotlin.data.WeightRepository
import com.example.dailyweighttracker_kotlin.data.WeightUnit
import com.example.dailyweighttracker_kotlin.data.room.Weight
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel for weight history screen

class WeightHistoryViewModel(
    private val weightRepository: WeightRepository,
    private val userPreferencesDAO: UserPreferencesDAO
) : ViewModel() {

    // UI state for weight history screen
    private val _uiState = MutableStateFlow(WeightHistoryState())
    val uiState: StateFlow<WeightHistoryState> = _uiState.asStateFlow()

    private var job: Job? = null

    init {
        // Load data when user logs in
        viewModelScope.launch {
            SessionManager.currentUser.collect { user ->
                job?.cancel()
                if (user == null) {
                    _uiState.value = WeightHistoryState()
                } else {
                    job = launch {
                        combine(
                            weightRepository.getAllWeightsStream(user.id),
                            userPreferencesDAO.getUnitPreference(user.id)
                        ) { weights, unit ->
                            _uiState.value.copy(
                                weights = weights.map { it.toWeightEntry(unit) },
                                weightUnit = unit
                            )
                        }.collect { _uiState.value = it }
                    }
                }
            }
        }
    }

    // Handles events from the UI
    fun onEvent(event: WeightHistoryEvent) {
        when (event) {
            is WeightHistoryEvent.OnDeleteWeight -> {
                _uiState.update {
                    it.copy(
                        showDeleteConfirmation = true,
                        weightToDelete = event.weight
                    )
                }
            }
            is WeightHistoryEvent.OnConfirmDelete -> {
                viewModelScope.launch {
                    _uiState.value.weightToDelete?.let { weightRepository.deleteWeight(it.toWeight(_uiState.value.weightUnit)) }
                    _uiState.update { it.copy(showDeleteConfirmation = false, weightToDelete = null) }
                }
            }
            is WeightHistoryEvent.OnDismissDelete -> {
                _uiState.update { it.copy(showDeleteConfirmation = false, weightToDelete = null) }
            }
            is WeightHistoryEvent.OnEditWeight -> {
                _uiState.update { it.copy(weightToEdit = event.weight, editDialogNewWeight = event.weight.weight) }
            }
            is WeightHistoryEvent.OnDismissEdit -> {
                _uiState.update { it.copy(weightToEdit = null) }
            }
            is WeightHistoryEvent.OnConfirmEdit -> {
                viewModelScope.launch {
                    val updatedEntry = _uiState.value.weightToEdit?.copy(weight = _uiState.value.editDialogNewWeight)
                    updatedEntry?.let { weightRepository.updateWeight(it.toWeight(_uiState.value.weightUnit)) }
                    _uiState.update { it.copy(weightToEdit = null) }
                }
            }
            is WeightHistoryEvent.OnEditDialogNewWeightChanged -> {
                _uiState.update { it.copy(editDialogNewWeight = event.newWeight) }
            }
        }
    }
}

// Converts room weight to UI weight entry
private fun Weight.toWeightEntry(unit: WeightUnit): WeightEntry {
    val weightInUnit = when (unit) {
        WeightUnit.LBS -> weight
        WeightUnit.KGS -> weight
    }
    return WeightEntry(
        id = id,
        weight = "%.1f".format(weightInUnit),
        date = date
    )
}

// Converts weight entry to room weight
private fun WeightEntry.toWeight(unit: WeightUnit): Weight {
    val currentUser = SessionManager.currentUser.value
    val weightInLbs = when (unit) {
        WeightUnit.LBS -> weight.toDouble()
        WeightUnit.KGS -> weight.toDouble()
    }
    return Weight(
        id = id,
        userId = currentUser?.id ?: 0,
        weight = weightInLbs,
        date = date
    )
}


// Events for edit and delete
sealed class WeightHistoryEvent {
    data class OnDeleteWeight(val weight: WeightEntry) : WeightHistoryEvent()
    object OnConfirmDelete : WeightHistoryEvent()
    object OnDismissDelete : WeightHistoryEvent()
    data class OnEditWeight(val weight: WeightEntry) : WeightHistoryEvent()
    object OnDismissEdit : WeightHistoryEvent()
    object OnConfirmEdit : WeightHistoryEvent()
    data class OnEditDialogNewWeightChanged(val newWeight: String) : WeightHistoryEvent()
}

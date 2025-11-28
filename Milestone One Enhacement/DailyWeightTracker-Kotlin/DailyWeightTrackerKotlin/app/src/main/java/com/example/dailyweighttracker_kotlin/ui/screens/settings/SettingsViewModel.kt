package com.example.dailyweighttracker_kotlin.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyweighttracker_kotlin.data.SessionManager
import com.example.dailyweighttracker_kotlin.data.UserPreferencesDAO
import com.example.dailyweighttracker_kotlin.data.UserRepository
import com.example.dailyweighttracker_kotlin.data.WeightUnit
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ViewModel for the settings screen
class SettingsViewModel(
    private val userRepository: UserRepository,
    private val userPreferencesDAO: UserPreferencesDAO
) : ViewModel() {

    // UI state for the settings screen
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    // Updates user preferences
    private var settingsJob: Job? = null

    init {
        viewModelScope.launch {
            // Get the current user
            SessionManager.currentUser.collect { user ->
                // Cancel the previous job if it exists
                settingsJob?.cancel()
                if (user == null) {
                    // If no user is logged in, reset the UI state
                    _uiState.value = SettingsState()
                } else {
                    // Load username
                    _uiState.update { it.copy(username = user.username) }
                    // Update the UI state with user preferences
                    settingsJob = launch {
                        combine(
                            userPreferencesDAO.getUnitPreference(user.id),
                            userPreferencesDAO.getGoalNotification(user.id)
                        ) { unit, goalNotification ->
                            _uiState.update { currentState ->
                                currentState.copy(
                                    weightUnit = unit,
                                    goalNotificationEnabled = goalNotification
                                )
                            }
                        }.collect { }
                    }
                }
            }
        }
    }

    // Handles events from the UI
    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnUsernameChanged -> _uiState.update { it.copy(newUsername = event.username) }
            is SettingsEvent.OnPasswordChanged -> _uiState.update { it.copy(newPassword = event.password) }
            is SettingsEvent.OnCurrentPasswordChanged -> _uiState.update { it.copy(currentPassword = event.password) }
            is SettingsEvent.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.password) }
            is SettingsEvent.OnWeightUnitChanged -> onWeightUnitChanged(event.unit)
            is SettingsEvent.OnGoalNotificationToggled -> onGoalNotificationToggled(event.enabled)
            is SettingsEvent.OnChangeUsername -> _uiState.update { it.copy(showChangeUsernameDialog = true) }
            is SettingsEvent.OnChangePassword -> _uiState.update { it.copy(showChangePasswordDialog = true) }
            is SettingsEvent.OnDismissDialog -> {
                _uiState.update {
                    it.copy(
                        showChangeUsernameDialog = false,
                        showChangePasswordDialog = false,
                        currentPassword = "",
                        newPassword = "",
                        confirmPassword = "",
                        newUsername = ""
                    )
                }
            }
            // Confirm username change
            is SettingsEvent.OnConfirmUsernameChange -> {
                viewModelScope.launch {
                    val currentUser = SessionManager.currentUser.value
                    if (currentUser != null && userRepository.verifyPassword(currentUser.id, _uiState.value.currentPassword)) {
                        userRepository.updateUser(currentUser.copy(username = _uiState.value.newUsername))
                        onEvent(SettingsEvent.OnDismissDialog)
                    }
                }
            }
            // Confirm password change
            is SettingsEvent.OnConfirmPasswordChange -> {
                viewModelScope.launch {
                    val currentUser = SessionManager.currentUser.value
                    if (currentUser != null && userRepository.verifyPassword(currentUser.id, _uiState.value.currentPassword)) {
                        if (_uiState.value.newPassword == _uiState.value.confirmPassword) {
                            userRepository.updateUser(currentUser.copy(password = _uiState.value.newPassword))
                            onEvent(SettingsEvent.OnDismissDialog)
                        }
                    }
                }
            }
            // Log out user
            is SettingsEvent.OnLogout -> {
                SessionManager.logout()
            }
        }
    }

    // Update weight unit
    private fun onWeightUnitChanged(newUnit: WeightUnit) {
        viewModelScope.launch {
            SessionManager.currentUser.value?.id?.let { userId ->
                userPreferencesDAO.updateUnitPreference(userId, newUnit)
            }
        }
    }

    // Update goal notification
    private fun onGoalNotificationToggled(enabled: Boolean) {
        viewModelScope.launch {
            SessionManager.currentUser.value?.id?.let { userId ->
                userPreferencesDAO.updateGoalNotification(userId, enabled)
            }
        }
    }
}

// UI state for the settings screen
data class SettingsState(
    val username: String = "",
    val newUsername: String = "",
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val weightUnit: WeightUnit = WeightUnit.LBS,
    val goalNotificationEnabled: Boolean = false,
    val showChangeUsernameDialog: Boolean = false,
    val showChangePasswordDialog: Boolean = false
)

// Events for the settings screen
sealed class SettingsEvent {
    data class OnUsernameChanged(val username: String) : SettingsEvent()
    data class OnPasswordChanged(val password: String) : SettingsEvent()
    data class OnCurrentPasswordChanged(val password: String) : SettingsEvent()
    data class OnConfirmPasswordChanged(val password: String) : SettingsEvent()
    data class OnWeightUnitChanged(val unit: WeightUnit) : SettingsEvent()
    data class OnGoalNotificationToggled(val enabled: Boolean) : SettingsEvent()
    object OnChangeUsername : SettingsEvent()
    object OnChangePassword : SettingsEvent()
    object OnDismissDialog : SettingsEvent()
    object OnConfirmUsernameChange : SettingsEvent()
    object OnConfirmPasswordChange : SettingsEvent()
    object OnLogout : SettingsEvent()
}

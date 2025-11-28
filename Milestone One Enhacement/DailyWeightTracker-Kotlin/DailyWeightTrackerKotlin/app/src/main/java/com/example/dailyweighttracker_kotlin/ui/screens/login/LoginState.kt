package com.example.dailyweighttracker_kotlin.ui.screens.login

// Login screen state
data class LoginState(
    val username: String = "",
    val password:  String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: Boolean = false
)

// Login events
sealed class LoginEvent {
    data class OnUsernameChanged(val username: String) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    object OnLoginClicked : LoginEvent()
    object OnCreateUserClicked : LoginEvent()
    object OnContinueAsGuestClicked : LoginEvent()
}
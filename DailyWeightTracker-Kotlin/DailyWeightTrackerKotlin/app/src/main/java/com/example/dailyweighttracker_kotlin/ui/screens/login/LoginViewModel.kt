package com.example.dailyweighttracker_kotlin.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailyweighttracker_kotlin.data.SessionManager
import com.example.dailyweighttracker_kotlin.data.UserRepository
import com.example.dailyweighttracker_kotlin.data.room.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// ViewModel For login screen
class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Login screen ui state

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    // Handle login events

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUsernameChanged -> {
                _uiState.value = _uiState.value.copy(username = event.username)
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.password)
            }
            LoginEvent.OnLoginClicked -> {
                login()
            }
            LoginEvent.OnCreateUserClicked -> {
                createUser()
            }
            LoginEvent.OnContinueAsGuestClicked -> {
                continueAsGuest()
            }
        }
    }

    // Logs the user in if the username and password are correct

    private fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val user = userRepository.getUserByUsername(_uiState.value.username).firstOrNull()
            if (user != null && user.password == _uiState.value.password) {
                // Login successful
                SessionManager.login(user)
                _uiState.value = _uiState.value.copy(loginSuccess = true)
            } else {
                // Login failed
                _uiState.value = _uiState.value.copy(error = "Invalid username or password")
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // Creates a new user

    private fun createUser() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val existingUser = userRepository.getUserByUsername(_uiState.value.username).firstOrNull()
            if (existingUser != null) {
                _uiState.value = _uiState.value.copy(error = "Username already exists")
            } else {
                val newUser = User(
                    username = _uiState.value.username,
                    password = _uiState.value.password
                )
                // Insert the new user into the database
                val newUserId = userRepository.insertUser(newUser)
                if (newUserId > -1) {
                   // Loads the new user from the database
                    val insertedUser = userRepository.getUserById(newUserId.toInt())
                    if (insertedUser != null) {
                        SessionManager.login(insertedUser)
                        _uiState.value = _uiState.value.copy(loginSuccess = true)
                    } else {
                        _uiState.value = _uiState.value.copy(error = "Failed to create user.")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(error = "Failed to create user.")
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // Logs the user in as a guest user

    private fun continueAsGuest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            var guestUser = userRepository.getGuestUser().firstOrNull()
            if (guestUser == null) {
                guestUser = User(username = "guest", password = "", isGuest = true)
                userRepository.insertUser(guestUser)
            }
            SessionManager.login(guestUser)
            _uiState.value = _uiState.value.copy(loginSuccess = true)
        }
    }
}

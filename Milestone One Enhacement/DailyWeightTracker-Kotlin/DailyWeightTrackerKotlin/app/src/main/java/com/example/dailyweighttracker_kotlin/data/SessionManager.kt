package com.example.dailyweighttracker_kotlin.data

import com.example.dailyweighttracker_kotlin.data.room.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Manages session for current user
object SessionManager {

    // Holds current user
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Logs the user in
    fun login(user: User) {
        _currentUser.value = user
    }

    // Logs the user out
    fun logout() {
        _currentUser.value = null
    }
}

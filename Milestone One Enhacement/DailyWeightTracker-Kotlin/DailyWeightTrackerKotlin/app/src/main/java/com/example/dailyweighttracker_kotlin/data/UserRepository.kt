package com.example.dailyweighttracker_kotlin.data

import com.example.dailyweighttracker_kotlin.data.room.User
import com.example.dailyweighttracker_kotlin.data.room.UserDao
import kotlinx.coroutines.flow.Flow

// Handles user database operations.
class UserRepository(private val userDao: UserDao) {

    // Inserts a new user and returns the generated ID.
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }

    // Updates an existing user.
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    // Gets user by username
    fun getUserByUsername(username: String): Flow<User?> {
        return userDao.getUserByUsername(username)
    }

    // Gets guest user
    fun getGuestUser(): Flow<User?> {
        return userDao.getGuestUser()
    }

    // Verifies password
    suspend fun verifyPassword(userId: Int, password: String): Boolean {
        val user = userDao.getUserById(userId)
        return user?.password == password
    }

    // Gets user by ID
    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
}

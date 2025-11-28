package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Insert a user into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User): Long

    // Update a user in the database
    @Update
    suspend fun updateUser(user: User)

    // Get values by username
    @Query("SELECT * FROM users WHERE username = :username")
    fun getUserByUsername(username: String): Flow<User?>

    // Get values for guest user
    @Query("SELECT * FROM users WHERE is_guest = 1")
    fun getGuestUser(): Flow<User?>

    // Get values by id
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?
}

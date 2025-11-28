package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Creates User entity in the database
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    @ColumnInfo(name = "is_guest")
    val isGuest: Boolean = false
)


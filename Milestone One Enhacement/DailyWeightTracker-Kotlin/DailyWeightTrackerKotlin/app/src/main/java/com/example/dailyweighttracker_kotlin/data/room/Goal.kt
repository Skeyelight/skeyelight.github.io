package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index // <-- YOU MUST ADD THIS IMPORT
import androidx.room.PrimaryKey

// Create entry for goals and attributes in database
@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)

// Creates goal data class
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val goalWeight: Double,
)
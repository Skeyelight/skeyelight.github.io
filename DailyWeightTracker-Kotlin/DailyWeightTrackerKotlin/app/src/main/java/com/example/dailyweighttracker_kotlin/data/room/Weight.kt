package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate


// Creates database entity for weights
@Entity(
    tableName = "weights",
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

// Creates weight data class
data class Weight(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val weight: Double,
    val date: LocalDate,
)
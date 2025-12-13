package com.example.dailyweighttracker_kotlin.ui.screens.history

import com.example.dailyweighttracker_kotlin.data.WeightUnit
import java.time.LocalDate

// UI State for weight history screen
data class WeightHistoryState(
    val weights: List<WeightEntry> = emptyList(),
    val weightUnit: WeightUnit = WeightUnit.LBS,
    val weightToDelete: WeightEntry? = null,
    val showDeleteConfirmation: Boolean = false,
    val weightToEdit: WeightEntry? = null,
    val editDialogNewWeight: String = ""
)

// Single weight entry
data class WeightEntry(
    val id: Int,
    val weight: String,
    val date: LocalDate
)

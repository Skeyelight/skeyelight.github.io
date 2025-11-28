package com.example.dailyweighttracker_kotlin.ui.screens.home

import com.example.dailyweighttracker_kotlin.data.WeightUnit
import com.example.dailyweighttracker_kotlin.data.room.Weight

// Holds UI state for Home screen
data class HomeUiState(
    val weightList: List<Weight> = emptyList(),
    val mostRecentWeight: Weight? = null,
    val goalWeight: Double? = null,
    val weightUnit: WeightUnit = WeightUnit.LBS,
    val showAddWeightDialog: Boolean = false,
    val showSetGoalDialog: Boolean = false
)


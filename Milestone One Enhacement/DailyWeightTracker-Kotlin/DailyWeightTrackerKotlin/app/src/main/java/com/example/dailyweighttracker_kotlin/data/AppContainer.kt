package com.example.dailyweighttracker_kotlin.data

import android.content.Context

// Defines shared components
interface AppContainer {
    val weightRepository: WeightRepository
    val userPreferencesDAO: UserPreferencesDAO
    val userRepository: UserRepository
    val goalRepository: GoalRepository
    val database: AppDatabase
}

// Provides shared components
class AppDataContainer(private val context: Context) : AppContainer {

    override val weightRepository: WeightRepository by lazy {
        WeightRepository(AppDatabase.getDatabase(context).weightDao())
    }

    override val userPreferencesDAO: UserPreferencesDAO by lazy {
        UserPreferencesDAO(context)
    }

    override val userRepository: UserRepository by lazy {
        UserRepository(AppDatabase.getDatabase(context).userDao())
    }

    // Add goal repository directly
    override val goalRepository: GoalRepository by lazy {
        GoalRepository(AppDatabase.getDatabase(context).goalDao())
    }

    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }
}


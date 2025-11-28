package com.example.dailyweighttracker_kotlin

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dailyweighttracker_kotlin.ui.screens.add_weight.AddWeightViewModel
import com.example.dailyweighttracker_kotlin.ui.screens.history.WeightHistoryViewModel
import com.example.dailyweighttracker_kotlin.ui.screens.home.HomeViewModel
import com.example.dailyweighttracker_kotlin.ui.screens.login.LoginViewModel
import com.example.dailyweighttracker_kotlin.ui.screens.set_goal.SetGoalViewModel
import com.example.dailyweighttracker_kotlin.ui.screens.settings.SettingsViewModel

// AppViewModelProvider provides dependencies for the app
object AppViewModelProvider {

    // Factory that creates the view models
    val Factory = viewModelFactory {

        // Initializer for WeightHistoryViewModel
        initializer {
            val application = weightApplication()
            val weightsRepository = application.container.weightRepository
            val userPreferencesDAO = application.container.userPreferencesDAO
            WeightHistoryViewModel(weightsRepository, userPreferencesDAO)
        }


        // Initializer for HomeViewModel
        initializer {
            val application = weightApplication()
            val weightsRepository = application.container.weightRepository
            val goalRepository = application.container.goalRepository
            val userPreferencesDAO = application.container.userPreferencesDAO
            HomeViewModel(weightsRepository, goalRepository, userPreferencesDAO)
        }


        // Initializer for AddWeightViewModel
        initializer {
            val application = weightApplication()
            val weightsRepository = application.container.weightRepository
            val userPreferencesDAO = application.container.userPreferencesDAO
            val goalDao = application.container.database.goalDao()
            AddWeightViewModel(weightsRepository, userPreferencesDAO,goalDao )
        }


        // Initializer for SetGoalViewModel
        initializer {
            val application = weightApplication()
            val goalRepository = application.container.goalRepository
            val userPreferencesDAO = application.container.userPreferencesDAO
            SetGoalViewModel(goalRepository, userPreferencesDAO)
        }


        // Initializer for LoginViewModel
        initializer {
            val application = weightApplication()
            val userRepository = application.container.userRepository
            LoginViewModel(userRepository)
        }


        // Initializer for SettingsViewModel
        initializer {
            val application = weightApplication()
            val userRepository = application.container.userRepository
            val userPreferencesDAO = application.container.userPreferencesDAO
            SettingsViewModel(userRepository, userPreferencesDAO)
        }
    }
}

// Extension function to retrieve the weight application
fun CreationExtras.weightApplication(): DailyWeightTrackerApplication = 
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DailyWeightTrackerApplication)
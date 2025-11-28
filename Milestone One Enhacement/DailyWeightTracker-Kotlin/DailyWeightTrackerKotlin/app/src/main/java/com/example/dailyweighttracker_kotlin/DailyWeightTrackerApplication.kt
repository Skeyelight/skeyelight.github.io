package com.example.dailyweighttracker_kotlin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.dailyweighttracker_kotlin.data.AppContainer
import com.example.dailyweighttracker_kotlin.data.AppDataContainer

// Main application class
class DailyWeightTrackerApplication : Application() {

    // Container for dependency injection
    lateinit var container: AppContainer

    // Called when the application is first created
    override fun onCreate() {
        super.onCreate()
        // Initialize the container
        container = AppDataContainer(this)
        createNotificationChannel()

    }

    // Creates a notification channel for the app
    private fun createNotificationChannel() {
        val name = "Weight Goal"
        val descriptionText = "Notifications for when you reach your weight goal"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("goal_channel", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager:
                NotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

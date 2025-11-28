package com.example.dailyweighttracker_kotlin.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dailyweighttracker_kotlin.R


// Creates notification for goal reached
object GoalNotificationHelper {

    fun sendGoalNotification(context: Context, goalWeight: Double) {

        val builder = NotificationCompat.Builder(context, "goal_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Goal Reached!")
            .setContentText("Congratulations! You reached your goal weight of $goalWeight.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = NotificationManagerCompat.from(context)

        // Check permission before showing notification
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission not granted, return without sending
            return
        }

        manager.notify(1001, builder.build())
    }
}

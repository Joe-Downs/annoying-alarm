package com.example.theannoyingalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Retrieve the Alarm object passed with the intent
        val alarm = (intent.getSerializableExtra("Alarm_object") as? Alarm)!!

        // Start the foreground service to play the alarm sound
        val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra("Alarm_object", alarm) // Passing the alarm object to the service
        }
        ContextCompat.startForegroundService(context, serviceIntent) // Start the foreground service

        // Check if the alarm should repeat
        val isRepeat = intent.getBooleanExtra("Alarm_repeat", false)
        if (isRepeat) {
            // If repeat is enabled, schedule the alarm for the same time next week

            // Get the alarm ID from the intent to recreate the alarm
            val alarmID = intent.getIntExtra("Alarm_ID", -1)
            if (alarmID == -1) {
                return // Exit if no valid alarm ID is found
            }

            // Set the time for the next alarm (1 week later from the current time)
            val nextAlarmTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis() // Get current time
                add(Calendar.WEEK_OF_YEAR, 1) // Add 1 week to the current time
                set(Calendar.SECOND, 0) // Reset seconds to 0 for cleaner scheduling
            }

            // Set up the AlarmManager to schedule the next alarm
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("Alarm_repeat", true) // Indicate the alarm should repeat
                putExtra("Alarm_ID", alarmID) // Pass the alarm ID
                putExtra("Alarm_object", alarm) // Pass the alarm object
            }

            // Create a PendingIntent to trigger the AlarmReceiver at the scheduled time
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmID, // Use alarm ID as request code
                alarmIntent, // The intent to trigger
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Flags for updating and immutability
            )

            // Schedule the alarm with AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, // Wake the device even if it's idle
                nextAlarmTime.timeInMillis, // The time for the next alarm
                pendingIntent // The PendingIntent that triggers the AlarmReceiver
            )
        } else {
            // If no repeat is set, mark the alarm as inactive and update the alarm state

            alarm.isActive = false // Set the alarm's active status to false

            // Get the ViewModel to update the alarm state in the database
            val alarmViewModel = ViewModelHolder.alarmViewModel
            alarmViewModel.updateAlarm(alarm) // Update the alarm's state in the database
        }
    }
}

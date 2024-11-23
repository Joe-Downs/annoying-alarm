package com.example.theannoyingalarm

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Create an intent to open the AlarmActivity
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        context.startActivity(fullScreenIntent)

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        val notification =NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.baseline_access_alarms_24)
            .setContentTitle("Alarm")
            .setContentText("Time to wake up!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .build()

        // Build notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"

        // Create a notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Alarm notifications"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Show the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted; send the notification

                notificationManager.notify(1, notification)
            } else {
                // Handle the case where the permission is not granted
                Log.e("NotificationPermission", "POST_NOTIFICATIONS permission not granted")
            }
        } else {
            // For Android 12 and below, no explicit permission is needed
            notificationManager.notify(1, notification)
        }

        // Set repeat if allow repeat
        val isRepeat = intent.getBooleanExtra("Alarm_repeat", false)
        if (isRepeat) {
            val alarmID = intent.getIntExtra("Alarm_ID", -1)
            if (alarmID == -1) {
                return
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val nextAlarmTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.WEEK_OF_YEAR, 1) // Add 1 week to the current time
                set(Calendar.SECOND, 0)
            }

            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                alarmID,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime.timeInMillis,
                pendingIntent
            )
        } else {
            val alarm = (intent.getSerializableExtra("Alarm_object") as? Alarm)!!
            alarm.isActive = false

            val alarmViewModel = ViewModelHolder.alarmViewModel
            alarmViewModel.updateAlarm(alarm)
        }
    }
}
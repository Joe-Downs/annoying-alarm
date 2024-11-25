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
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.AUDIO_SERVICE
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onReceive(context: Context, intent: Intent) {
        val alarm = (intent.getSerializableExtra("Alarm_object") as? Alarm)!!

        // Start the foreground service to play the alarm sound
        val serviceIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra("Alarm_object", alarm)
        }
        ContextCompat.startForegroundService(context, serviceIntent)

        // Set repeat if allow repeat
        val isRepeat = intent.getBooleanExtra("Alarm_repeat", false)
        if (isRepeat) {
            // Recreate the alarm for next week
            val alarmID = intent.getIntExtra("Alarm_ID", -1)
            if (alarmID == -1) {
                return
            }

            val nextAlarmTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.WEEK_OF_YEAR, 1) // Add 1 week to the current time
                set(Calendar.SECOND, 0)
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("Alarm_repeat", true)
                putExtra("Alarm_ID", alarmID)
                putExtra("Alarm_object", alarm)
            }
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

            alarm.isActive = false

            val alarmViewModel = ViewModelHolder.alarmViewModel
            alarmViewModel.updateAlarm(alarm)
        }
    }
}
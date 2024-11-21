package com.example.theannoyingalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "Alarm is ringing!", Toast.LENGTH_LONG).show()

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
        }
    }
}
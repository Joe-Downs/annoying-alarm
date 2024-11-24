package com.example.theannoyingalarm

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AlarmActivity: AppCompatActivity() {
    private lateinit var alarmReceiver: AlarmReceiver
    private val scheduler = Executors.newSingleThreadScheduledExecutor()

    private lateinit var alarmName: TextView
    private lateinit var alarmTime: TextView
    private lateinit var alarmAm: TextView
    private lateinit var snoozeButton: Button
    private lateinit var dismissButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_activity)

        alarmName = findViewById(R.id.alarm_name)
        alarmTime = findViewById(R.id.alarm_time)
        alarmAm = findViewById(R.id.alarm_am_pm)
        snoozeButton = findViewById(R.id.snooze_button)
        dismissButton = findViewById(R.id.dismiss_button)

        alarmReceiver = AlarmReceiver()
        startUpdateTime()
        dismissButton.setOnClickListener {
            // Stop the foreground service
            val serviceIntent = Intent(this, AlarmForegroundService::class.java)
            stopService(serviceIntent)

            // Close the activity
            finish()
            finish()
        }

        snoozeButton.setOnClickListener {

        }
    }

    private fun startUpdateTime() {
        // Schedule a task to update the time every second
        scheduler.scheduleWithFixedDelay({
            // Get the current time
            val calendar = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
            val formattedTime = timeFormat.format(calendar.time)

            val isAm = calendar.get(Calendar.AM_PM) == Calendar.AM
            runOnUiThread {
                alarmTime.text = formattedTime
                alarmAm.text = if (isAm) getString(R.string.am) else getString(R.string.pm)
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Shut down the scheduler when the activity is destroyed to prevent memory leaks
        scheduler.shutdown()

        // Stop the foreground service
        val serviceIntent = Intent(this, AlarmForegroundService::class.java)
        stopService(serviceIntent)
    }
}
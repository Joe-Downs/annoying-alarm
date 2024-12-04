package com.example.theannoyingalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class AlarmActivity: AppCompatActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var alarmReceiver: AlarmReceiver
    private lateinit var alarm: Alarm
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

        // Get the alarm and make sure it is not null
        val tempAlarm = (intent.getSerializableExtra("Alarm_object") as? Alarm)
        if (tempAlarm == null) {
            dismissAlarm()
        } else {
            alarm = tempAlarm
        }

        alarmReceiver = AlarmReceiver()

        // Display alarm name
        alarmName.text = alarm.name

        // Set up a scheduler to display current time
        startUpdateTime()

        dismissButton.setOnClickListener {
            showPuzzle(false)
        }

        snoozeButton.setOnClickListener {
            showPuzzle(true)
        }

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val isComplete = result.data?.getBooleanExtra(IS_PUZZLE_COMPLETE_KEY, false) ?: false
                if (isComplete) {
                    val isSnooze = result.data?.getBooleanExtra(IS_SNOOZE_KEY, false) ?: false
                    if (isSnooze) {
                        snoozeAlarm()
                    }
                    dismissAlarm()
                }
            }
        }
    }

    private fun snoozeAlarm() {
        // Set another alarm that will ring in 10 minutes
        val snoozeAlarm = Calendar.getInstance() // Get the current time
        snoozeAlarm.add(Calendar.MINUTE, 10) // Add 10 minutes

        // create unique id for snooze alarm, last digit is 0 represent the snooze alarm for this alarm
        val alarmID = alarm.alarmID * 10

        // Set snooze alarm for the next 10 minutes
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("Alarm_repeat", false)
            putExtra("Alarm_ID", alarmID)
            putExtra("Alarm_object", alarm)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmID,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            snoozeAlarm.timeInMillis,
            pendingIntent
        )
    }

    private fun dismissAlarm() {
        // Stop the foreground service
        val serviceIntent = Intent(this, AlarmForegroundService::class.java)
        stopService(serviceIntent)

        // Close the activity
        finish()
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

    private fun showPuzzle(isSnooze: Boolean) {
        val randomPuzzle = Puzzle.values().random()
        when (randomPuzzle) {
            Puzzle.SlidingPuzzle -> {
                val intent = Intent(this, SlidingPuzzle::class.java).apply {
                    putExtra(IS_SNOOZE_KEY, isSnooze)
                }

                activityResultLauncher.launch(intent)
            }

            Puzzle.AdditionPuzzle -> {
                val intent = Intent(this, AdditionPuzzle::class.java).apply {
                    putExtra(IS_SNOOZE_KEY, isSnooze)
                }

                activityResultLauncher.launch(intent)
            }

            Puzzle.RGBGuruPuzzle -> {
                val intent = Intent(this, RGBGuruPuzzle::class.java).apply {
                    putExtra(IS_SNOOZE_KEY, isSnooze)
                }

                activityResultLauncher.launch(intent)
            }
        }

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
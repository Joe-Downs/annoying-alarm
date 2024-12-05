package com.example.theannoyingalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true) var alarmID: Int = 0, // Unique ID for each alarm (auto-generated)
    @ColumnInfo(name = "Name") var name: String = "", // Name of the alarm (e.g., "Wake up")
    @ColumnInfo(name = "Hour") var hour: Int = 0, // Hour part of the alarm time (24-hour format)
    @ColumnInfo(name = "Minute")  var min: Int = 0, // Minute part of the alarm time
    @ColumnInfo(name = "Is_AM")  var isAm: Boolean = true, // Boolean to check if the time is AM or PM
    @ColumnInfo(name = "Is_Active")  var isActive: Boolean = false, // Boolean to check if the alarm is active
    @ColumnInfo(name = "Repeat")  var repeat: String = "" // Days of the week the alarm repeats (e.g., "Mon,Wed,Fri")
) : Serializable {

    // Function to set an alarm
    fun setAlarm(context: Context) {
        // Check Android version to handle scheduling of exact alarms (Android 12+ requires permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to enable exact alarms if not granted
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            } else {
                // If permission granted, set the alarm
                setExactAlarm(context)
            }
        } else {
            // For Android versions below 12, set the alarm directly
            setExactAlarm(context)
        }
    }

    // Function to cancel the alarm
    fun cancelAlarm(context: Context) {
        // Cancel alarms for the days set in the repeat field
        val idList = Array(8) { index -> if (index == 0) alarmID else alarmID * 10 + index }
        idList.forEach { id ->
            cancelAlarmWithID(context, id)
        }
    }

    // Helper function to cancel an alarm with a specific ID
    private fun cancelAlarmWithID(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Cancel the pending alarm based on the ID
        alarmManager.cancel(pendingIntent)
    }

    // Function to set an exact alarm, considering repetition
    private fun setExactAlarm(context: Context) {
        if (this.repeat.isEmpty()) {
            setSingleAlarm(context) // Set a single alarm if no repeat days are set
        } else {
            // For each repeat day (e.g., "Mon", "Tue"), schedule a weekly alarm
            this.repeat.forEach { day ->
                val weekDay = WeekDay.entries.find { it.short.equals(day, true) }
                if (weekDay != null) {
                    setWeeklyAlarm(context, weekDay) // Set a weekly alarm for the specified day
                }
            }
        }
    }

    // Function to set a weekly alarm for a specific day of the week
    private fun setWeeklyAlarm(context: Context, dayOfWeek: WeekDay) {
        // Create a unique alarm ID for each day (appends the day of the week)
        val id = this.alarmID * 10 + dayOfWeek.int
        val calendar = Calendar.getInstance()

        // Set the alarm time
        calendar.set(Calendar.HOUR_OF_DAY, this.hour)
        calendar.set(Calendar.MINUTE, this.min)
        calendar.set(Calendar.SECOND, 0)

        // Check if the alarm time is in the past for today and adjust to the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
        }

        // Adjust the calendar to the specified weekday
        while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek.int) {
            calendar.add(Calendar.DAY_OF_WEEK, 1) // Move to the next weekday
        }

        // Set the alarm using AlarmManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("Alarm_repeat", true)
            putExtra("Alarm_ID", id)
            putExtra("Alarm_object", this@Alarm)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    // Function to set a single alarm (without repeat)
    private fun setSingleAlarm(context: Context) {
        val calendar = Calendar.getInstance()

        // Set the alarm time considering AM/PM
        calendar.set(Calendar.HOUR_OF_DAY, if(isAm) this.hour else this.hour + 12)
        calendar.set(Calendar.MINUTE, this.min)
        calendar.set(Calendar.SECOND, 0)

        // Check if the alarm time is in the past and adjust to the next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
        }

        // Set the alarm using AlarmManager
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("Alarm_repeat", false)
            putExtra("Alarm_ID", this@Alarm.alarmID)
            putExtra("Alarm_object", this@Alarm)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            this.alarmID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}

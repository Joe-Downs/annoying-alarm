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
    @PrimaryKey(autoGenerate = true) var alarmID: Int = 0,
    @ColumnInfo(name = "Name") var name: String = "",
    @ColumnInfo(name = "Hour") var hour: Int = 0,
    @ColumnInfo(name = "Minute")  var min: Int = 0,
    @ColumnInfo(name = "Is_AM")  var isAm: Boolean = true,
    @ColumnInfo(name = "Is_Active")  var isActive: Boolean = false,
    @ColumnInfo(name = "Repeat")  var repeat: String = ""
) : Serializable {
    fun setAlarm(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to enable exact alarms
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
            } else {
                // Permission granted; proceed to set an alarm
                setExactAlarm(context)
            }
        } else {
            // For Android versions below 12, set the alarm directly
            setExactAlarm(context)
        }
    }

    fun cancelAlarm(context: Context) {
        // Cancel every alarm, even if it did not exist
        val idList = Array(8) { index -> if (index == 0) alarmID else alarmID * 10 + index }
        idList.forEach { id ->
            cancelAlarmWithID(context, id)
        }
    }

    // Cancel alarm with provided ID
    private fun cancelAlarmWithID(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    private fun setExactAlarm(context: Context) {
        if (this.repeat.isEmpty()) {
            setSingleAlarm(context)
        } else {
            this.repeat.forEach { day ->
                val weekDay = WeekDay.entries.find { it.short.equals(day, true)}
                if (weekDay != null) {
                    setWeeklyAlarm(context, weekDay)
                }
            }
        }
    }

    // Function to set weekly alarm for a day of the week
    private fun setWeeklyAlarm(context: Context, dayOfWeek: WeekDay) {
        // Create unique id for this alarm
        val id = this.alarmID * 10 + dayOfWeek.int
        val calendar = Calendar.getInstance()

        // Set the time
        calendar.set(Calendar.HOUR_OF_DAY, this.hour)
        calendar.set(Calendar.MINUTE, this.min)
        calendar.set(Calendar.SECOND, 0)

        // Check if the alarm time is in the past for today
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
        }

        // Adjust the calendar to the selected day of the week
        while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek.int) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    private fun setSingleAlarm(context: Context) {
        val calendar = Calendar.getInstance()

        // Set the time
        calendar.set(Calendar.HOUR_OF_DAY, if(isAm) this.hour else this.hour + 12)
        calendar.set(Calendar.MINUTE, this.min)
        calendar.set(Calendar.SECOND, 0)

        // Check if the alarm time is in the past for today
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
        }

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
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}
package com.example.theannoyingalarm

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class AlarmEdit : AppCompatActivity() {

    // Declare UI elements and variables used in this activity
    private lateinit var alarm: Alarm
    private lateinit var timePicker: TimePicker
    private lateinit var alarmLabel: EditText
    private lateinit var repeatButton: Button
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var titleLabel: TextView

    // Declare the ActivityResultLauncher for handling result from AlarmRepeat activity
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    // Used to track the position of the alarm (for editing purposes)
    private var position = -1
    // Flag to indicate if a new alarm is being added or an existing alarm is being edited
    private var isAdd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_edit)  // Set the layout for the activity

        // Register the ActivityResultLauncher to handle the result from AlarmRepeat activity
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra(REPEAT_KEY)  // Get repeat information
                if (data != null) {
                    repeatButton.text = getAttributedRepeatText(data)  // Set button text
                    alarm.repeat = data  // Update the repeat value in the alarm object
                } else {
                    // Show toast if repeat is canceled
                    Toast.makeText(this, "Set Repeat Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Retrieve the Alarm object passed through the intent and check if it's an add or edit action
        alarm = (intent.getSerializableExtra(ALARM_KEY) as? Alarm)!!
        isAdd = intent.getBooleanExtra(ADD_ALARM_KEY, false)

        // Initialize UI components
        timePicker = findViewById(R.id.alarmTimePicker)
        alarmLabel = findViewById(R.id.alarmName)
        repeatButton = findViewById(R.id.alarmRepeatButton)
        cancelButton = findViewById(R.id.alarmEditCancel)
        saveButton = findViewById(R.id.alarmEditDone)
        titleLabel = findViewById(R.id.alarmEditTitle)

        // Set the time on the TimePicker based on the alarm's time (considering AM/PM)
        timePicker.hour = alarm.hour + (if (alarm.isAm) 0 else 12)
        timePicker.minute = alarm.min

        // Set the alarm name and repeat button text
        alarmLabel.setText(alarm.name)
        repeatButton.text = getAttributedRepeatText(alarm.repeat)

        // Set click listeners for buttons
        repeatButton.setOnClickListener {
            repeatButtonClicked()
        }

        cancelButton.setOnClickListener {
            cancelClicked()
        }

        saveButton.setOnClickListener {
            doneClicked()
        }

        // Update alarm time whenever the time on the TimePicker changes
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            alarm.hour = if (hour > 12) hour - 12 else hour  // Adjust for 12-hour format
            alarm.min = minute  // Update minute value
            alarm.isAm = (hour < 12)  // Update AM/PM based on hour
        }

        // Set the title of the activity based on whether it's adding or editing an alarm
        if (isAdd) {
            titleLabel.text = getString(R.string.add_alarm)
        } else {
            titleLabel.text = getString(R.string.edit_alarm)
        }
    }

    // Handle the repeat button click, which launches AlarmRepeat activity
    private fun repeatButtonClicked() {
        val intent = Intent(this, AlarmRepeat::class.java).apply {
            putExtra(REPEAT_KEY, alarm.repeat)  // Pass the current repeat setting to the AlarmRepeat activity
        }
        activityResultLauncher.launch(intent)  // Launch AlarmRepeat activity and wait for the result
    }

    // Handle cancel button press (finish the activity without saving)
    private fun cancelClicked() {
        finish()  // Close the current activity and return to the previous one
    }

    // Handle save button press (save changes and return the result)
    private fun doneClicked() {
        alarm.name = alarmLabel.text.toString()  // Update the alarm's name with the text from the EditText

        // Create a result intent with the updated alarm and whether it's an add or edit operation
        val resultIntent = Intent().apply {
            putExtra(ALARM_KEY, alarm)
            putExtra(ADD_ALARM_KEY, isAdd)
        }
        setResult(RESULT_OK, resultIntent)  // Set the result as OK and pass the data back
        finish()  // Finish the activity and return to the previous one
    }
}

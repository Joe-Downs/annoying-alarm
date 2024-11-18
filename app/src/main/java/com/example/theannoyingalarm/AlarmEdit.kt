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

class AlarmEdit: AppCompatActivity() {
    private lateinit var alarm: Alarm
    private lateinit var timePicker: TimePicker
    private lateinit var alarmLabel: EditText
    private lateinit var repeatButton: Button
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var titleLabel: TextView

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var position = -1
    private var isAdd = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_edit)

        // Register the ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra(REPEAT_KEY)
                if (data != null) {
                    repeatButton.text = getAttributedRepeatText(data)
                    alarm.repeat = data
                } else {
                    Toast.makeText(this, "Set Repeat Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }

        alarm = (intent.getSerializableExtra(ALARM_KEY) as? Alarm)!!
        isAdd = intent.getBooleanExtra(ADD_ALARM_KEY, false)

        timePicker = findViewById(R.id.alarmTimePicker)
        alarmLabel = findViewById(R.id.alarmName)
        repeatButton = findViewById(R.id.alarmRepeatButton)
        cancelButton = findViewById(R.id.alarmEditCancel)
        saveButton = findViewById(R.id.alarmEditDone)
        titleLabel = findViewById(R.id.alarmEditTitle)

        timePicker.hour = alarm.hour + (if (alarm.isAm) 0 else 12)
        timePicker.minute = alarm.min

        alarmLabel.setText(alarm.name)
        repeatButton.text = getAttributedRepeatText(alarm.repeat)

        repeatButton.setOnClickListener {
            repeatButtonClicked()
        }

        cancelButton.setOnClickListener {
            cancelClicked()
        }

        saveButton.setOnClickListener {
            doneClicked()
        }

        timePicker.setOnTimeChangedListener { _, hour, minute ->
            alarm.hour = if (hour > 12) hour - 12 else hour
            alarm.min = timePicker.minute
            alarm.isAm = (timePicker.hour < 12)
        }

        // Set view title depend on is adding alarm or editing alarm
        if (isAdd) {
            titleLabel.text = getString(R.string.add_alarm)
        } else {
            titleLabel.text = getString(R.string.edit_alarm)
        }
    }

    private fun repeatButtonClicked() {
        val intent = Intent(this, AlarmRepeat::class.java).apply {
            putExtra(REPEAT_KEY, alarm.repeat)
        }
        activityResultLauncher.launch(intent)
    }

    // handle Cancel button press
    private fun cancelClicked() {
        finish()
    }

    // handle Save button press
    private fun doneClicked() {
        alarm.name = alarmLabel.text.toString()

        val resultIntent = Intent().apply {
            putExtra(ALARM_KEY, alarm)
            putExtra(ADD_ALARM_KEY, isAdd)
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
package com.example.theannoyingalarm

import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Typeface
import android.widget.Button

class AlarmEdit: AppCompatActivity() {
    private lateinit var alarm: Alarm
    private lateinit var timePicker: TimePicker
    private lateinit var alarmLabel: EditText
    private lateinit var repeatButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alarm_edit)

        alarm = (intent.getSerializableExtra("Alarm") as? Alarm)!!
        timePicker = findViewById(R.id.alarmTimePicker)
        alarmLabel = findViewById(R.id.alarmName)
        repeatButton = findViewById(R.id.alarmRepeatButton)

        timePicker.hour = alarm.hour + (if (alarm.isAm) 0 else 12)
        timePicker.minute = alarm.min

        alarmLabel.setText(alarm.name)
        repeatButton.text = getAttributedRepeatText(alarm.repeat)
    }

    private fun getAttributedRepeatText(repeat: String): SpannableString {
        if (repeat.isEmpty()) {
            return SpannableString("Never")
        }

        if (repeat.length == 7) {
            return SpannableString("Always")
        }

        val result = SpannableString("S M T W T F S ")

        // Check if repeat on Sunday
        if (repeat.contains('N', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Check if repeat on Monday
        if (repeat.contains('M', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 2, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Check if repeat on Tuesday
        if (repeat.contains('T', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 4, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Check if repeat on Wednesday
        if (repeat.contains('W', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 6, 7, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Check if repeat on Thursday
        if (repeat.contains('U', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 8, 9, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Check if repeat on Friday
        if (repeat.contains('F', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 10, 11, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Check if repeat on Saturday
        if (repeat.contains('S', true)) {
            result.setSpan(StyleSpan(Typeface.BOLD), 12, 13, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return result
    }
}
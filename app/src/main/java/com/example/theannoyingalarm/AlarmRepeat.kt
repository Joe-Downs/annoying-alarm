package com.example.theannoyingalarm

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AlarmRepeat: AppCompatActivity() {
    private lateinit var repeatMonday: Button
    private lateinit var repeatTuesday: Button
    private lateinit var repeatWednesday: Button
    private lateinit var repeatThursday: Button
    private lateinit var repeatFriday: Button
    private lateinit var repeatSaturday: Button
    private lateinit var repeatSunday: Button

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private var img: Drawable? = null

    private var repeatDates = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_repeat)

        repeatMonday = findViewById(R.id.repeatMonday)
        repeatTuesday = findViewById(R.id.repeatTuesday)
        repeatWednesday = findViewById(R.id.repeatWednesday)
        repeatThursday = findViewById(R.id.repeatThursday)
        repeatFriday = findViewById(R.id.repeatFriday)
        repeatSaturday = findViewById(R.id.repeatSaturday)
        repeatSunday = findViewById(R.id.repeatSunday)

        saveButton = findViewById(R.id.repeatDone)
        cancelButton = findViewById(R.id.repeatCancel)

        repeatDates = intent.getStringExtra(REPEAT_KEY) ?: ""

        val size = dpToPx(24f)
        val color = ContextCompat.getColor(this, R.color.system_blue)
        img = ContextCompat.getDrawable(this, R.drawable.icons8_done)
        img?.setTint(color)
        img?.setBounds(0,0,size,size)

        setUpButtons()

        repeatMonday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.MONDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        repeatTuesday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.TUESDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        repeatWednesday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.WEDNESDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        repeatThursday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.THURSDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        repeatFriday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.FRIDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        repeatSaturday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.SATURDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        repeatSunday.setOnClickListener {
            val button = it as Button
            val day = WeekDay.SUNDAY
            if (repeatDates.contains(day.short)) {
                removeDate(button, day)
            } else {
                setDate(button, day)
            }
        }

        saveButton.setOnClickListener {
            saveClicked()
        }

        cancelButton.setOnClickListener {
            cancelClicked()
        }
    }

    private fun removeDate(button: Button, date: WeekDay) {
        button.setCompoundDrawables(null, null, null, null)
        repeatDates = repeatDates.replace(date.short.toString(), "")
    }

    private fun setDate(button: Button, date: WeekDay) {
        button.setCompoundDrawables(null, null, img, null)
        repeatDates += date.short
    }

    private fun setUpButtons() {
        if (repeatDates.contains(WeekDay.MONDAY.short)) {
            repeatMonday.setCompoundDrawables(null, null, img, null)
        }
        if (repeatDates.contains(WeekDay.TUESDAY.short)) {
            repeatTuesday.setCompoundDrawables(null, null, img, null)
        }
        if (repeatDates.contains(WeekDay.WEDNESDAY.short)) {
            repeatWednesday.setCompoundDrawables(null, null, img, null)
        }
        if (repeatDates.contains(WeekDay.THURSDAY.short)) {
            repeatThursday.setCompoundDrawables(null, null, img, null)
        }
        if (repeatDates.contains(WeekDay.FRIDAY.short)) {
            repeatFriday.setCompoundDrawables(null, null, img, null)
        }
        if (repeatDates.contains(WeekDay.SATURDAY.short)) {
            repeatSaturday.setCompoundDrawables(null, null, img, null)
        }
        if (repeatDates.contains(WeekDay.SUNDAY.short)) {
            repeatSunday.setCompoundDrawables(null, null, img, null)
        }
    }

    private fun saveClicked() {
//        Toast.makeText(applicationContext, repeatDates, Toast.LENGTH_SHORT).show()
        val resultIntent = Intent()
        resultIntent.putExtra(REPEAT_KEY, repeatDates)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun cancelClicked() {
        finish()
    }

    private fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}


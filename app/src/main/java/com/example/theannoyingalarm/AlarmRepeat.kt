package com.example.theannoyingalarm

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AlarmRepeat: AppCompatActivity() {

    // Declare buttons for each day of the week
    private lateinit var repeatMonday: Button
    private lateinit var repeatTuesday: Button
    private lateinit var repeatWednesday: Button
    private lateinit var repeatThursday: Button
    private lateinit var repeatFriday: Button
    private lateinit var repeatSaturday: Button
    private lateinit var repeatSunday: Button

    // Declare save and cancel buttons
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    // Drawable for the checkmark icon (used to mark selected days)
    private var img: Drawable? = null

    // String to hold the selected repeat days (e.g., "MTW" for Monday, Tuesday, Wednesday)
    private var repeatDates = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_repeat)

        // Initialize the buttons for each day of the week
        repeatMonday = findViewById(R.id.repeatMonday)
        repeatTuesday = findViewById(R.id.repeatTuesday)
        repeatWednesday = findViewById(R.id.repeatWednesday)
        repeatThursday = findViewById(R.id.repeatThursday)
        repeatFriday = findViewById(R.id.repeatFriday)
        repeatSaturday = findViewById(R.id.repeatSaturday)
        repeatSunday = findViewById(R.id.repeatSunday)

        // Initialize the save and cancel buttons
        saveButton = findViewById(R.id.repeatDone)
        cancelButton = findViewById(R.id.repeatCancel)

        // Retrieve the repeat days passed from the previous activity
        repeatDates = intent.getStringExtra(REPEAT_KEY) ?: ""

        // Set the size and color of the checkmark icon
        val size = dpToPx(24f) // Convert dp to px for the size of the checkmark
        val color = ContextCompat.getColor(this, R.color.system_blue) // Set the color for the checkmark
        img = ContextCompat.getDrawable(this, R.drawable.icons8_done) // Load the checkmark icon
        img?.setTint(color) // Tint the icon with the chosen color
        img?.setBounds(0, 0, size, size) // Set the bounds for the icon

        // Set up the buttons based on the current repeat days
        setUpButtons()

        // Set onClickListeners for each day of the week
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

        // Set up the save and cancel buttons
        saveButton.setOnClickListener {
            saveClicked() // Save the selected repeat days and close the activity
        }

        cancelButton.setOnClickListener {
            cancelClicked() // Close the activity without saving
        }
    }

    // Remove a date (day) from the repeatDates string and update the button's icon
    private fun removeDate(button: Button, date: WeekDay) {
        button.setCompoundDrawables(null, null, null, null) // Remove the checkmark icon
        repeatDates = repeatDates.replace(date.short.toString(), "") // Remove the day from the repeatDates string
    }

    // Add a date (day) to the repeatDates string and update the button's icon
    private fun setDate(button: Button, date: WeekDay) {
        button.setCompoundDrawables(null, null, img, null) // Set the checkmark icon
        repeatDates += date.short // Add the day to the repeatDates string
    }

    // Set the button icons based on the current repeatDates string
    private fun setUpButtons() {
        if (repeatDates.contains(WeekDay.MONDAY.short)) {
            repeatMonday.setCompoundDrawables(null, null, img, null) // Show checkmark for Monday
        }
        if (repeatDates.contains(WeekDay.TUESDAY.short)) {
            repeatTuesday.setCompoundDrawables(null, null, img, null) // Show checkmark for Tuesday
        }
        if (repeatDates.contains(WeekDay.WEDNESDAY.short)) {
            repeatWednesday.setCompoundDrawables(null, null, img, null) // Show checkmark for Wednesday
        }
        if (repeatDates.contains(WeekDay.THURSDAY.short)) {
            repeatThursday.setCompoundDrawables(null, null, img, null) // Show checkmark for Thursday
        }
        if (repeatDates.contains(WeekDay.FRIDAY.short)) {
            repeatFriday.setCompoundDrawables(null, null, img, null) // Show checkmark for Friday
        }
        if (repeatDates.contains(WeekDay.SATURDAY.short)) {
            repeatSaturday.setCompoundDrawables(null, null, img, null) // Show checkmark for Saturday
        }
        if (repeatDates.contains(WeekDay.SUNDAY.short)) {
            repeatSunday.setCompoundDrawables(null, null, img, null) // Show checkmark for Sunday
        }
    }

    // Save the selected repeat days and return them to the previous activity
    private fun saveClicked() {
        val resultIntent = Intent() // Create an intent to return the result
        resultIntent.putExtra(REPEAT_KEY, repeatDates) // Add the repeatDates string to the result
        setResult(RESULT_OK, resultIntent) // Set the result as OK
        finish() // Close the activity
    }

    // Cancel the selection and close the activity without saving
    private fun cancelClicked() {
        finish() // Close the activity without saving
    }

    // Convert dp (density-independent pixels) to px (pixels)
    private fun dpToPx(dp: Float): Int {
        val density = resources.displayMetrics.density // Get the screen density
        return (dp * density).toInt() // Convert dp to px
    }
}


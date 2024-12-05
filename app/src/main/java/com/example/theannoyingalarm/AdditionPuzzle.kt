package com.example.theannoyingalarm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class AdditionPuzzle : AppCompatActivity() {
    private var correctAnswer: Int = 0   // To store the correct answer of the math puzzle
    private var isSnooze = false          // Flag to indicate if the alarm is in snooze mode
    private var isComplete = false        // Flag to indicate if the puzzle is completed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addition_puzzle) // Set the layout for this activity

        isSnooze = intent.getBooleanExtra(IS_SNOOZE_KEY, false) // Retrieve the snooze flag from the intent

        // Get references to UI elements
        val tvAlarmStatus: TextView = findViewById(R.id.tvAlarmStatus) // TextView showing alarm status
        val tvPuzzle: TextView = findViewById(R.id.tvPuzzle)           // TextView displaying the puzzle question
        val etAnswer: EditText = findViewById(R.id.etAnswer)          // EditText for user to input answer
        val btnSubmit: Button = findViewById(R.id.btnSubmit)           // Button to submit the answer
        val tvFeedback: TextView = findViewById(R.id.tvFeedback)       // TextView for feedback messages

        // Generate two random numbers for the addition puzzle
        val number1 = Random.nextInt(1, 500) // Random number between 1 and 500
        val number2 = Random.nextInt(1, 500) // Another random number between 1 and 500
        correctAnswer = number1 + number2    // Calculate the correct answer

        // Set the puzzle question in the TextView
        tvPuzzle.text = "What is $number1 + $number2?"

        // Handle the logic for the submit button click
        btnSubmit.setOnClickListener {
            val userAnswer = etAnswer.text.toString().toIntOrNull() // Get and parse the user's input

            // Check if the user entered a valid number
            if (userAnswer == null) {
                tvFeedback.text = "❌ Please enter a valid number!" // Show error if input is invalid
            } else if (userAnswer == correctAnswer) { // Check if the answer is correct
                tvFeedback.text = "🎉 Correct! The alarm is now turned off." // Display success message
                tvAlarmStatus.text = "✅ Alarm turned off!" // Update alarm status text
                tvAlarmStatus.setTextColor(getColor(R.color.teal_700)) // Change text color to green
                btnSubmit.isEnabled = false // Disable the submit button after correct answer
                // Use a coroutine to add a delay before finishing the activity
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000) // Wait for 2 seconds
                    puzzleComplete() // Mark the puzzle as complete
                }
            } else { // If the answer is incorrect
                tvFeedback.text = "❌ Wrong answer. Try again!" // Display incorrect answer feedback
            }
        }
    }

    // Function to be called when the puzzle is completed
    private fun puzzleComplete() {
        isComplete = true // Mark the puzzle as complete
        val resultIntent = Intent().apply {
            putExtra(IS_SNOOZE_KEY, isSnooze) // Pass back the snooze flag
            putExtra(IS_PUZZLE_COMPLETE_KEY, isComplete) // Indicate that the puzzle is complete
        }

        setResult(RESULT_OK, resultIntent) // Set the result of the activity
        finish() // Finish the activity
    }
}

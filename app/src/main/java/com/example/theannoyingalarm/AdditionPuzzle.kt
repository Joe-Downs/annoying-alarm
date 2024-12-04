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
    private var correctAnswer: Int = 0
    private var isSnooze = false
    private var isComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addition_puzzle)

        isSnooze = intent.getBooleanExtra(IS_SNOOZE_KEY, false)

        // Get UI elements
        val tvAlarmStatus: TextView = findViewById(R.id.tvAlarmStatus)
        val tvPuzzle: TextView = findViewById(R.id.tvPuzzle)
        val etAnswer: EditText = findViewById(R.id.etAnswer)
        val btnSubmit: Button = findViewById(R.id.btnSubmit)
        val tvFeedback: TextView = findViewById(R.id.tvFeedback)

        // Generate the math puzzle
        val number1 = Random.nextInt(1, 500)
        val number2 = Random.nextInt(1, 500)
        correctAnswer = number1 + number2

        // Set the puzzle question
        tvPuzzle.text = "What is $number1 + $number2?"

        // Submit button logic
        btnSubmit.setOnClickListener {
            val userAnswer = etAnswer.text.toString().toIntOrNull()

            if (userAnswer == null) {
                tvFeedback.text = "❌ Please enter a valid number!"
            } else if (userAnswer == correctAnswer) {
                tvFeedback.text = "🎉 Correct! The alarm is now turned off."
                tvAlarmStatus.text = "✅ Alarm turned off!"
                tvAlarmStatus.setTextColor(getColor(R.color.teal_700))
                btnSubmit.isEnabled = false // Disable button
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    puzzleComplete()
                }
            } else {
                tvFeedback.text = "❌ Wrong answer. Try again!"
            }
        }
    }

    private fun puzzleComplete() {
        isComplete = true
        val resultIntent = Intent().apply {
            putExtra(IS_SNOOZE_KEY, isSnooze)
            putExtra(IS_PUZZLE_COMPLETE_KEY, isComplete)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
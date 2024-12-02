package com.example.theannoyingalarm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TestPuzzle : AppCompatActivity() {
    private lateinit var button: Button
    private var isSnooze = false
    private var isComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_puzzle)

        isSnooze = intent.getBooleanExtra(IS_SNOOZE_KEY, false)
        button = findViewById(R.id.stop_button)
        button.setOnClickListener {
            complete()
        }
    }

    private fun complete() {
        isComplete = true
        val resultIntent = Intent().apply {
            putExtra(IS_SNOOZE_KEY, isSnooze)
            putExtra(IS_PUZZLE_COMPLETE_KEY, isComplete)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
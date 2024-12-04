package com.example.theannoyingalarm

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.compose.setContent
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.theannoyingalarm.ui.theme.TheAnnoyingAlarmTheme

class RGBGuruPuzzle : AppCompatActivity() {
    private var isSnooze = false
    private var isComplete = false

    lateinit var targetSquare: ImageView
    var targetColor : Int = 0
    lateinit var userSquare: ImageView
    lateinit var redSlider: SeekBar
    lateinit var greenSlider: SeekBar
    lateinit var blueSlider: SeekBar

    private fun getRandomColor() : Int {
        val red = (0..255).random()
        val green = (0..255).random()
        val blue = (0..255).random()

        return Color.argb(255, red, green, blue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.rgb_guru)

        isSnooze = intent.getBooleanExtra(IS_SNOOZE_KEY, false)

        // Find the components
        targetSquare = findViewById(R.id.rgbGuruTargetSquare)
        userSquare = findViewById(R.id.rgbGuruUserSquare)
        redSlider = findViewById(R.id.rgbGuruRedSlider)
        greenSlider = findViewById(R.id.rgbGuruGreenSlider)
        blueSlider = findViewById(R.id.rgbGuruBlueSlider)

        var red = redSlider.progress
        var green = greenSlider.progress
        var blue = blueSlider.progress

        var listener = GenericSeekBarChangeListener(this)
        this.redSlider.setOnSeekBarChangeListener(listener)
        this.greenSlider.setOnSeekBarChangeListener(listener)
        this.blueSlider.setOnSeekBarChangeListener(listener)

        this.targetColor = getRandomColor()
        targetSquare.setColorFilter(targetColor)
    }

    fun puzzleComplete() {
        isComplete = true
        val resultIntent = Intent().apply {
            putExtra(IS_SNOOZE_KEY, isSnooze)
            putExtra(IS_PUZZLE_COMPLETE_KEY, isComplete)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import com.example.theannoyingalarm.ui.theme.TheAnnoyingAlarmTheme

class GenericSeekBarChangeListener(puzzle : RGBGuruPuzzle) : OnSeekBarChangeListener
{
    val puzzle : RGBGuruPuzzle = puzzle
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            val red = this.puzzle.redSlider.progress
            val green = this.puzzle.greenSlider.progress
            val blue = this.puzzle.blueSlider.progress

            val color = Color.argb(255, red, green, blue)
            Log.d("COLOR", color.toString())
            val target = this.puzzle.targetColor
            // This tolerance can be adjusted to make the game harder / easier
            val tolerance = 50
            if (Color.red(color) <= Color.red(target) + tolerance && Color.red(color) >= Color.red(target) - tolerance) {
                if (Color.green(color) <= Color.green(target) + tolerance && Color.green(color) >= Color.green(target) - tolerance) {
                    if (Color.blue(color) <= Color.blue(target) + tolerance && Color.blue(color) >= Color.blue(target) - tolerance) {
                        Log.d("COLOR", "YOU WIN")
                        this.puzzle.puzzleComplete()
                    }
                }

            }
            this.puzzle.userSquare.setColorFilter(color)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // Optional: Handle start of touch if needed
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // Optional: Handle end of touch if needed
    }
}

class RGBGuruPuzzle : ComponentActivity() {
    private var isSnooze = false
    private var isComplete = false

    private lateinit var targetSquare: ImageView
    public var targetColor : Int = 0
    public lateinit var userSquare: ImageView
    public lateinit var redSlider: SeekBar
    public lateinit var greenSlider: SeekBar
    public lateinit var blueSlider: SeekBar

    fun getRandomColor() : Int {
        val red = (0..255).random()
        val green = (0..255).random()
        val blue = (0..255).random()

        return Color.argb(255, red, green, blue)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(R.layout.rgb_guru)


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

    public fun puzzleComplete() {
        isComplete = true
        val resultIntent = Intent().apply {
            putExtra(IS_SNOOZE_KEY, isSnooze)
            putExtra(IS_PUZZLE_COMPLETE_KEY, isComplete)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
package com.example.theannoyingalarm

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import kotlinx.coroutines.delay
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            puzzle.puzzleComplete()
                        }
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
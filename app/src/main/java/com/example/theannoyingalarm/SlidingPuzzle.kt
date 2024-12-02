package com.example.theannoyingalarm

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.theannoyingalarm.ui.theme.TheAnnoyingAlarmTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SlidingPuzzle : ComponentActivity() {
    private var isSnooze = false
    private var isComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSnooze = intent.getBooleanExtra(IS_SNOOZE_KEY, false)
        setContent {
            TheAnnoyingAlarmTheme  {
                SlidingPuzzleGame()
            }
        }


    }

    @Composable
    fun SlidingPuzzleGame() {
        val context = LocalContext.current as? Activity
        val puzzleState = remember { mutableStateOf(generateSolvablePuzzle()) }
        var isSolved by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .border(5.dp, Color.Black)
                    .padding(5.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                ) {
                    items(puzzleState.value) { tile ->
                        Tile(
                            number = tile,
                            onClick = {
                                if (!isSolved) {
                                    handleTileClick(tile, puzzleState)
                                    isSolved = isPuzzleSolved(puzzleState.value)
                                    if (isSolved) {
                                        coroutineScope.launch {
                                            delay(2000)
                                            context?.finish() // Use LocalContext to call finish
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            if (isSolved) {
                Text(
                    text = "Congratulations! You solved the puzzle!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Composable
    fun Tile(number: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
        if (number == 0) {
            Box(modifier = modifier
                .fillMaxSize()
                .aspectRatio(1f)
            )
        } else {
            Card(
                modifier = modifier
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .clickable { onClick() },
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 56.sp,
                    )
                }
            }
        }
    }

    private fun handleTileClick(tile: Int, puzzleState: MutableState<List<Int>>) {
        val grid = puzzleState.value.toMutableList()
        val emptyIndex = grid.indexOf(0)
        val tileIndex = grid.indexOf(tile)

        if (isAdjacent(tileIndex, emptyIndex)) {
            grid[emptyIndex] = tile
            grid[tileIndex] = 0
            puzzleState.value = grid
        }
    }

    private fun isAdjacent(index1: Int, index2: Int): Boolean {
        val row1 = index1 / 3
        val col1 = index1 % 3
        val row2 = index2 / 3
        val col2 = index2 % 3
        return (row1 == row2 && Math.abs(col1 - col2) == 1) || (col1 == col2 && Math.abs(row1 - row2) == 1)
    }

    private fun generateSolvablePuzzle(): List<Int> {
        val solved = (1..8).toMutableList().apply { add(0) }
        do {
            solved.shuffle()
        } while (!isSolvable(solved) || isPuzzleSolved(solved))
        return solved
    }

    private fun isSolvable(puzzle: List<Int>): Boolean {
        val inversions = countInversions(puzzle)
        return inversions % 2 == 0
    }

    private fun countInversions(puzzle: List<Int>): Int {
        var inversions = 0
        val puzzleWithoutZero = puzzle.filter { it != 0 } // ignore the blank space (0)
        for (i in puzzleWithoutZero.indices) {
            for (j in i + 1 until puzzleWithoutZero.size) {
                if (puzzleWithoutZero[i] > puzzleWithoutZero[j]) {
                    inversions++
                }
            }
        }
        return inversions
    }

    private fun isPuzzleSolved(puzzle: List<Int>): Boolean {
        return puzzle == (1..8).toList() + 0
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




package com.example.theannoyingalarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.theannoyingalarm.ui.theme.TheAnnoyingAlarmTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private lateinit var alarmRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.main_activity)
        alarmRecyclerView = findViewById(R.id.alarmsRecyclerView)

        val spanCount = spanCount(150)
        alarmRecyclerView.layoutManager = GridLayoutManager(this, spanCount)


        val alarmList = listOf(
            Alarm("Alarm 1", 3, 10, isAm = true),
            Alarm("Alarm 2", 4, 15, isAm = false)
        )

        val adapter = AlarmsAdapter(alarmList)
        alarmRecyclerView.adapter = adapter
    }

    private fun spanCount(itemWidth: Int): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / itemWidth).toInt()
    }
}
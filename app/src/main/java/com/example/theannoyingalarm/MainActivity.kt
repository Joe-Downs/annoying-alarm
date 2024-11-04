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
        setContentView(R.layout.main_activity)
        alarmRecyclerView = findViewById(R.id.alarmsRecyclerView)

        val spanCount = spanCount(150)
        alarmRecyclerView.layoutManager = GridLayoutManager(this, spanCount)


        val alarmList = listOf(
            Alarm("Alarm 1", 3, 10),
            Alarm("Alarm 2", 4, 15)
        )

        val adapter = AlarmsAdapter(alarmList)
        alarmRecyclerView.adapter = adapter
//        enableEdgeToEdge()
//        setContent {
//            TheAnnoyingAlarmTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
    }

//    private fun setAlarm() {
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.HOUR_OF_DAY, alarmPicker.hour)
//        calendar.set(Calendar.MINUTE, alarmPicker.minute)
//        calendar.set(Calendar.SECOND, 0)
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
//    }

    private fun spanCount(itemWidth: Int): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / itemWidth).toInt()
    }
}



//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//@Composable
//fun alarm() {
//
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    TheAnnoyingAlarmTheme {
//        Greeting("Android")
//    }
//}
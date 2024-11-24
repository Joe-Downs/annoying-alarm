package com.example.theannoyingalarm

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var alarmViewModel: AlarmViewModel

    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var addAlarmButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.main_activity)

        requestBatteryOptimizationExclusion()

        // Initialize all views
        alarmRecyclerView = findViewById(R.id.alarmsRecyclerView)
        addAlarmButton = findViewById(R.id.addAlarmButton)

        // Calculate how many alarms can appear in one column and set the layout
        val spanCount = spanCount(150)
        alarmRecyclerView.layoutManager = GridLayoutManager(this, spanCount)

        // Set up recycler view adapter
        val adapter = AlarmsAdapter(
            this,
            emptyList(),
            onItemClick = { alarm -> editAlarmShow(alarm) },
            onDeleteClick = { alarm -> deleteAlarm(alarm) })
        alarmRecyclerView.adapter = adapter

        // Initialize View Model using the Factory
        val factory = AlarmFactory(application)
        alarmViewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

        // Observe Live Data
        alarmViewModel.alarms.observe(this) { alarms ->
            adapter.setData(alarms)
        }

        // Set up add alarm button
        addAlarmButton.setOnClickListener {
            val addAlarm = Alarm(name = "Alarm", hour =  6, min =  0, isAm =  false)
            val intent = Intent(this, AlarmEdit::class.java). apply {
                putExtra(ALARM_KEY, addAlarm)
                putExtra(ADD_ALARM_KEY, true)
            }

            activityResultLauncher.launch(intent)
        }

        // Initialize the ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val isAdd = result.data?.getBooleanExtra(ADD_ALARM_KEY, false) ?: false
                val resultAlarm = (result.data?.getSerializableExtra(ALARM_KEY) as? Alarm)
                if (resultAlarm != null) {
                    if (isAdd) {
                        alarmViewModel.addAlarm(resultAlarm)
                    } else {
                        alarmViewModel.updateAlarm(resultAlarm)
                        resultAlarm.cancelAlarm(this)
                        if (resultAlarm.isActive) {
                            resultAlarm.setAlarm(this)
                        }
                    }
                }
            }
        }

        // Add default item if first launch
        checkAndAddDefaultItem()

        // Ask permission to use notification alarm
        notifyPermission()
    }

    // Handle Permission Request Result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                return
            } else {
                // Permission denied
                Log.d("Notification Permission", "Permission were denied to allow notification resources")
            }
        }
    }

    private fun spanCount(itemWidth: Int): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / itemWidth).toInt()
    }

    private fun editAlarmShow(alarm: Alarm) {
        val intent = Intent(this, AlarmEdit::class.java).apply {
            putExtra(ALARM_KEY, alarm)
//            putExtra(POSITION_KEY, position)
            putExtra(ADD_ALARM_KEY, false)
        }

        activityResultLauncher.launch(intent)
    }

    private fun deleteAlarm(alarm: Alarm) {
        alarm.cancelAlarm(this)
        alarmViewModel.deleteAlarm(alarm)
    }

    // Check if the app is first launch
    private fun checkAndAddDefaultItem() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // If it is the first time launching the app, add a default data
            alarmViewModel.addAlarm(Alarm(name = "Alarm", hour = 6, min = 0, isAm = true))

            // Update so that the first launch was handle
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        }
    }

    private fun notifyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            // If the permission is not granted, request it.
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, proceed with other initializations or permissions
                return
            }
        } else {
            // For versions lower than Android 13, proceed without checking POST_NOTIFICATIONS permission
            return
        }
    }

    private fun requestBatteryOptimizationExclusion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${packageName}")
            }
            startActivity(intent)
        }
    }
}
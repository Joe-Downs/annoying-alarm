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
    // Declare variables for activity result launcher, view model, and UI components
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var addAlarmButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        // Set the content view for the activity
        setContentView(R.layout.main_activity)

        // Request exclusion from battery optimizations
        requestBatteryOptimizationExclusion()

        // Initialize all views using findViewById
        alarmRecyclerView = findViewById(R.id.alarmsRecyclerView)
        addAlarmButton = findViewById(R.id.addAlarmButton)

        // Calculate how many alarms can appear in one column based on screen width
        val spanCount = spanCount(150)
        // Set the layout manager of the RecyclerView to a grid layout
        alarmRecyclerView.layoutManager = GridLayoutManager(this, spanCount)

        // Set up the RecyclerView adapter with onClick and onDelete callbacks
        val adapter = AlarmsAdapter(
            this,
            emptyList(),
            onItemClick = { alarm -> editAlarmShow(alarm) }, // Edit alarm when clicked
            onDeleteClick = { alarm -> deleteAlarm(alarm) } // Delete alarm when delete icon clicked
        )
        alarmRecyclerView.adapter = adapter

        // Initialize the ViewModel using a factory to provide the application context
        val factory = AlarmFactory(application)
        alarmViewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

        // Observe the LiveData for changes in alarms and update the RecyclerView adapter
        alarmViewModel.alarms.observe(this) { alarms ->
            adapter.setData(alarms)
        }

        // Set up the "add alarm" button to open the alarm editor when clicked
        addAlarmButton.setOnClickListener {
            // Create a default alarm object
            val addAlarm = Alarm(name = "Alarm", hour = 6, min = 0, isAm = false)
            // Create an intent to open the AlarmEdit activity
            val intent = Intent(this, AlarmEdit::class.java).apply {
                putExtra(ALARM_KEY, addAlarm)
                putExtra(ADD_ALARM_KEY, true)
            }

            // Launch the activity for result to handle adding a new alarm
            activityResultLauncher.launch(intent)
        }

        // Initialize the ActivityResultLauncher to handle result after activity finishes
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val isAdd = result.data?.getBooleanExtra(ADD_ALARM_KEY, false) ?: false
                val resultAlarm = (result.data?.getSerializableExtra(ALARM_KEY) as? Alarm)
                if (resultAlarm != null) {
                    if (isAdd) {
                        // Add new alarm to the database
                        alarmViewModel.addAlarm(resultAlarm)
                    } else {
                        // Update existing alarm
                        alarmViewModel.updateAlarm(resultAlarm)
                        resultAlarm.cancelAlarm(this)
                        // Re-set the alarm if it's active
                        if (resultAlarm.isActive) {
                            resultAlarm.setAlarm(this)
                        }
                    }
                }
            }
        }

        // Check if it's the first launch and add a default alarm if so
        checkAndAddDefaultItem()

        // Request permission to post notifications if required
        notifyPermission()
    }

    // Handle the result of permission requests (e.g., notification permission)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed
                return
            } else {
                // Permission denied, log the error
                Log.d("Notification Permission", "Permission was denied to allow notification resources")
            }
        }
    }

    // Calculate how many items can fit in one column based on item width
    private fun spanCount(itemWidth: Int): Int {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / itemWidth).toInt()
    }

    // Launch the alarm editor to edit an existing alarm
    private fun editAlarmShow(alarm: Alarm) {
        val intent = Intent(this, AlarmEdit::class.java).apply {
            putExtra(ALARM_KEY, alarm)
            putExtra(ADD_ALARM_KEY, false)
        }
        activityResultLauncher.launch(intent)
    }

    // Delete an alarm and remove it from the database
    private fun deleteAlarm(alarm: Alarm) {
        alarm.cancelAlarm(this)
        alarmViewModel.deleteAlarm(alarm)
    }

    // Check if it's the first time launching the app and add a default alarm
    private fun checkAndAddDefaultItem() {
        val sharedPreferences = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            // If it's the first launch, add a default alarm
            alarmViewModel.addAlarm(Alarm(name = "Alarm", hour = 6, min = 0, isAm = true))

            // Update shared preferences to mark that the first launch has been handled
            sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
        }
    }

    // Request permission to post notifications for Android 13 and above
    private fun notifyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            // If permission is not granted, request it
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, no further action needed
                return
            }
        } else {
            // For versions lower than Android 13, no need to check POST_NOTIFICATIONS permission
            return
        }
    }

    // Request exclusion from battery optimizations to ensure alarms are not delayed
    private fun requestBatteryOptimizationExclusion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${packageName}")
            }
            startActivity(intent)
        }
    }
}

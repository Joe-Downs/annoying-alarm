package com.example.theannoyingalarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {
    // Initialize the AlarmDao, which provides access to the database
    private val alarmDao = AlarmDatabase.getDatabase(application).alarmDao()

    // LiveData to observe a list of alarms from the database
    val alarms: LiveData<List<Alarm>> = alarmDao.getAllAlarms()

    // Function to add a new alarm to the database
    fun addAlarm(alarm: Alarm) {
        // Perform database operations on a background thread using IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.insert(alarm) // Insert the alarm into the database
        }
    }

    // Function to update an existing alarm in the database
    fun updateAlarm(alarm: Alarm) {
        // Perform database operations on a background thread using IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.update(alarm) // Update the alarm in the database
        }
    }

    // Function to delete an alarm from the database
    fun deleteAlarm(alarm: Alarm) {
        // Perform database operations on a background thread using IO dispatcher
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.delete(alarm) // Delete the alarm from the database
        }
    }
}

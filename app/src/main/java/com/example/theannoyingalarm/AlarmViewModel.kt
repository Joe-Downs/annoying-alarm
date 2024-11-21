package com.example.theannoyingalarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application): AndroidViewModel(application) {
    private val alarmDao = AlarmDatabase.getDatabase(application).alarmDao()
    val alarms: LiveData<List<Alarm>> = alarmDao.getAllAlarms()

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.insert(alarm)
        }
    }

    fun updateAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.update(alarm)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.delete(alarm)
        }
    }
}
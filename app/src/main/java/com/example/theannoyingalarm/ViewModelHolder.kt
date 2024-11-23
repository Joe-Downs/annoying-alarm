package com.example.theannoyingalarm

import android.app.Application
import androidx.lifecycle.ViewModelProvider

object ViewModelHolder {
    lateinit var alarmViewModel: AlarmViewModel

    fun initialize(application: Application) {
        val factory = AlarmFactory(application)
        alarmViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(AlarmViewModel::class.java)
    }
}
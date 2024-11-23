package com.example.theannoyingalarm

import android.app.Application

class TheAnnoyingAlarm : Application() {
    override fun onCreate() {
        super.onCreate()
        ViewModelHolder.initialize(this)
    }
}
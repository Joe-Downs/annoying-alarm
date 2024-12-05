package com.example.theannoyingalarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory class for creating an instance of AlarmViewModel
class AlarmFactory(private val application: Application) : ViewModelProvider.Factory {

    // Override the create() method to instantiate the ViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the ViewModel class is of type AlarmViewModel
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            // Suppress the unchecked cast warning since we are casting to the correct type
            @Suppress("UNCHECKED_CAST")
            // Return a new instance of AlarmViewModel, passing the application context
            return AlarmViewModel(application) as T
        }

        // Throw an exception if the ViewModel class is not recognized
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

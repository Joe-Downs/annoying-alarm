package com.example.theannoyingalarm

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Annotates the class as a Room Database and defines the entities (Alarm) and version
@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {

    // Abstract method to get the DAO (Data Access Object) for Alarm
    abstract fun alarmDao(): AlarmDAO

    // Companion object to implement the Singleton pattern for the database instance
    companion object {
        // Volatile ensures visibility of the instance across threads
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        // Function to get the instance of the AlarmDatabase
        fun getDatabase(context: Context): AlarmDatabase {
            // If INSTANCE is not null, return it (database already created)
            return INSTANCE ?: synchronized(this) {
                // If INSTANCE is null, create a new database instance
                val instance = Room.databaseBuilder(
                    context.applicationContext,  // Use application context to prevent memory leaks
                    AlarmDatabase::class.java,   // Database class
                    "alarm_database"             // Database name
                ).build()  // Build the database instance
                // Assign the instance to the singleton and return it
                INSTANCE = instance
                instance
            }
        }
    }
}

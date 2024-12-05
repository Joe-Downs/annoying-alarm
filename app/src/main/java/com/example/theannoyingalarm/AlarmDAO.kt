package com.example.theannoyingalarm

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// Marking this interface as a DAO (Data Access Object) for Room database operations
@Dao
public interface AlarmDAO {

    // Insert a single Alarm object into the database.
    // This method runs on a background thread due to 'suspend' modifier.
    @Insert
    suspend fun insert(alarm: Alarm)

    // Insert a list of Alarm objects into the database.
    // If any Alarm already exists, it will be replaced based on the conflict strategy.
    // The 'REPLACE' strategy will replace the existing entry with the same primary key.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alarms: List<Alarm>)

    // Update an existing Alarm object in the database.
    // The method will update the record with the same primary key (ID) of the Alarm.
    // The 'suspend' modifier ensures it runs on a background thread.
    @Update
    suspend fun update(alarm: Alarm)

    // Delete an existing Alarm object from the database.
    // The 'suspend' modifier ensures it runs on a background thread.
    @Delete
    suspend fun delete(alarm: Alarm)

    // Query to fetch all Alarm records from the database.
    // It returns LiveData, which is lifecycle-aware, allowing automatic UI updates when data changes.
    @Query("SELECT * FROM Alarm")
    fun getAllAlarms(): LiveData<List<Alarm>>
}

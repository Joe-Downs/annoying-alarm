package com.example.theannoyingalarm

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
public interface AlarmDAO {
    @Insert
    suspend fun insert(alarm: Alarm)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alarms: List<Alarm>)

    @Update
    suspend fun update(alarm: Alarm)

    @Delete
    suspend fun delete(alarm: Alarm)

    @Query("SELECT * FROM Alarm")
    fun getAllAlarms(): LiveData<List<Alarm>>
}
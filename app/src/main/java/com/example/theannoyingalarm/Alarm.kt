package com.example.theannoyingalarm

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Alarm")
data class Alarm(
    @PrimaryKey(autoGenerate = true) var alarmID: Int = 0,
    @ColumnInfo(name = "Name") var name: String = "",
    @ColumnInfo(name = "Hour") var hour: Int = 0,
    @ColumnInfo(name = "Minute")  var min: Int = 0,
    @ColumnInfo(name = "Is_AM")  var isAm: Boolean = true,
    @ColumnInfo(name = "Is_Active")  var isActive: Boolean = false,
    @ColumnInfo(name = "Repeat")  var repeat: String = ""
) : Serializable
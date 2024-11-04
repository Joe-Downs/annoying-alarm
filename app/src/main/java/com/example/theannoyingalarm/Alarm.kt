package com.example.theannoyingalarm

data class Alarm(
    var name: String = "Alarm",
    var hour: Int = 0,
    var min: Int = 0,
    var state: String = "AM",
    var isActive: Boolean = false
)
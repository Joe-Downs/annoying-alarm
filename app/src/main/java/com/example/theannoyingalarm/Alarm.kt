package com.example.theannoyingalarm

import java.io.Serializable

data class Alarm(
    var name: String = "",
    var hour: Int = 0,
    var min: Int = 0,
    var isAm: Boolean = true,
    var isActive: Boolean = false,
    var repeat: String = ""
) : Serializable
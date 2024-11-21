package com.example.theannoyingalarm

enum class WeekDay(val short: Char, val long: String, val int: Int) {
    SUNDAY('N', "Sunday", 1),
    MONDAY('M', "Monday", 2),
    TUESDAY('T', "Tuesday", 3),
    WEDNESDAY('W', "Wednesday", 4),
    THURSDAY('U', "Thursday", 5),
    FRIDAY('F', "Friday", 6),
    SATURDAY('S', "Saturday", 7),
}
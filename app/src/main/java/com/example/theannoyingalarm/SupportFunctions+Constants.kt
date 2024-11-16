package com.example.theannoyingalarm

import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Color

public const val REPEAT_KEY = "Repeats"
public const val ALARM_KEY = "Alarm"
public const val POSITION_KEY = "alarm_position"

public enum class WeekDay(val short: Char, val long: String) {
    MONDAY('M', "Monday"),
    TUESDAY('T', "Tuesday"),
    WEDNESDAY('W', "Wednesday"),
    THURSDAY('U', "Thursday"),
    FRIDAY('F', "Friday"),
    SATURDAY('S', "Saturday"),
    SUNDAY('N', "Sunday")
}

public fun getAttributedRepeatText(repeat: String): SpannableString {
    if (repeat.isEmpty()) {
        return SpannableString("Never")
    }

    if (repeat.length == 7) {
        return SpannableString("Always")
    }

    val result = SpannableString("S M T W T F S ")

    // Check if repeat on Sunday
    if (repeat.contains('N', true)) {
//        result.removeSpan(result.getSpans(0, 1, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Check if repeat on Monday
    if (repeat.contains('M', true)) {
//        result.removeSpan(result.getSpans(2, 3, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 2, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 2, 3, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Check if repeat on Tuesday
    if (repeat.contains('T', true)) {
//        result.removeSpan(result.getSpans(4, 5, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 4, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 4, 5, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Check if repeat on Wednesday
    if (repeat.contains('W', true)) {
//        result.removeSpan(result.getSpans(6, 7, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 6, 7, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 6, 7, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Check if repeat on Thursday
    if (repeat.contains('U', true)) {
//        result.removeSpan(result.getSpans(8, 9, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 8, 9, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 8, 9, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Check if repeat on Friday
    if (repeat.contains('F', true)) {
//        result.removeSpan(result.getSpans(10, 11, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 10, 11, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 10, 11, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    // Check if repeat on Saturday
    if (repeat.contains('S', true)) {
//        result.removeSpan(result.getSpans(12, 13, Any::class.java).first())
        result.setSpan(StyleSpan(Typeface.BOLD), 12, 13, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        result.setSpan(ForegroundColorSpan(Color.BLACK), 12, 13, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return result
}
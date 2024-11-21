package com.example.theannoyingalarm

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

class AlarmsAdapter(
    private val context: Context,
    private var alarmsList: List<Alarm>,
    private val onItemClick: (Alarm) -> Unit,
    private val onDeleteClick: (Alarm) -> Unit) :
    RecyclerView.Adapter<AlarmsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val selectedTime: TextView = view.findViewById(R.id.selectedTime)
        val repeat: TextView = view.findViewById(R.id.repeatDate)
        val alarmName: TextView = view.findViewById(R.id.alarmName)
        val activeSwitch: Switch = view.findViewById(R.id.alarmSwitch)
        val removeButton: ImageButton = view.findViewById(R.id.removeAlarmButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alarmsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarmsList[position]
        holder.selectedTime.text = formatTimeString(alarm.hour, alarm.min, alarm.isAm)
        holder.alarmName.text = alarm.name

        // Set repeat indicator
        val repeatP1 = SpannableString("Repeat: ")
        val repeatText = SpannableString(TextUtils.concat(repeatP1, getAttributedRepeatText(alarm.repeat)))
        holder.repeat.text = repeatText

        if (alarm.isActive) {
            holder.activeSwitch.isChecked = true

            // TODO: Show remain time
        } else {
            holder.activeSwitch.isChecked = false
        }

        // handle edit alarm click
        holder.itemView.setOnClickListener {
            onItemClick(alarm)
        }

        // handle remove alarm click
        holder.removeButton.setOnClickListener {
            onDeleteClick(alarm)
        }

        holder.activeSwitch.setOnCheckedChangeListener { _, isChecked ->
            alarm.isActive = isChecked

            if (isChecked) {
                setAlarm(alarm)
            } else {
                cancelAlarm(alarm)
            }
        }


    }

    // Private Functions
    private fun formatTimeString(hour: Int, minute: Int, amPm: Boolean): String {
        return String.format("%02d:%02d %s", hour, minute, (if (amPm) "AM" else "PM"))
    }

    private fun setAlarm(alarm: Alarm) {
        alarm.setAlarm(context)
    }

    private fun cancelAlarm(alarm: Alarm) {
        alarm.cancelAlarm(context)
    }

    // Public functions
    fun setData(alarms: List<Alarm>) {
        alarmsList = alarms
        notifyDataSetChanged()
    }
}
package com.example.theannoyingalarm

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

class AlarmsAdapter(private val alarmsList: List<Alarm>) :
    RecyclerView.Adapter<AlarmsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val selectedTime: TextView = view.findViewById(R.id.selectedTime)
        val repeat: TextView = view.findViewById(R.id.repeatDate)
        val alarmName: TextView = view.findViewById(R.id.alarmName)
        val activeSwitch: Switch = view.findViewById(R.id.alarmSwitch)
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

        // TODO: implement repeat indicator
        holder.repeat.text = "Repeat: Never"
        if (alarm.isActive) {
            holder.activeSwitch.isChecked = true

            // TODO: Show remain time
        } else {
            holder.activeSwitch.isChecked = false
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AlarmEdit::class.java). apply {
                putExtra("Alarm", alarm)
            }

            (context as MainActivity).startActivity(intent)
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

    private fun formatTimeString(hour: Int, minute: Int, amPm: Boolean): String {
        return String.format("%02d:%02d %s", hour, minute, (if (amPm) "AM" else "PM"))
    }

    private fun setAlarm(alarm: Alarm) {

    }

    private fun cancelAlarm(alarm: Alarm) {

    }
}
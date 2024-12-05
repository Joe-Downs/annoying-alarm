package com.example.theannoyingalarm

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmsAdapter(
    private val context: Context,  // Context for accessing resources and launching intents
    private var alarmsList: List<Alarm>,  // List of alarms to be displayed in the RecyclerView
    private val onItemClick: (Alarm) -> Unit,  // Callback for handling item click (edit alarm)
    private val onDeleteClick: (Alarm) -> Unit  // Callback for handling delete button click
) : RecyclerView.Adapter<AlarmsAdapter.ViewHolder>() {

    // ViewHolder class holds references to the views for each item in the RecyclerView
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val selectedTime: TextView = view.findViewById(R.id.selectedTime)  // TextView for displaying time
        val repeat: TextView = view.findViewById(R.id.repeatDate)  // TextView for displaying repeat info
        val alarmName: TextView = view.findViewById(R.id.alarmName)  // TextView for displaying alarm name
        val activeSwitch: Switch = view.findViewById(R.id.alarmSwitch)  // Switch to toggle alarm active status
        val removeButton: ImageButton = view.findViewById(R.id.removeAlarmButton)  // Button to remove alarm
    }

    // Called when a new ViewHolder is created. This inflates the layout for each alarm item.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_layout, parent, false)
        return ViewHolder(view)
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int {
        return alarmsList.size
    }

    // Binds the data to the ViewHolder. This is called for each item in the RecyclerView.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarmsList[position]  // Get the alarm at the current position

        // Format and display the alarm time
        holder.selectedTime.text = formatTimeString(alarm.hour, alarm.min, alarm.isAm)
        holder.alarmName.text = alarm.name

        // Set the repeat indicator text with formatted repeat info
        val repeatP1 = SpannableString("Repeat: ")
        val repeatText = SpannableString(TextUtils.concat(repeatP1, getAttributedRepeatText(alarm.repeat)))
        holder.repeat.text = repeatText

        // Set the Switch state based on whether the alarm is active
        if (alarm.isActive) {
            holder.activeSwitch.isChecked = true
        } else {
            holder.activeSwitch.isChecked = false
        }

        // Handle the click event for editing the alarm
        holder.itemView.setOnClickListener {
            onItemClick(alarm)  // Call the callback for editing
        }

        // Handle the remove button click
        holder.removeButton.setOnClickListener {
            onDeleteClick(alarm)  // Call the callback for deleting the alarm
        }

        // Handle the state change of the Switch (toggle alarm active status)
        holder.activeSwitch.setOnCheckedChangeListener { _, isChecked ->
            alarm.isActive = isChecked  // Update the alarm's active status
            val alarmViewModel = ViewModelHolder.alarmViewModel  // Access the ViewModel
            alarmViewModel.updateAlarm(alarm)  // Update the alarm in the ViewModel (and database)

            // Set or cancel the alarm based on whether it's active or not
            if (isChecked) {
                setAlarm(alarm)  // Set the alarm
            } else {
                cancelAlarm(alarm)  // Cancel the alarm
            }
        }
    }

    // Private helper function to format the time string for the alarm
    private fun formatTimeString(hour: Int, minute: Int, amPm: Boolean): String {
        return String.format("%02d:%02d %s", hour, minute, if (amPm) "AM" else "PM")
    }

    // Private helper function to set the alarm (e.g., schedule it using the AlarmManager)
    private fun setAlarm(alarm: Alarm) {
        alarm.setAlarm(context)  // Call the Alarm object's method to set the alarm
    }

    // Private helper function to cancel the alarm
    private fun cancelAlarm(alarm: Alarm) {
        alarm.cancelAlarm(context)  // Call the Alarm object's method to cancel the alarm
    }

    // Public function to update the alarm list in the adapter and notify the RecyclerView
    fun setData(alarms: List<Alarm>) {
        alarmsList = alarms  // Update the list of alarms
        notifyDataSetChanged()  // Notify the adapter to refresh the data in the RecyclerView
    }
}

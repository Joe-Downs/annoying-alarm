package com.example.theannoyingalarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class AlarmForegroundService : Service() {

    // Declare AudioManager and AudioFocusRequest for handling audio focus
    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest

    // MediaPlayer for playing the alarm sound
    private var mediaPlayer: MediaPlayer? = null
    // Notification channel ID
    private val channelId = "alarm_channel"

    // Called when the service is started
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Retrieve the Alarm object passed with the intent
        val tempAlarm = (intent?.getSerializableExtra("Alarm_object") as? Alarm)!!

        // Create the notification channel (required for notifications)
        createNotificationChannel()

        // Create an intent to launch the AlarmActivity when the notification is tapped
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra("Alarm_object", tempAlarm)
        }

        // Create a PendingIntent for opening the AlarmActivity
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification to display while the service is running
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Active")
            .setContentText("Tap to stop the alarm.")
            .setSmallIcon(R.drawable.baseline_access_alarms_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)  // Full screen intent for alarm activity
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)  // Automatically cancel notification when tapped
            .build()

        // Start the service in the foreground with the created notification
        startForeground(1, notification)

        // Initialize the AudioManager to manage audio focus
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Add a delay of 3 seconds before playing the alarm sound to ensure proper notification display
        Handler(Looper.getMainLooper()).postDelayed({
            // Request audio focus to ensure the alarm sound can be played
            val result = requestAudioFocus()
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                startAlarm()  // Start playing the alarm sound
            }
        }, 3000) // Delay of 3 seconds

        return START_STICKY // Keep the service running
    }

    // Called when the service is destroyed, stops the alarm
    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    // onBind is not used since this is a started service, so we return null
    override fun onBind(intent: Intent?): IBinder? = null

    // Method to request audio focus for playing the alarm sound
    private fun requestAudioFocus(): Int {
        // For Android 8.0 and higher, use AudioFocusRequest
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioFocusRequestBuilder = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)  // Set listener for audio focus changes
                .build()

            audioManager.requestAudioFocus(audioFocusRequestBuilder)
        } else {
            // For older Android versions, use the older method to request audio focus
            audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    // Listener to handle changes in audio focus
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Regained audio focus, resume playing alarm sound
                if (mediaPlayer == null || !mediaPlayer!!.isPlaying) {
                    mediaPlayer?.start()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Temporarily lost audio focus (e.g., media playback started), pause alarm sound
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanently lost audio focus (e.g., another app took over), stop alarm
                stopAlarm()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Audio focus lost, but allow alarm sound to be quieter (ducking)
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.setVolume(0.1f, 0.1f)  // Reduce volume to allow ducking
                }
            }
        }
    }

    // Method to start the alarm sound
    private fun startAlarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.bright_morning_alarm).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            isLooping = true  // Loop the alarm sound until stopped
            start()
        }
    }

    // Method to stop the alarm sound
    private fun stopAlarm() {
        mediaPlayer?.stop()  // Stop the media player
        mediaPlayer?.release()  // Release resources
        mediaPlayer = null  // Nullify media player reference
        abandonAudioFocus()  // Abandon audio focus when done
    }

    // Method to abandon audio focus after the alarm is stopped
    private fun abandonAudioFocus() {
        // For Android 8.0 and higher, use AudioFocusRequest to abandon focus
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            audioManager.abandonAudioFocus(audioFocusChangeListener)  // For older versions
        }
    }

    // Method to create the notification channel for API 26+ devices
    private fun createNotificationChannel() {
        // Create the notification channel only if the API level is 26 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Alarm notifications"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC  // Make notification visible on lockscreen
                setBypassDnd(true)  // Allow notification to bypass Do Not Disturb mode
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)  // Create the channel
        }
    }
}

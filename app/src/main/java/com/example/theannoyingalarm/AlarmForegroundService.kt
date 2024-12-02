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

    private lateinit var audioManager: AudioManager
    private lateinit var audioFocusRequest: AudioFocusRequest

    private var mediaPlayer: MediaPlayer? = null
    private val channelId = "alarm_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val tempAlarm = (intent?.getSerializableExtra("Alarm_object") as? Alarm)!!

        createNotificationChannel()

        // Create an intent to open the AlarmActivity
        val fullScreenIntent = Intent(this, AlarmActivity::class.java).apply {
            putExtra("Alarm_object", tempAlarm)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Alarm Active")
            .setContentText("Tap to stop the alarm.")
            .setSmallIcon(R.drawable.baseline_access_alarms_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)

        // Initialize AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Add 3 seconds of delay to ensure the sound play after the notification
        Handler(Looper.getMainLooper()).postDelayed({
            val result = requestAudioFocus()
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                startAlarm()
            }
        }, 3000) // Delay of 3 seconds

        return START_STICKY
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun requestAudioFocus(): Int {
        // For Android 8.0 (API level 26) and higher, use AudioFocusRequest
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioFocusRequestBuilder = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()

            audioManager.requestAudioFocus(audioFocusRequestBuilder)
        } else {
            // For older Android versions, use requestAudioFocus() method
            audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_ALARM, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Regained audio focus, resume playing alarm sound
                if (mediaPlayer == null || !mediaPlayer!!.isPlaying) {
                    mediaPlayer?.start()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Temporarily lost audio focus (e.g., media playback started)
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanently lost audio focus (e.g., an app took over)
                stopAlarm()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Audio focus lost, but the alarm sound can be quieter (ducking)
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.setVolume(0.1f, 0.1f)  // Reduce volume to allow ducking
                }
            }
        }
    }
    private fun startAlarm() {
        mediaPlayer = MediaPlayer.create(this, R.raw.bright_morning_alarm).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            isLooping = true
            start()
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        abandonAudioFocus()  // Abandon audio focus when the alarm stops
    }

    private fun abandonAudioFocus() {
        // Abandon audio focus when done with alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    private fun createNotificationChannel() {
        // Create a notification channel for API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Alarm notifications"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
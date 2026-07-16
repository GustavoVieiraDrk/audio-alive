package com.audioalive.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.audioalive.R

private const val CHANNEL_ID = "DAC_KEEPALIVE_CHANNEL"

class AudioKeepAliveService : Service() {
    private var isPlaying = false
    private var audioTrack: AudioTrack? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d("DAC_KeepAlive", "TV in Standby. audio paused.")
                    stopSilentAudioStream()
                }
                Intent.ACTION_SCREEN_ON -> {
                    Log.d("DAC_KeepAlive", "TV Woke up. Waiting for the USB to recognize the DAC...")
                    Handler(Looper.getMainLooper()).postDelayed({
                        startSilentAudioStream()
                    }, 3000)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DacKeepAlive::AudioLock")
        @SuppressLint("WakelockTimeout")
        wakeLock?.acquire()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

        startSilentAudioStream()

        return START_STICKY
    }

    private fun startSilentAudioStream() {
        if (isPlaying) return
        isPlaying = true

        Thread {
            val sampleRate = 8000
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            fun createTrack(): AudioTrack {
                return AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize)
                    .build()
            }

            audioTrack = createTrack()
            audioTrack?.play()

            val silence = ShortArray(minBufferSize)

            while (isPlaying) {
                val result = audioTrack?.write(silence, 0, silence.size) ?: -1
                if (result < 0) {
                    Log.e("DAC_KeepAlive", "Error in AudioTrack (Dead Channel). Recreating...")
                    audioTrack?.release()
                    Thread.sleep(2000)

                    if (isPlaying) {
                        audioTrack = createTrack()
                        audioTrack?.play()
                    }
                }
            }

            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null

        }.start()
    }

    private fun stopSilentAudioStream() {
        isPlaying = false
    }

    override fun onDestroy() {
        stopSilentAudioStream()

        try {
            unregisterReceiver(screenStateReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
package com.audioalive.foundation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.audioalive.service.AudioKeepAliveService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON") {
            val serviceIntent = Intent(context, AudioKeepAliveService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}

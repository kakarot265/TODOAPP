package com.example.todoapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class AlertReceiver : BroadcastReceiver() {

    private final val channelId = "Todo Channel"
    override fun onReceive(context: Context, intent: Intent) {

        val notificationid = intent.getIntExtra("notifiacationId", 0)
        // Calling main Activity when Notification is Tapped
        val i = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, i, 0)

        val nm:NotificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            nm.createNotificationChannel(NotificationChannel(channelId, "default", NotificationManager.IMPORTANCE_DEFAULT ))
        }

        val SimpleNotification = NotificationCompat.Builder(context, channelId )
                .setContentIntent(pi)
                .setContentTitle("Task Time")
                .setContentText("Perform the task")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

        nm.notify(notificationid, SimpleNotification)
    }
}
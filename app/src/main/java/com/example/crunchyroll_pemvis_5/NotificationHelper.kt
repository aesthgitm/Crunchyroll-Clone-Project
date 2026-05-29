package com.example.crunchyroll_pemvis_5

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat

object NotificationHelper {

    private const val CHANNEL_ID = "crunchy_channel"
    private const val CHANNEL_NAME = "Crunchyroll Notifications"

    // Custom Toast with premium look
    fun showToast(context: Context, message: String, iconRes: Int? = null) {
        try {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.custom_toast, null)

            val text: TextView = layout.findViewById(R.id.toast_message)
            text.text = message

            val icon: ImageView = layout.findViewById(R.id.toast_icon)
            if (iconRes != null) {
                icon.setImageResource(iconRes)
            } else {
                // Default custom orange checkmark / info icon if none provided
                icon.setImageResource(android.R.drawable.ic_dialog_info)
            }

            val toast = Toast(context.applicationContext)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 150)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout
            toast.show()
        } catch (e: Exception) {
            // Fallback to standard toast on error
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Standard Push Notification
    fun showSystemNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Crunchyroll User Activity Notifications"
                enableLights(true)
                lightColor = android.graphics.Color.parseColor("#FF6400")
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}

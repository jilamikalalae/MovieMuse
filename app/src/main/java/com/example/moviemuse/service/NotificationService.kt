package com.example.moviemuse.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.moviemuse.MainActivity
import com.example.moviemuse.R
import com.example.moviemuse.model.Movie

class NotificationService(private val context: Context) {

    private val TAG = "NotificationService"
    private val channelId = "watchlist_reminders"
    private val handler = Handler(Looper.getMainLooper())

    init {
        createNotificationChannel()
        Log.d(TAG, "NotificationService initialized")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Watchlist Reminders"
            val descriptionText = "Reminders for movies in your watchlist"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $channelId")
        }
    }

    fun scheduleWatchlistReminder(movie: Movie) {
        Log.d(TAG, "Scheduling reminder for movie: ${movie.title}")
        // Schedule notification to be sent immediately for testing
        handler.postDelayed({
            showWatchlistReminderNotification(movie)
        }, 5000) // 5 seconds in milliseconds

        // Also show a test notification immediately
        showTestNotification(movie)
    }

    private fun showTestNotification(movie: Movie) {
        Log.d(TAG, "Showing immediate test notification for: ${movie.title}")
        val notificationId = 1000 + movie.id
        showNotification(
            notificationId,
            "TEST: Movie Added!",
            "You just added '${movie.title}' to your favorites"
        )
    }

    private fun showWatchlistReminderNotification(movie: Movie) {
        Log.d(TAG, "Showing watchlist reminder for: ${movie.title}")
        val notificationTitle = "🎬 Don't Forget Your Watchlist!"
        val notificationContent = "'${movie.title}' is still waiting for you. Watch it tonight!"
        showNotification(movie.id, notificationTitle, notificationContent)
    }

    private fun showNotification(notificationId: Int, title: String, content: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(notificationId, builder.build())
                Log.d(TAG, "Notification sent with ID: $notificationId")
            } else {
                Log.e(TAG, "Cannot show notification: POST_NOTIFICATIONS permission not granted")
            }
        }
    }
}

package com.github.sdpteam15.polyevents.view

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// TODO: consider instantiating Firebase database here
class PolyEventsApplication : Application() {
    companion object{
        var inTest = false
        lateinit var application :PolyEventsApplication
    }
    override fun onCreate() {
        super.onCreate()
        application = this
    }
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { LocalDatabase.getDatabase(this, applicationScope) }

    /**
     * To be able to deliver notifications on the app, we must register the app's notification
     * channel with the system by passing an instance of NotificationChannel to
     * createNotificationChannel(). Because we must create the notification channel before posting
     * any notifications on Android 8.0 and higher, we should execute this code as soon as the app
     * starts. It's safe to call this repeatedly because creating an existing notification channel performs no operation.
     * @param channelId the id of the channel
     * @param channelName the name of the channel
     */
    fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.event_notification_channel_description)

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }
}
package com.github.sdpteam15.polyevents.view.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.github.sdpteam15.polyevents.helper.*
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID

/**
 * Android apps can send or receive broadcast messages from the Android system and other Android apps.
 * We create a custom BroadcastReceiver to receive broadcasts and handle accordingly.
 * The system package manager registers the receiver when the app is installed.
 * The receiver then becomes a separate entry point into your app which means that the system can
 * start the app and deliver the broadcast if the app is not currently running. The broadcast
 * will be used to notify the user for example of the start of an event.
 * Check https://developer.android.com/guide/components/broadcasts?hl=en
 */
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager = ContextCompat.getSystemService(
            context!!,
            NotificationManager::class.java
        ) as NotificationManager

        // Check which type of notification we're receiving
        val notificationType = intent!!.getStringExtra(EXTRA_NOTIFICATION_TYPE)

        if (notificationType != null) {
            when (NotificationType.valueOf(notificationType)) {
                NotificationType.EVENT_NOTIFICATION -> {
                    val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
                    val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 1)
                    val notificationMessage = intent.getStringExtra(EXTRA_NOTIFICATION_MESSAGE)

                    // Create the content intent for the notification, which redirects to
                    // the target activity.
                    val contentIntent = Intent(context, EventActivity::class.java).apply {
                        putExtra(EXTRA_EVENT_ID, eventId)
                    }

                    if (eventId != null && notificationMessage != null) {
                        notificationManager.sendEventNotification(
                            messageBody = notificationMessage,
                            applicationContext = context,
                            notificationId = notificationId,
                            contentIntent = contentIntent
                        )
                    }
                }
                // Add here more cases as we have more types of notifications other than events' to handle differently.
                else -> Unit
            }
        }
    }

}
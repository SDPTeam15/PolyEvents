package com.github.sdpteam15.polyevents.helper

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID

// TODO: will have to increment for each event
private val NOTIFICATION_ID = 0

const val EXTRA_NOTIFICATION_ID = "com.github.sdpteam15.polyevents.helper.NOTIFICATION_ID"
const val EXTRA_NOTIFICATION_MESSAGE = "com.github.sdpteam15.polyevents.helper.NOTIFICATION_MESSAGE"
const val REQUEST_CODE =  0

/**
 * Extension function to the Notification manager, to create a custom notification
 * @param messageBody the message that will be displayed in the body of the notification
 * @param applicationContext the context of the application
 * @param eventId the eventId of the event of which the user will be notified
 * @param notificationId the id for the notification to be set
 */
fun NotificationManager.sendEventNotification(
    messageBody: String, applicationContext: Context, eventId: String, notificationId: Int
) {
    // Create the content intent for the notification, which launches
    // this activity
    val contentIntent = Intent(applicationContext, EventActivity::class.java).apply {
        putExtra(EXTRA_EVENT_ID, eventId)
    }

    /**
     * We created the intent, but the notification is displayed outside the app.
     * To make an intent work outside the app, we need to create a new PendingIntent.
     * PendingIntent grants rights to another application or the system to perform an operation
     * on behalf of the application. A PendingIntent itself is simply a reference to a token
     * maintained by the system describing the original data used to retrieve it.
     * This means that, even if its owning application's process is killed, the PendingIntent
     * itself will remain usable from other processes it has been given to.
     * In this case, the system will use the pending intent to open the app on behalf of us,
     * regardless of whether or not the timer app is running.
     * Check https://developer.android.com/training/notify-user/build-notification
     */
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.event_notification_channel_id)
    ).setSmallIcon(R.drawable.ic_event)        // TODO: replace with application icon
        .setContentTitle(applicationContext.getString(R.string.app_name))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(notificationId, builder.build())
}


/**
 * Extension function to cancel all the notifications of the notification Manager.
 * (Called for example when a user signs out)
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

/**
 * Cancel a notification with the provided notification id
 * @param notificationId the id of the notification we set
 */
fun NotificationManager.cancelNotification(notificationId: Int) {
    cancel(notificationId)
}
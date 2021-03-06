package com.github.sdpteam15.polyevents.helper

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import com.github.sdpteam15.polyevents.view.service.AlarmReceiver
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime

const val EXTRA_NOTIFICATION_TYPE = "com.github.sdpteam15.polyevents.helper.NOTIFICATION_TYPE"
const val EXTRA_NOTIFICATION_ID = "com.github.sdpteam15.polyevents.helper.NOTIFICATION_ID"
const val EXTRA_NOTIFICATION_MESSAGE = "com.github.sdpteam15.polyevents.helper.NOTIFICATION_MESSAGE"

/**
 * Enum class to identify different types of notifications to handle differently
 * @property notificationType the string value of the NotificationType
 */
enum class NotificationType(private val notificationType: String) {
    EVENT_NOTIFICATION("EVENT_NOTIFICATION"),
    OTHER_NOTIFICATION("OTHER_NOTIFICATION");

    override fun toString(): String {
        return notificationType
    }
}

/**
 * Notifications Helper that implement notifications scheduling
 * with the Alarm manager, as well as Notification manager related methods to send
 * notifications and/or cancel them
 */
class NotificationsHelper(private val applicationContext: Context) : NotificationsScheduler {
    /**
     * Cancel the notification associated with the id provided as well as the pending intent
     * scheduling the alarm to fire up the notification.
     * @param notificationId the id of the notification to cancel
     */
    override fun cancelNotification(notificationId: Int?) {
        if (notificationId != null) {
            val app = applicationContext as PolyEventsApplication

            // Get the alarm manager from the system
            val alarmManager =
                app.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val pendingIntent =
                PendingIntent.getBroadcast(
                    app, notificationId, Intent(app, AlarmReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            if (pendingIntent != null && alarmManager != null) {
                // Cancel the pending intent
                alarmManager.cancel(pendingIntent)
            }

            val notificationManager = ContextCompat.getSystemService(
                app,
                NotificationManager::class.java
            ) as NotificationManager
            // Cancel the created intent
            notificationManager.cancelNotification(notificationId)
        }
    }

    /**
     * Set the alarm at the scheduled time using the Alarm manager. The alarm manager will notify
     * the alarm receiver which is a broadcast receiver from inside and outside the app, and will
     * trigger a notification.
     * @param scheduledIntent the intent to launch when the scheduled time arrives
     * @param scheduledTime the time at which the intent should be launched
     */
    private fun scheduleNotification(
        scheduledIntent: Intent,
        scheduledTime: LocalDateTime
    ): Int {
        val app = (applicationContext as PolyEventsApplication)

        val newNotificationId = generateNewNotificationId()
        scheduledIntent.putExtra(EXTRA_NOTIFICATION_ID, newNotificationId)

        // Get the alarm manager from the system
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create a pending intent from the intent to be broadcast
        val notifyPendingIntent = PendingIntent.getBroadcast(
            app,
            newNotificationId,
            scheduledIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            HelperFunctions.Converters.fromLocalDateTime(scheduledTime)!!,
            notifyPendingIntent
        )
        return newNotificationId
    }

    /**
     * Schedule an event notification. For that we need to create an intent with all the necessary
     * information, that is the event id and the notification message. Then we call schedule
     * notification with that intent and the notification message
     * @param eventId the id of the event for which we are scheduling a notification
     * @param notificationMessage the body of the message for the notification
     * @param scheduledTime the time at which the notification is scheduled for
     */
    override fun scheduleEventNotification(
        eventId: String,
        notificationMessage: String,
        scheduledTime: LocalDateTime
    ): Int {
        val app = (applicationContext as PolyEventsApplication)
        // Set the alarm before the event start by 15 min
        val notifyEventIntent = Intent(app, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_NOTIFICATION_TYPE, NotificationType.EVENT_NOTIFICATION.toString())
            putExtra(EXTRA_EVENT_ID, eventId)
            putExtra(EXTRA_NOTIFICATION_MESSAGE, notificationMessage)
        }

        return scheduleNotification(
            scheduledIntent = notifyEventIntent,
            scheduledTime = scheduledTime
        )
    }

    /**
     * Generate a new notification id for a notification
     */
    override fun generateNewNotificationId(): Int = runBlocking {
        val app = (applicationContext as PolyEventsApplication)
        val notificationUid = app.localDatabase.notificationUidDao().getNotificationUid()
        if (notificationUid.isEmpty()) {
            // If no notificationUid instance already created, create one
            app.localDatabase.notificationUidDao().insert(NotificationUid(uid = 1))
            0
        } else {
            val currentNotificationUid = notificationUid[0]
            app.localDatabase.notificationUidDao()
                .insert(currentNotificationUid.copy(uid = currentNotificationUid.uid + 1))
            currentNotificationUid.uid
        }
    }
}

/**
 * Extension function to the Notification manager, to create a custom notification.
 * @param messageBody the message that will be displayed in the body of the notification
 * @param applicationContext the context of the application
 * @param notificationId the id for the notification to be set
 * @param contentIntent the intent to be launched, when clicking on the notification
 */
fun NotificationManager.sendEventNotification(
    messageBody: String,
    applicationContext: Context,
    notificationId: Int,
    contentIntent: Intent?
) {

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
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    ).setSmallIcon(R.drawable.ic_ico)
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
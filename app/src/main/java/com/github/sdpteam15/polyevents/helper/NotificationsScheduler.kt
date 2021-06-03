package com.github.sdpteam15.polyevents.helper

import java.time.LocalDateTime

/**
 * Interface for notifications scheduling.
 */
interface NotificationsScheduler {

    /**
     * Schedule a notification for an event at a certain time.
     * @param eventId the id of the event for which we are scheduling a notification
     * @param notificationMessage the body of the message for the notification
     * @param scheduledTime the time at which the notification is scheduled for
     */
    fun scheduleEventNotification(eventId: String,
                                  notificationMessage: String,
                                  scheduledTime: LocalDateTime): Int

    /**
     * Cancel a notification with provided id
     * @param notificationId the id of the notification to cancel
     */
    fun cancelNotification(notificationId: Int?)

    /**
     * Generate a new id for a notification
     */
    fun generateNewNotificationId(): Int
}
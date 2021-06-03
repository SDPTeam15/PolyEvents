package com.github.sdpteam15.polyevents.model.database.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.sdpteam15.polyevents.model.entity.Event
import java.time.LocalDateTime

/**
 * The event entity to be stored in the local database. Contains only the relevant information to
 * be displayed.
 * @property eventId the id of the event. Non nullable since it will serve as primary key
 * @property eventName the name of the event
 * @property organizer the organizer of the event
 * @property zoneId the id of the zone
 * @property zoneName the zone of the event
 * @property description a description of the event
 * @property startTime the starting time of the event (as LocalDateTime)
 * @property endTime the ending time of the event (as LocalDateTime)
 * @property eventStartNotificationId every event stored in the local database will be assigned its own notification
 * with its own unique id. We store the unique id of the notification in the event instance, so we
 * can easily fetch the notification by having this event instance and getting that notification id.
 * This is the notification id of the notification launched at the start of the event
 * @property eventBeforeNotificationId notification id of the notification launched before the event
 * as a reminder.
 */
@Entity(tableName = "event_table")
data class EventLocal(
        // TODO: should we add the user id subscribed to this event?
        // TODO: add event icon?
        @PrimaryKey
        @ColumnInfo(name = "event_id")
        @NonNull
        val eventId: String,
        @ColumnInfo(name = "event_name")
        val eventName: String? = null,
        @ColumnInfo(name = "organizer")
        val organizer: String? = null,
        @ColumnInfo(name = "zoneId")
        val zoneId: String? = null,
        @ColumnInfo(name = "zoneName")
        val zoneName: String? = null,
        @ColumnInfo(name = "description")
        var description: String? = null,
        @ColumnInfo(name = "start_time")
        val startTime: LocalDateTime? = null,
        @ColumnInfo(name = "end_time")
        val endTime: LocalDateTime? = null,
        @ColumnInfo(name = "event_start_notification_id")
        var eventStartNotificationId: Int? = null,
        @ColumnInfo(name = "event_before_notification_id")
        var eventBeforeNotificationId: Int? = null,
        @ColumnInfo(name = "is_limited")
        val isLimited: Boolean = false
) {
    fun toEvent(): Event =
            Event(
                    eventId = eventId,
                    eventName = eventName,
                    organizer = organizer,
                    zoneId = zoneId,
                    zoneName = zoneName,
                    description = description,
                    startTime = startTime,
                    endTime = endTime,
                    limitedEvent = isLimited
            )

    companion object {
        fun fromEvent(e: Event) =
                EventLocal(
                        eventId = e.eventId!!,
                        eventName = e.eventName,
                        organizer = e.organizer,
                        zoneId = e.zoneId,
                        zoneName = e.zoneName,
                        description = e.description,
                        startTime = e.startTime,
                        endTime = e.endTime,
                        isLimited = e.isLimitedEvent()
                )
    }
}
package com.github.sdpteam15.polyevents.model.room

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
 * // TODO: modify zone if necessary
 * @property zoneName
 * @property description a description of the event
 * @property startTime the starting time of the event (as LocalDateTime)
 * @property endTime the ending time of the event (as LocalDateTime)
 * @property tags tags of the event
 */
@Entity(tableName = "event_table")
data class EventLocal(
        // TODO: should we add the user id subscribed to this event?
        // TODO: add event icon?
        @PrimaryKey
        @ColumnInfo(name="event_id")
        @NonNull
        val eventId: String,
        @ColumnInfo(name = "event_name")
        val eventName: String? = null,
        @ColumnInfo(name = "organizer")
        val organizer: String? = null,
        @ColumnInfo(name = "zoneName")
        val zoneName: String? = null,
        @ColumnInfo(name = "description")
        var description: String? = null,
        @ColumnInfo(name = "start_time")
        val startTime: LocalDateTime? = null,
        @ColumnInfo(name = "end_time")
        val endTime: LocalDateTime? = null,
        @ColumnInfo(name = "tags")
        val tags: MutableSet<String> = mutableSetOf()
) {
        fun toEvent(): Event =
                Event(
                        eventId = eventId,
                        eventName = eventName,
                        organizer = organizer,
                        zoneName = zoneName,
                        description = description,
                        startTime = startTime,
                        endTime = endTime,
                        // create a copy of the set
                        tags = tags.toMutableSet()
                )

        companion object {
                fun fromEvent(e: Event) =
                        EventLocal(
                                eventId = e.eventId!!,
                                eventName = e.eventName,
                                organizer = e.organizer,
                                zoneName = e.zoneName,
                                description = e.description,
                                startTime = e.startTime,
                                endTime = e.endTime,
                                // create a copy of the set
                                tags = e.tags.toMutableSet()
                        )
        }
}
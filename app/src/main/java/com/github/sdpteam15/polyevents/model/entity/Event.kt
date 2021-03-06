package com.github.sdpteam15.polyevents.model.entity

import android.graphics.Bitmap
import com.github.sdpteam15.polyevents.helper.HelperFunctions.formatDateTimeWithRespectToAnotherDate
import com.github.sdpteam15.polyevents.model.exceptions.MaxAttendeesException
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Entity model for an activity during the event. Renamed Event
 * to avoid confusion with the Android Activity class.
 *
 * @property eventId the uid of the event document in the database.
 * @property eventName the name of the event.
 * @property organizer the username of the organizer of the event
 * @property zoneId the id of the zone where the event is happening
 * @property zoneName the name of the zone where the event is happening
 * @property description a description of the event
 * @property inventory the list of items in the event's inventory
 * @property tags additional set of tags to describe the event
 * @property startTime the time at which the event begins
 * @property endTime the time at which the event ends
 * @property limitedEvent specifies whether the event has a maximum number of attendees
 * @property maxNumberOfSlots the maximum amount of attendees to this event
 * @property icon a bitmap picture of the icon associated to the event
 */
@IgnoreExtraProperties
data class Event(
    var eventId: String? = null,
    val eventName: String? = null,
    val organizer: String? = null,
    val zoneId: String? = null,
    val zoneName: String? = null,
    var description: String? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val inventory: MutableList<Item> = mutableListOf(),
    // NOTE: Set is not a supported collection in Firebase Firestore so will be stored as list in the db.
    val tags: MutableList<String> = mutableListOf(),

    var status: EventStatus? = null,
    var adminMessage: String? = null,
    var eventEditId: String? = null,
    // Not used currently
    var icon: Bitmap? = null,

    private var limitedEvent: Boolean = false,
    private var maxNumberOfSlots: Int? = null,
    private val participants: MutableSet<String> = mutableSetOf()
) {

    /**
     * Get the maximum number of slots if this event is limited
     */
    fun getMaxNumberOfSlots(): Int? = if (limitedEvent) maxNumberOfSlots else null

    /**
     * Check if event is limited
     */
    fun isLimitedEvent(): Boolean = limitedEvent

    /**
     * Clear the event of its participants.
     * Only for testing purposes
     */
    fun clearParticipants() {
        participants.clear()
    }

    /**
     * Get a copy of the event participants
     */
    fun getParticipants() = participants.toMutableSet()

    /**
     * Remove a participant from event
     * @param userUid the id of the user to remove from the participant set
     */
    fun removeParticipant(userUid: String) {
        this.participants.remove(userUid)
    }

    /**
     * Make this event limited and set the maximum number of attendees for this event.
     * @param maxNumberOfSlots the maximum number of people that can attend this event
     * @throws IllegalArgumentException if the maximum number of slots is negative
     */
    fun makeLimitedEvent(maxNumberOfSlots: Int) {
        if (maxNumberOfSlots < 0) {
            throw IllegalArgumentException("Cannot have a negative number of slots")
        }
        limitedEvent = true
        this.maxNumberOfSlots = maxNumberOfSlots
    }

    /**
     * Add a participant to this event if it's a limited event
     * @param userUid the id of the new participant user
     * @throws IllegalArgumentException if the event is not a limited event
     * @throws MaxAttendeesException if the event is already at max capacity
     */
    fun addParticipant(userUid: String) {
        if (!this.limitedEvent) {
            throw IllegalArgumentException("This event is not a limited event to add participants")
        }
        if (this.participants.size >= maxNumberOfSlots!!) {
            throw MaxAttendeesException("Reached the maximum number of attendees for this event")
        }
        this.participants.add(userUid)
    }

    /**
     * Add a new tag for this activity
     * @param newTag : the tag to add
     * @return true if this tag was successfully added
     */
    fun addTag(newTag: String): Boolean {
        return tags.add(newTag)
    }

    /**
     * Remove a tag for this activity
     * @param tag : the tag to remove
     * @return true if this tag was successfully remove, false if it
     * was not present
     */
    fun removeTag(tag: String): Boolean {
        return tags.remove(tag)
    }

    /**
     * Add an item to the event's inventory
     * @param item the item to add
     * @return true if the item was successfully added
     */
    fun addItem(item: Item): Boolean =
        inventory.add(item)

    /**
     * Remove item from the event's inventory
     * @param item the item to remove
     * @return true if the item was successfully removed
     */
    fun removeItem(item: Item): Boolean =
        inventory.remove(item)

    /**
     * Check if event's inventory has certain item
     * @param item the item to check
     * @return true if the item is there
     */
    fun hasItem(item: Item): Boolean =
        inventory.contains(item)

    /**
     * Return the formatted start time of the event
     */
    fun formattedStartTime(): String =
        formatDateTimeWithRespectToAnotherDate(startTime, LocalDate.now())

    enum class EventStatus(private val status: String) {
        PENDING("pending"),
        ACCEPTED("accepted"),
        REFUSED("refused"),
        CANCELED("canceled");

        override fun toString(): String {
            return status
        }

        companion object {
            private val map = values().associateBy(EventStatus::status)
            private val mapOrdinal = map.mapKeys { it.value.ordinal }
            /**
             * Return the EventStatus status corresponding to the given ordinal
             * @param ordinal The index of the EventStatus we want to retrieve
             */
            fun fromOrdinal(ordinal: Int) = mapOrdinal[ordinal]
        }
    }
}
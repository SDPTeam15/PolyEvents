package com.github.sdpteam15.polyevents.model

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.exceptions.MaxAttendeesException
import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Entity model for an activity during the event. Renamed Event
 * to avoid confusion with the Android Activity class.
 *
 * @property eventId the uid of the event document in the database.
 * @property eventName the name of the event.
 * @property organizer the username of the organizer of the event
 * @property zoneName the name of the zone where the event is happening
 * @property description a description of the event
 * @property icon a bitmap picture of the icon associated to the event
 * @property inventory the list of items in the event's inventory
 * @property tags additional set of tags to describe the event
 * @property startTime the time at which the event begins
 * @property endTime the time at which the event ends
 * @property limitedEvent specifies whether the event has a maximum number of attendees
 * @property maxNumberOfSlots the maximum amount of attendees to this event
 */
// TODO: look into storing instances of LocalDateTime, or Long (for startTime and endTime)
// TODO: Should the eventName be unique? (Important when writing security rules)
// TODO: keep track of items, or just required items?
// TODO: add location
@IgnoreExtraProperties
data class Event(
    val eventId: String? = null,
    val eventName: String? = null,
    val organizer: String? = null,
    val zoneName: String? = null,
    var description: String? = null,
    var icon: Bitmap? = null,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val inventory: MutableList<Item> = mutableListOf(),
    // NOTE: Set is not a supported collection in Firebase Firestore so will be stored as list in the db.
    val tags: MutableSet<String> = mutableSetOf(),
    var limitedEvent: Boolean = false,
    var maxNumberOfSlots: Int? = 0,
) {
    /**
     * Set the maximum number of participant slots for this event if it's a limited event.
     * @param slots: the maximum number of slots for this event
     */
    fun setMaxNumberOfSlots(slots: Int) {
        if (!limitedEvent) {
            limitedEvent = true
        }
        maxNumberOfSlots = slots
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
     * Return the hour (and minutes) at which the activity occurs. Uses k:mm pattern, k for
     * hour going between 0-23h.
     * @return string HH:MM
     */
    fun formattedStartTime(): String {
        if (startTime == null) {
            return ""
        } else {
            //return SimpleDateFormat("k:mm", Locale.getDefault()).format(startTime)
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("k:mm")
            return startTime.format(formatter)
        }
    }

    companion object {
        const val DEFAULT_DURATION = 1F
    }
}
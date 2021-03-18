package com.github.sdpteam15.polyevents.model

import android.graphics.Bitmap
import com.github.sdpteam15.polyevents.exceptions.InsufficientAmountException
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Entity model for an activity during the event. Renamed Event
 * to avoid confusion with the Android Activity class.
 *
 * @property eventName the name of the event.
 * @property organizer the username of the organizer of the event
 * @property zoneName the name of the zone where the event is happening
 * @property description a description of the event
 * @property icon a bitmap picture of the icon associated to the event
 * @property inventory a map of items and their availabilities
 * @property tags additional set of tags to describe the event
 */
// TODO: look into storing instances of LocalDateTime, or Long (for startTime and endTime)
// TODO: Should the eventName be unique?
// TODO: keep track of items, or just required items?
@IgnoreExtraProperties
data class Event(
        val eventName: String? = null,
        val organizer: String? = null,
        val zoneName: String? = null,
        val description: String? = null,
        val icon: Bitmap? = null,
        @ServerTimestamp val startTime: Date? = null,
        @ServerTimestamp val endTime: Date? = null,
        val inventory: MutableMap<Item, Int> = mutableMapOf(),
        // NOTE: Set is not a supported collection in Firebase Firestore
        val tags: MutableSet<String> = mutableSetOf()
) {
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
     * Add an amount of an item to the inventory of the event
     * @param item: the item to add
     * @param amount the amount of the item to add. By default is 1
     * @return the old previous amount of that item, or null if the item was not found
     */
    fun addItem(item: Item, amount: Int = 1): Int? =
        inventory.put(item, amount)

    /**
     * Take an amount of some item from the inventory of the event.
     *
     * @param item the item to take
     * @param amount amount of the item to take. By default is 1
     * @return the old previous amount of that item, or null if the item was not found
     */
    fun takeItem(item: Item, amount: Int = 1): Int? {
        val currentAmount = inventory.getOrDefault(item, 0)
        if (amount > currentAmount) {
            throw InsufficientAmountException("There are only $currentAmount of $item available")
        } else {
            return inventory.put(item, currentAmount - amount)
        }
    }

    /**
     * Return the hour (and minutes) at which the activity occurs
     * @return string HH:MM
     */
    fun formattedStartTime(): String {
        if (startTime == null) {
            return ""
        } else {
            return SimpleDateFormat("k:mm", Locale.getDefault()).format(startTime)
        }
    }

    companion object {
        const val DEFAULT_DURATION = 1F
    }
}
package com.github.sdpteam15.polyevents.event

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.Database.Companion.currentDatabase
import java.time.LocalDateTime

const val EVENT_DEFAULT_DURATION = 1F

/**
 * Describe an event
 * @property name its name
 * @property description description
 * @property start when it happens
 * @property organizer its duration
 * @param durationHours the organizer of this event
 * @property organizer the zone where the event is
 * @property icon an icon for this event to display
 * @property id an id to uniquely identify the event
 * @property tags the tags the event corresponds to
 */
class Event(
    var name: String,
    var description: String,
    var start: LocalDateTime,
    durationHours: Float,
    var organizer: String,
    var zone: String,
    var icon: Bitmap? = null,
    val id: String,
    val tags: MutableSet<String> = mutableSetOf()
) {

    /**
     * Duration of the event
     */
    var durationHours: Float = EVENT_DEFAULT_DURATION
        set(durationH) {
            field = if (durationH < 0) EVENT_DEFAULT_DURATION else durationH
        }

    init {
        this@Event.durationHours = durationHours
    }

    /**
     * Add a new tag for this event
     * @param newTag : the tag to add
     * @return true if this tag was successfully added
     */
    fun addTag(newTag: String): Boolean {
        val result = tags.add(newTag)
        if (result)
            currentDatabase.updateEvent(this);
        return result
    }

    /**
     * Remove a tag for this event
     * @param tag : the tag to remove
     * @return true if this tag was successfully remove, false if it
     * was not present
     */
    fun removeTag(tag: String): Boolean {
        val result = tags.remove(tag)
        if (result)
            currentDatabase.updateEvent(this);
        return result
    }

    /**
     * Return the hour (and minutes) at which the event occurs
     * @return string HH:MM
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String {
        val hour = start.hour.toString().padStart(2, '0')
        val minute = start.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }
}
package com.github.sdpteam15.polyevents.activity

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

/**
 * Describe an activity :
 * - its name
 * - description
 * - when it happens
 * - its duration
 * - the organizer of this activity
 * - the zone where the activity is
 * - an icon for this activity to display
 * - an id to uniquely identify the activity
 * - the tags the activity corresponds to
 *
 */
class Activity(
    var name: String,
    var description: String,
    var start: LocalDateTime,
    durationHours: Float,
    var organizer: String,
    var zone: String,
    var icon: Bitmap?,
    val id: String,
    val tags: MutableSet<String> = mutableSetOf()) {

    var durationHours: Float = durationHours
        set(durationH) {
            field = if (durationH < 0) Companion.DEFAULT_DURATION else durationH
        }

    init {
        if (durationHours < 0) {
            this@Activity.durationHours = Companion.DEFAULT_DURATION
        }
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
     * Return the hour (and minutes) at which the activity occurs
     * @return string HH:MM
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String {
        val hour = start.hour.toString().padStart(2, '0')
        val minute = start.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    companion object {
        const val DEFAULT_DURATION = 1F
    }
}
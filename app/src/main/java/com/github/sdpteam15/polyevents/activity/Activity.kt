package com.github.sdpteam15.polyevents.activity

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import java.time.LocalDateTime

const val ACTIVITY_DEFAULT_DURATION = 1F

/**
 * Describe an activit
 * @property name its name
 * @property description description
 * @property start when it happens
 * @property organizer its duration
 * @param durationHours the organizer of this activity
 * @property organizer the zone where the activity is
 * @property icon an icon for this activity to display
 * @property id an id to uniquely identify the activity
 * @property tags the tags the activity corresponds to
 */
class Activity(
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
     * Duration of the activity
     */
    var durationHours: Float = ACTIVITY_DEFAULT_DURATION
        set(durationH) {
            field = if (durationH < 0) ACTIVITY_DEFAULT_DURATION else durationH
        }

    init {
        this@Activity.durationHours = durationHours
    }

    /**
     * Add a new tag for this activity
     * @param newTag : the tag to add
     * @return true if this tag was successfully added
     */
    fun addTag(newTag: String): Boolean {
        val result = tags.add(newTag)
        if (result)
            currentDatabase.updateActivity(this);
        return result
    }

    /**
     * Remove a tag for this activity
     * @param tag : the tag to remove
     * @return true if this tag was successfully remove, false if it
     * was not present
     */
    fun removeTag(tag: String): Boolean {
        val result = tags.remove(tag)
        if (result)
            currentDatabase.updateActivity(this);
        return result
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
}
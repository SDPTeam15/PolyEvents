package com.github.sdpteam15.polyevents.activity

import android.graphics.Bitmap
import java.time.LocalDateTime

/**
 * Describe an activity :
 * - its name
 * - description
 * - when it happens
 * - the organizer of this activity
 * - the zone where the activity is
 * - the tags the activity corresponds to
 * - an icon for this activity to display
 */
interface ActivityInterface {

    var name: String
    var description: String
    var start: LocalDateTime
    var durationHours: Float
    var organizer: String
    var zone: String
    val tags: MutableSet<String>
    var icon: Bitmap?

    /**
     * Add a new tag for this activity
     * @param newTag : the tag to add
     * @return true if this tag was successfully added
     */
    fun addTag(newTag: String): Boolean

    /**
     * Remove a tag for this activity
     * @param tag : the tag to remove
     * @return true if this tag was successfully remove, false if it
     * was not present
     */
    fun removeTag(tag: String): Boolean
}
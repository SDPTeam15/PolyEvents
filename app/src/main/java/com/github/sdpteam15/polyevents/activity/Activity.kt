package com.github.sdpteam15.polyevents.activity

import android.graphics.Bitmap
import java.time.LocalDateTime

/**
 * Implements the activity interface.
 */
data class Activity(override val name: String,
                    override val description: String,
                    override val schedule: Pair<LocalDateTime, LocalDateTime>,
                    override val organizer: String,
                    override val zone: String,
                    override val icon: Bitmap?,
                    override val tags: MutableSet<String> = mutableSetOf()) : ActivityInterface {

    override fun addTag(newTag: String): Boolean {
        return tags.add(newTag)
    }

    override fun removeTag(tag: String): Boolean {
        return tags.remove(tag)
    }
}
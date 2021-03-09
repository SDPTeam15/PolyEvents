package com.github.sdpteam15.polyevents.activity

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

/**
 * Implements the activity interface.
 */
data class Activity(
    override var name: String,
    override var description: String,
    override var start: LocalDateTime,
    val durationHrs: Float,
    override var organizer: String,
    override var zone: String,
    override var icon: Bitmap?,
    override val tags: MutableSet<String> = mutableSetOf()
) : ActivityInterface {

    override var durationHours: Float = durationHrs
        set(durationH) {
            field = if (durationH < 0) Companion.DEFAULT_DURATION else durationH
        }

    init {
        if (durationHours < 0) {
            durationHours = Companion.DEFAULT_DURATION
        }
    }

    override fun addTag(newTag: String): Boolean {
        return tags.add(newTag)
    }

    override fun removeTag(tag: String): Boolean {
        return tags.remove(tag)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTime(): String {
        val hour = start.hour.toString().padStart(2, '0')
        val minute = start.minute.toString().padStart(2, '0')
        return "$hour:$minute"
    }

    companion object {
        const val DEFAULT_DURATION = 1F
    }
}
package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.activity.Activity
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * Unit tests for the Activity implementation.
 */

class ActivityTest {

    lateinit var activity: Activity

    @Before
    fun createNewActivity() {
        activity = Activity(
                "Test Activity", "Activity to make tests !",
                LocalDateTime.of(2020, 3, 15, 14, 0),
                "The best organizer", "A", null)
        activity.durationHours = 3F
    }

    @Test
    fun addTagIsCorrect() {
        val newTag = "Food"
        activity.addTag(newTag)
        assertThat(activity.tags, hasItem(newTag))
    }

    @Test
    fun removeTagIsCorrect() {
        val newTag = "Movie"
        activity.addTag(newTag)

        activity.removeTag(newTag)
        assertThat(activity.tags, not(hasItem(newTag)))
    }

    @Test
    fun negativeDurationIsResetToDefault() {
        val newActivity = Activity(
                "Test Activity", "Activity to make tests !",
                LocalDateTime.of(2020, 3, 15, 14, 0),
                "The best organizer", "A", null)
        activity.durationHours = -1.5F

        assertThat(activity.durationHours, Is(Activity.DEFAULT_DURATION))
    }
}
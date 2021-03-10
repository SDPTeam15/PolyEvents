package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.activity.Activity
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
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
                3.5F,
                "The best organizer", "A", null)
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
                -2.25F,
                "The best organizer", "A", null)

        assertThat(newActivity.durationHours, Is(Activity.DEFAULT_DURATION))
        newActivity.durationHours = -1.5F
        assertThat(newActivity.durationHours, Is(Activity.DEFAULT_DURATION))
    }

    @Test
    fun setGetNameIsCorrect() {
        val newName = "New Name de bleu !"
        activity.name = newName
        assertThat(activity.name, Is(newName))
    }

    @Test
    fun setGetDescriptionIsCorrect() {
        val newDesc = "New description"
        activity.description = newDesc
        assertThat(activity.description, Is(newDesc))
    }

    @Test
    fun setGetStartIsCorrect() {
        val newStart = LocalDateTime.of(2420, 9, 1, 4, 55)
        activity.start = newStart
        assertThat(activity.start, Is(newStart))
    }

    @Test
    fun setGetDurationIsCorrect() {
        val newDuration = 3.75F
        activity.durationHours = newDuration
        assertThat(activity.durationHours, Is(newDuration))
    }

    @Test
    fun setGetOrganizerIsCorrect() {
        val newOrganizer = "New organizer"
        activity.organizer = newOrganizer
        assertThat(activity.organizer, Is(newOrganizer))
    }

    @Test
    fun setGetZoneIsCorrect() {
        val newZone = "New zone"
        activity.zone = newZone
        assertThat(activity.zone, Is(newZone))
    }
}
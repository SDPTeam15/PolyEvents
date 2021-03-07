package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.activity.Activity
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.time.LocalDateTime
import java.util.*

/**
 * Unit tests for the Activity implementation.
 */

class ActivityTest {

    lateinit var activity: Activity

    @Before
    fun createNewActivity() {
        activity = Activity("Test Activity","Activity to make tests !",
                            Pair(LocalDateTime.of(2020, 3, 15, 14, 0),
                                 LocalDateTime.of(2020, 3, 15, 18, 0)),
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
}
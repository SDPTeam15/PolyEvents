package com.github.sdpteam15.polyevents.event

import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.FakeDatabase
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * Unit tests for the Activity implementation.
 */

class EventTest {

    lateinit var event: Event

    @Before
    fun createNewEvent() {
        currentDatabase = FakeDatabase
        event = Event(
            "Test Event", "Event to make tests !",
            LocalDateTime.of(2020, 3, 15, 14, 0),
            3.5F,
            "The best organizer", "A", null, "1"
        )
    }

    @Test
    fun addTagIsCorrect() {
        val newTag = "Food"
        event.addTag(newTag)
        assertThat(event.tags, hasItem(newTag))
    }

    @Test
    fun removeTagIsCorrect() {
        val newTag = "Movie"
        event.addTag(newTag)

        event.removeTag(newTag)
        assertThat(event.tags, not(hasItem(newTag)))
    }

    @Test
    fun negativeDurationIsResetToDefault() {
        val newEvent = Event(
            "Test Event", "Event to make tests !",
            LocalDateTime.of(2020, 3, 15, 14, 0),
            -2.25F,
            "The best organizer", "A", null, "2"
        )

        assertThat(newEvent.durationHours, Is(EVENT_DEFAULT_DURATION))
        newEvent.durationHours = -1.5F
        assertThat(newEvent.durationHours, Is(EVENT_DEFAULT_DURATION))
    }

    @Test
    fun setGetNameIsCorrect() {
        val newName = "New Name de bleu !"
        event.name = newName
        assertThat(event.name, Is(newName))
    }

    @Test
    fun setGetDescriptionIsCorrect() {
        val newDesc = "New description"
        event.description = newDesc
        assertThat(event.description, Is(newDesc))
    }

    @Test
    fun setGetStartIsCorrect() {
        val newStart = LocalDateTime.of(2420, 9, 1, 4, 55)
        event.start = newStart
        assertThat(event.start, Is(newStart))
    }

    @Test
    fun setGetDurationIsCorrect() {
        val newDuration = 3.75F
        event.durationHours = newDuration
        assertThat(event.durationHours, Is(newDuration))
    }

    @Test
    fun setGetOrganizerIsCorrect() {
        val newOrganizer = "New organizer"
        event.organizer = newOrganizer
        assertThat(event.organizer, Is(newOrganizer))
    }

    @Test
    fun setGetZoneIsCorrect() {
        val newZone = "New zone"
        event.zone = newZone
        assertThat(event.zone, Is(newZone))
    }
}
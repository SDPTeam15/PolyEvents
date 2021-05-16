package com.github.sdpteam15.polyevents.view.fragments

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime

class ZoneEventsFragmentTest {
    private lateinit var zoneId: String
    private lateinit var zoneName: String

    private lateinit var event1: Event
    private lateinit var event2: Event
    private lateinit var event3: Event

    private val firstEventName = "Sushi demo"

    private lateinit var mockedDatabase: DatabaseInterface
    private lateinit var mockedEventDatabase: EventDatabaseInterface
    private lateinit var mockedZoneDatabase: ZoneDatabaseInterface

    private lateinit var scenario: FragmentScenario<ZoneEventsFragment>

    @Suppress("UNCHECKED_CAST")
    @Before
    fun setup() {
        zoneId = "zone1"
        zoneName = "The Zone"

        event1 = Event(
            eventName = firstEventName,
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
            endTime = LocalDateTime.of(2021, 3, 7, 13, 0, 0),
            organizer = "The fish band",
            zoneName = "Kitchen",
            tags = mutableSetOf("sushi", "japan", "cooking")
        )
        event1.makeLimitedEvent(25)

        event2 = Event(
            eventName = "Aqua Poney",
            description = "Super cool activity !" +
                    " With a super long description that essentially describes and explains" +
                    " the content of the activity we are speaking of.",
            startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
            organizer = "The Aqua Poney team",
            zoneName = "Swimming pool"
        )
        event2.makeLimitedEvent(25)

        event3 = Event(
            eventName = "Concert",
            description = "Super noisy activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 21, 15),
            organizer = "AcademiC DeCibel",
            zoneName = "Concert Hall",
            tags = mutableSetOf("music", "live", "pogo")
        )

        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        mockedEventDatabase = Mockito.mock(EventDatabaseInterface::class.java)
        mockedZoneDatabase = Mockito.mock(ZoneDatabaseInterface::class.java)

        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(
            mockedEventDatabase
        )
        Mockito.`when`(mockedDatabase.zoneDatabase).thenReturn(
            mockedZoneDatabase
        )

        Mockito.`when`(
            mockedDatabase.eventDatabase!!.getEventsByZoneId(
                zoneId = anyOrNull(),
                limit = anyOrNull(),
                events = anyOrNull(),
                userAccess = anyOrNull()
            )
        ).then {
            (it.arguments[2] as ObservableList<Event>?)
                ?.addAll(mutableListOf(event1, event2, event3))
            Observable(true)
        }

        Database.currentDatabase = mockedDatabase

        scenario = launchFragmentInContainer<ZoneEventsFragment>(
            fragmentArgs = bundleOf(
                ZonePreviewBottomSheetDialogFragment.EXTRA_ZONE_ID to zoneId,
                ZonePreviewBottomSheetDialogFragment.EXTRA_ZONE_NAME to zoneName
            ),
            themeResId = R.style.Theme_PolyEvents
        )
    }

    @Test
    fun testZoneEventsFragmentCorrectlyDisplayed() {
        assertDisplayed(R.id.fragment_zone_events_zone_name_text_view, zoneName)
        assertDisplayed(R.id.zone_events_fragment_recycler_view)
    }

    @Test
    fun testZoneEventsCorrectlyDisplayed() {
        assertListItemCount(R.id.zone_events_fragment_recycler_view, 3)
        assertDisplayedAtPosition(
            R.id.zone_events_fragment_recycler_view, 0,
            R.id.id_event_name_text, firstEventName
        )
    }
}
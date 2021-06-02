package com.github.sdpteam15.polyevents.view.fragments

import android.content.Context
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.fragments.ZonePreviewBottomSheetDialogFragment.Companion.EXTRA_ZONE_ID
import com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertDisabled
import com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertEnabled
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When


class ZonePreviewBottomSheetDialogFragmentTest {
    private lateinit var zone: Zone
    private lateinit var event1: Event
    private lateinit var event2: Event
    private lateinit var event3: Event

    private val firstEventName = "Sushi demo"

    private lateinit var mockedDatabase: DatabaseInterface
    private lateinit var mockedEventDatabase: EventDatabaseInterface
    private lateinit var mockedZoneDatabase: ZoneDatabaseInterface

    private lateinit var scenario: FragmentScenario<ZonePreviewBottomSheetDialogFragment>

    @Suppress("UNCHECKED_CAST")
    @Before
    fun setup() {
        zone = Zone(
            zoneId = "zone1",
            zoneName = "The zone",
            description = "Big zone",
            location = "here"
        )

        event1 = Event(
            eventName = firstEventName,
            description = "Super hungry activity !",
            startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
            endTime = LocalDateTime.of(2021, 3, 7, 13, 0, 0),
            organizer = "The fish band",
            zoneName = "Kitchen",
            tags = mutableListOf("sushi", "japan", "cooking")
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
            tags = mutableListOf("music", "live", "pogo")
        )

        mockedDatabase = mock(DatabaseInterface::class.java)
        mockedEventDatabase = mock(EventDatabaseInterface::class.java)
        mockedZoneDatabase = mock(ZoneDatabaseInterface::class.java)

        When(mockedDatabase.eventDatabase).thenReturn(
            mockedEventDatabase
        )
        When(mockedDatabase.zoneDatabase).thenReturn(
            mockedZoneDatabase
        )

        When(mockedDatabase.eventDatabase!!.getEventsByZoneId(
            zoneId = anyOrNull(),
            limit = anyOrNull(),
            events = anyOrNull()
        )).then {
            (it.arguments[2] as ObservableList<Event>?)
                ?.addAll(mutableListOf(event1, event2, event3))
            Observable(true)
        }

        When(mockedZoneDatabase.getZoneInformation(
            zoneId = anyOrNull(),
            zone = anyOrNull()
        )).then {
            (it.arguments[1] as Observable<Zone>?)
                ?.postValue(zone)
            Observable(true)
        }

        currentDatabase = mockedDatabase

        scenario = launchFragmentInContainer<ZonePreviewBottomSheetDialogFragment>(
            fragmentArgs = bundleOf(EXTRA_ZONE_ID to zone.zoneId),
            themeResId = R.style.Theme_PolyEvents
        ).onFragment {
            val fragment = it
            // Set listener to just dimiss dialogs, we are not testing the other fragments
            fragment.setOnItineraryClickListener {
                fragment.dismiss()
            }
            fragment.setOnShowEventsClickListener {
                fragment.dismiss()
            }
        }
    }

    @After
    fun teardown() {
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun testBottomSheetDialogCorrectlyDisplayed() {
        assertDisplayed(R.id.preview_zone_events_bottom_sheet_dialog)

        assertDisplayed(R.id.zone_preview_dialog_zone_name, zone.zoneName!!)
        assertDisplayed(R.id.zone_preview_dialog_zone_description, zone.description!!)

        assertDisplayed(R.id.zone_preview_show_events_button)
        assertDisplayed(R.id.zone_preview_show_itinerary_button)

        assertDisplayed(R.id.zone_preview_dialog_event_recycler_view)

        assertDisplayed(R.id.zone_preview_dialog_upcoming_events)

    }
    
    @Test
    fun testDialogButtonsAreDisabledByDefault() {
        When(mockedZoneDatabase.getZoneInformation(
            zoneId = anyOrNull(),
            zone = anyOrNull()
        )).then {
            Observable(false)
        }

        scenario.recreate()

        assertDisabled(R.id.zone_preview_show_itinerary_button)
        assertDisabled(R.id.zone_preview_show_events_button)
    }

    @Test
    fun testUpcomingEventsCorrectlyDisplayed() {
        assertDisplayed(R.id.zone_preview_dialog_event_recycler_view)

        assertListItemCount(R.id.zone_preview_dialog_event_recycler_view, 3)

        assertDisplayedAtPosition(
            R.id.zone_preview_dialog_event_recycler_view, 0,
            R.id.card_preview_event_name, firstEventName
        )
    }

    @Test
    fun testOnClickItineraryGoToMapFragment() {
        assertDisplayed(R.id.preview_zone_events_bottom_sheet_dialog)
        assertEnabled(R.id.zone_preview_show_itinerary_button)
        clickOn(R.id.zone_preview_show_itinerary_button)
        assertNotExist(R.id.preview_zone_events_bottom_sheet_dialog)
    }

    @Test
    fun testOnClickSeeEventsGoToEventsPreviewFragment() {
        assertDisplayed(R.id.preview_zone_events_bottom_sheet_dialog)
        assertEnabled(R.id.zone_preview_show_events_button)
        clickOn(R.id.zone_preview_show_events_button)
        assertNotExist(R.id.preview_zone_events_bottom_sheet_dialog)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun testNoUpcomingEventsDisplayedWhenNoEvents() {
        When(mockedEventDatabase.getEventsByZoneId(
            zoneId = anyOrNull(),
            limit = anyOrNull(),
            events = anyOrNull()
        )).thenAnswer {
            (it.arguments[2] as ObservableList<Event>)
                .clear()
            Observable(true)
        }

        scenario.recreate()

        val context: Context = ApplicationProvider.getApplicationContext()
        assertDisplayed(
            R.id.zone_preview_dialog_upcoming_events,
            context.resources.getString(R.string.no_upcoming_events)
        )
    }

    @Test
    fun testCreatingNewInstanceOfZonePreviewFragment() {
        val f = ZonePreviewBottomSheetDialogFragment.newInstance(
            zone.zoneId!!,
            onItineraryClickListener = {

            },
            onShowEventsClickListener = {

            }
        )
        assertEquals(f.arguments!![EXTRA_ZONE_ID], zone.zoneId)
    }
}
package com.github.sdpteam15.polyevents.view.activity

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementListActivity
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import java.sql.Time
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TimeTableActivityTest {
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedEventDB: EventDatabaseInterface
    lateinit var scenario: ActivityScenario<TimeTableActivity>

    private val event1 =
        Event(
            eventName = "Test 1",
            eventId = "Id1",
            zoneId = " zid1",
            zoneName = "zoneName1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now()
        )
    private val event2 =
        Event(
            eventName = "Test 2",
            eventId = "Id2",
            zoneId = " zid1",
            zoneName = "zoneName1",
            startTime = LocalDateTime.now().plusDays(1),
            endTime = LocalDateTime.now().plusDays(1)
        )
    private val event3 =
        Event(
            eventName = "Test 3",
            eventId = "Id3",
            zoneId = " zid2",
            zoneName = "zoneName2",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now()
        )
    private val event4 =
        Event(
            eventName = "Test 4",
            eventId = "Id4",
            zoneId = " zid3",
            zoneName = "zoneName3",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now()
        )
    private lateinit var events: MutableList<Event>
    private lateinit var zones: MutableList<Zone>
    private var nbzones: Int=0

    private fun setupEventsAndZones() {
        events = mutableListOf(event1, event2, event3, event4)
        zones = mutableListOf(
            Zone(zoneId = "zid1", zoneName = "zoneName1"),
            Zone(zoneId = "zid2", zoneName = "zoneName2"),
            Zone(zoneId = "zid3", zoneName = "zoneName3")
        )
        nbzones = zones.size
    }

    @Before
    fun setup() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            TimeTableActivity::class.java
        )

        setupEventsAndZones()

        val mockedZoneDB = Mockito.mock(ZoneDatabaseInterface::class.java)

        Mockito.`when`(
            mockedZoneDB.getAllZones(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[2] as ObservableList<Zone>).add(
                Zone(
                    zoneName = "Test zone",
                    zoneId = "zoneId"
                )
            )
            Observable(true)
        }
        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        mockedEventDB = Mockito.mock(EventDatabaseInterface::class.java)

        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        Mockito.`when`(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)
        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[2] as ObservableList<Event>).addAll(events)
                Observable(true, this)
            }

        Mockito.`when`(mockedEventDB.getEventFromId(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[1] as Observable<Event>).postValue(event1)
                Observable(true)
            }

        Database.currentDatabase = mockedDatabase
        scenario = ActivityScenario.launch(intent)
    }

    @Test
    fun goesToActivity(){
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        Espresso.onView(ViewMatchers.withId(R.id.id_change_zone_right))
            .perform(ViewActions.click())
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        Espresso.onView(ViewMatchers.withId(R.id.id_change_zone_left))
            .perform(ViewActions.click())
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        Espresso.onView(ViewMatchers.withId(R.id.id_title_zone))
            .perform(ViewActions.click())
        Espresso.onData(CoreMatchers.anything()).atPosition(0).perform(ViewActions.click())
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        Espresso.onView(ViewMatchers.withId(R.id.id_title_zone))
            .perform(ViewActions.click())
        Espresso.onData(CoreMatchers.anything()).atPosition(1).perform(ViewActions.click())
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        Espresso.onView(ViewMatchers.withId(R.id.id_title_zone))
            .perform(ViewActions.click())
        Espresso.onData(CoreMatchers.anything()).atPosition(2).perform(ViewActions.click())
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        val view = TimeTableActivity.instance!!.displayedViews.first()
    }

    @Test
    fun generateTimeTest(){
        Thread.sleep(1000)
        val v1 = 42
        val v2 = 34
        val v3 = 39
        val v4 = 50
        TimeTableActivity.instance!!.selectedItem = v4
        assertEquals(v4, TimeTableActivity.instance!!.selectedItem)
        TimeTableActivity.instance!!.nextId = v1
        assertEquals(v1, TimeTableActivity.instance!!.nextId)
        TimeTableActivity.instance!!.currentPadding = v2
        assertEquals(v2, TimeTableActivity.instance!!.currentPadding)
        TimeTableActivity.instance!!.widthDP = v3
        assertEquals(v3, TimeTableActivity.instance!!.widthDP)

        TimeTableActivity.instance!!.hourSizeDp
        TimeTableActivity.instance!!.lineHeightDp
        TimeTableActivity.instance!!.linepaddingLeftDP
        TimeTableActivity.instance!!.nowLineHeightDP
        TimeTableActivity.instance!!.hourToLine


    }
    @Test
    fun clic(){
        Thread.sleep(1000)
        assertEquals(1, TimeTableActivity.instance!!.displayedViews.size)
        val view = TimeTableActivity.instance!!.displayedViews.first()

        Espresso.onView(ViewMatchers.withId(view.id))
            .perform(ViewActions.click())

    }

}
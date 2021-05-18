package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When
@Suppress("UNCHECKED_CAST")
class EventManagementTest {
    lateinit var scenario: ActivityScenario<MainActivity>
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedEventDB: EventDatabaseInterface
    lateinit var mockedZoneDB: ZoneDatabaseInterface
    val listZone = mutableListOf<Zone>()
    var event: Event? = null
    val eventName = "name"
    val eventDesc = "desc"
    val eventNb = "10"
    val eventId = "test id"

    private fun setupListZone() {
        listZone.add(Zone(zoneName = "A", zoneId = "idA"))
        listZone.add(Zone(zoneName = "B", zoneId = "idB"))
        listZone.add(Zone(zoneName = "C", zoneId = "idC"))
    }

    @Before
    fun setup() {
        setupListZone()

        val startTime = LocalDateTime.now()
        val endTime = LocalDateTime.now()
        event = Event(
            zoneId = listZone[0].zoneId,
            zoneName = listZone[0].zoneName,
            eventName = eventName,
            eventId = eventId,
            description = eventDesc,
            maxNumberOfSlots = eventNb.toInt(),
            limitedEvent = true,
            startTime = startTime,
            endTime = endTime
        )
        mockedDatabase = mock(DatabaseInterface::class.java)
        mockedEventDB = mock(EventDatabaseInterface::class.java)
        mockedZoneDB = mock(ZoneDatabaseInterface::class.java)

        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)

        val obs = Observable<Boolean>()
        When(
            mockedZoneDB.getAllZones(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[2] as ObservableList<Zone>).addAll(listZone)
            obs
        }

        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )

        Database.currentDatabase = mockedDatabase

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            EventManagementListActivity.NEW_EVENT_ID
        )
        scenario = ActivityScenario.launch(intent)
        obs.postValue(true)
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    private fun closeKeyboard() {
        closeSoftKeyboard()
        onView(isRoot()).perform(closeSoftKeyboard())
    }

    private fun clickAndCheckNotRedirect() {
        closeKeyboard()
        onView(withId(R.id.btnManageEvent)).perform(scrollTo(), click())
    }

    private fun addAddListener(): Observable<Boolean> {
        val obs = Observable<Boolean>()
        When(mockedEventDB.createEvent(anyOrNull(), anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs
        }
        return obs
    }

    private fun addUpdateListener(): Observable<Boolean> {
        val obs = Observable<Boolean>()
        When(mockedEventDB.updateEvent(anyOrNull(), anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs
        }
        return obs
    }

    @Test
    fun updateOnlyWhenAllConditionsSatisfied1() {
        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.minusDays(
                1
            )
        )
        onView(withId(R.id.eventManagementNameField)).check(matches(isDisplayed()))
        clickAndCheckNotRedirect()
        onView(withId(R.id.eventManagementNameField)).perform(replaceText(eventName))
        clickAndCheckNotRedirect()
        onView(withId(R.id.eventManagementDescriptionField)).perform(replaceText(eventDesc))
        clickAndCheckNotRedirect()
        closeKeyboard()
        onView(withId(R.id.swtLimitedEvent)).perform(click())
        onView(withId(R.id.swtLimitedEvent)).perform(click())
        Intents.init()
        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.plusDays(
                1
            )
        )
        val obs = addAddListener()
        closeSoftKeyboard()
        onView(withId(R.id.btnManageEvent)).perform(scrollTo(), click())
        obs.postValue(true)
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun updateOnlyWhenAllConditionsSatisfied2() {
        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.minusDays(
                1
            )
        )
        clickAndCheckNotRedirect()
        onView(withId(R.id.eventManagementNameField)).perform(replaceText(eventName))
        clickAndCheckNotRedirect()
        onView(withId(R.id.eventManagementDescriptionField)).perform(replaceText(eventDesc))
        clickAndCheckNotRedirect()
        closeKeyboard()
        onView(withId(R.id.swtLimitedEvent)).perform(click())
        onView(withId(R.id.etNbPart)).perform(replaceText(EventManagementActivity.EMPTY_PART_NB))
        clickAndCheckNotRedirect()
        onView(withId(R.id.etNbPart)).perform(replaceText(EventManagementActivity.MIN_PART_NB.toString()))
        clickAndCheckNotRedirect()

        Intents.init()
        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.plusDays(
                1
            )
        )
        val obs = addAddListener()
        closeSoftKeyboard()
        onView(withId(R.id.btnManageEvent)).perform(scrollTo(), click())
        obs.postValue(true)
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun redirectToManagementListIfFailToGetZone() {
        mockedDatabase = mock(DatabaseInterface::class.java)
        mockedEventDB = mock(EventDatabaseInterface::class.java)
        mockedZoneDB = mock(ZoneDatabaseInterface::class.java)
        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)
        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )

        val obs = Observable<Boolean>()
        When(
            mockedZoneDB.getAllZones(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[2] as ObservableList<Zone>).addAll(listZone)
            obs
        }
        Database.currentDatabase = mockedDatabase

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            EventManagementListActivity.NEW_EVENT_ID
        )
        scenario = ActivityScenario.launch(intent)
        Intents.init()
        obs.postValue(false)
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun redirectToManagementListIfFailToGetEventInformation() {
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            obs
        }

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            "asdjsahjkdsahjda"
        )
        scenario = ActivityScenario.launch(intent)
        Intents.init()
        obs.postValue(false)
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun gettedInformationCorrectlySet() {
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[1] as Observable<Event>).postValue(event!!)
            obs
        }

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            eventId
        )
        scenario = ActivityScenario.launch(intent)
        obs.postValue(true)
        onView(withId(R.id.et_end_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        event!!.endTime.toString().replace("T", " ")
                    )
                )
            )
        )
        onView(withId(R.id.et_start_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        event!!.startTime.toString().replace("T", " ")
                    )
                )
            )
        )
        onView(withId(R.id.etNbPart)).check(matches(withText(CoreMatchers.containsString(eventNb))))
        onView(withId(R.id.eventManagementNameField)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        eventName
                    )
                )
            )
        )
        onView(withId(R.id.eventManagementDescriptionField)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        eventDesc
                    )
                )
            )
        )
        onView(withId(R.id.swtLimitedEvent)).check(matches(isChecked()))

    }

    @Test
    fun addReturnTheCorrectlySetField() {
        val startDate = EventManagementActivity.dateStart.value!!
        val endDate = EventManagementActivity.dateEnd.value!!
        onView(withId(R.id.eventManagementNameField)).perform(typeText(eventName))
        onView(withId(R.id.eventManagementDescriptionField)).perform(replaceText(eventDesc))
        onView(withId(R.id.btnManageEvent)).perform(scrollTo())
        val obs = addAddListener()
        onView(withId(R.id.btnManageEvent)).perform(scrollTo(), click())
        obs.postValue(true)

        assertEquals(event!!.endTime, endDate)
        assertEquals(event!!.startTime, startDate)
        assertEquals(event!!.eventName, eventName)
        assertEquals(event!!.description, eventDesc)
    }

    @Test
    fun failToUpdateStaysOnActivity() {
        val obs2 = addUpdateListener()
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[1] as Observable<Event>).postValue(event!!)
            obs
        }

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            eventId
        )
        scenario = ActivityScenario.launch(intent)
        obs.postValue(true)

        onView(withId(R.id.btnManageEvent)).perform(scrollTo(), click())
        obs2.postValue(false)

        onView(withId(R.id.btnManageEvent)).perform(scrollTo())
        onView(withId(R.id.btnManageEvent)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulUpdateRedirect() {
        val startTime = LocalDateTime.now()
        val endTime = LocalDateTime.now()
        event = Event(
            zoneId = listZone[0].zoneId,
            zoneName = listZone[0].zoneName,
            eventName = eventName,
            eventId = eventId,
            description = eventDesc,
            limitedEvent = false,
            startTime = startTime,
            endTime = endTime
        )
        val obs2 = addUpdateListener()
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[1] as Observable<Event>).postValue(event!!)
            obs
        }

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            eventId
        )
        scenario = ActivityScenario.launch(intent)
        obs.postValue(true)
        closeKeyboard()
        onView(withId(R.id.swtLimitedEvent)).perform(click())
        onView(withId(R.id.etNbPart)).perform(replaceText(eventNb))

        onView(withId(R.id.btnManageEvent)).perform(click())
        Intents.init()
        obs2.postValue(true)
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
        Intents.release()

        assertEquals(event!!.endTime, endTime)
        assertEquals(event!!.startTime, startTime)
        assertEquals(event!!.eventName, eventName)
        assertEquals(event!!.description, eventDesc)
        assertEquals(event!!.getMaxNumberOfSlots(), eventNb.toInt())
        assertEquals(event!!.isLimitedEvent(), true)
        assertEquals(event!!.eventId, eventId)

    }

    @Test
    fun clickOnTimeDatePickerWithoutChangingDontChangeDate() {
        val startDate = EventManagementActivity.dateStart.value!!
        val endDate = EventManagementActivity.dateEnd.value!!

        onView(withId(R.id.btnStartDate)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.btnEndDate)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.btnStartTime)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.btnEndTime)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.et_end_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        endDate.toString().replace("T", " ")
                    )
                )
            )
        )
        onView(withId(R.id.et_start_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        startDate.toString().replace("T", " ")
                    )
                )
            )
        )
    }

    @Test
    fun updatedDateChangeText() {
        val startDate = LocalDateTime.now()
        val endDate = startDate.plusDays(7)
        EventManagementActivity.dateEnd.postValue(endDate)
        EventManagementActivity.dateStart.postValue(startDate)

        onView(withId(R.id.et_end_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        endDate.toString().replace("T", " ")
                    )
                )
            )
        )
        onView(withId(R.id.et_start_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        startDate.toString().replace("T", " ")
                    )
                )
            )
        )

        val startDate2 = startDate.plusDays(2).withNano(0).withSecond(0)
        val endData2 = endDate.plusDays(2).withNano(0).withSecond(0)

        EventManagementActivity.postValueDate(
            EventManagementActivity.TYPE_END,
            endData2.year,
            endData2.monthValue,
            endData2.dayOfMonth,
            endData2.hour,
            endData2.minute
        )
        EventManagementActivity.postValueDate(
            EventManagementActivity.TYPE_START,
            startDate2.year,
            startDate2.monthValue,
            startDate2.dayOfMonth,
            startDate2.hour,
            startDate2.minute
        )
        onView(withId(R.id.et_end_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        endData2.toString().replace("T", " ")
                    )
                )
            )
        )
        onView(withId(R.id.et_start_date)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        startDate2.toString().replace("T", " ")
                    )
                )
            )
        )
    }
}
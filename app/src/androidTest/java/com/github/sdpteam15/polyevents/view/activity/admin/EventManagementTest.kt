package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.adapter.EventListAdapter
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
    lateinit var mockedUserDb: UserDatabaseInterface
    lateinit var mockedZoneDB: ZoneDatabaseInterface
    val listZone = mutableListOf<Zone>()
    val listUser = mutableListOf<UserEntity>()
    var event: Event? = null
    val eventName = "name"
    val eventDesc = "desc"
    val eventNb = "10"
    val eventId = "test id"

    private val event1 =
        Event(
            eventName = "Test 1",
            eventId = "Id1",
            zoneId = " zid1",
            zoneName = "zoneName1",
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(),
            organizer = "idA"
        )
    private val event2 =
        Event(eventName = "Test 2", eventId = "Id2", zoneId = " zid1", zoneName = "zoneName1")
    private val event3 =
        Event(eventName = "Test 3", eventId = "Id3", zoneId = " zid2", zoneName = "zoneName2")
    private val event4 =
        Event(eventName = "Test 4", eventId = "Id4", zoneId = " zid3", zoneName = "zoneName3")
    private lateinit var events: MutableList<Event>
    private lateinit var zones: MutableList<Zone>
    private val tags = "Cool, Fine"

    private fun setupEventsAndZones() {
        events = mutableListOf(event1, event2, event3, event4)
        zones = mutableListOf(
            Zone(zoneId = "zid1", zoneName = "zoneName1"),
            Zone(zoneId = "zid2", zoneName = "zoneName2"),
            Zone(zoneId = "zid3", zoneName = "zoneName3")
        )
    }

    private fun setupListZone() {
        listZone.add(Zone(zoneName = "UserA", zoneId = "idA"))
        listZone.add(Zone(zoneName = "UserB", zoneId = "idB"))
        listZone.add(Zone(zoneName = "UserC", zoneId = "idC"))
    }

    private fun setupListUser() {
        listUser.add(UserEntity(name = "A", uid = "idA"))
        listUser.add(UserEntity(name = "B", uid = "idB"))
        listUser.add(UserEntity(name = "C", uid = "idC"))
    }

    private fun setupMockDatabase() {
        mockedDatabase = mock(DatabaseInterface::class.java)
        mockedEventDB = mock(EventDatabaseInterface::class.java)
        mockedZoneDB = mock(ZoneDatabaseInterface::class.java)
        mockedUserDb = mock(UserDatabaseInterface::class.java)

        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)
        When(mockedDatabase.userDatabase).thenReturn(mockedUserDb)

        When(
            mockedZoneDB.getActiveZones(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[0] as ObservableList<Zone>).addAll(listZone)
            Observable(true)
        }

        When(
            mockedUserDb.getListAllUsers(
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[0] as ObservableList<UserEntity>).addAll(listUser)
            Observable(true)
        }

        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(
                Observable(true)
            )
        When(mockedDatabase.currentUser).thenReturn(UserEntity(uid = "UserId"))
        Database.currentDatabase = mockedDatabase
    }

    @Before
    fun setup() {
        setupListZone()
        setupListUser()
        setupEventsAndZones()

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
            endTime = endTime,
            tags = tags.trim().split(",").map { s -> s.trim() }.toMutableList()
        )

        setupMockDatabase()

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            EventManagementListActivity.NEW_EVENT_ID
        )
        scenario = ActivityScenario.launch(intent)

    }

    @After
    fun teardown() {
        Thread.sleep(1000)
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    private fun closeKeyboard() {
        closeSoftKeyboard()
        Espresso.closeSoftKeyboard()
        onView(isRoot()).perform(closeSoftKeyboard())
    }

    private fun clickAndCheckNotRedirect() {
        closeKeyboard()
        Thread.sleep(100)
        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())
    }

    private fun addAddListener(): Observable<Boolean> {
        val obs = Observable<Boolean>()
        When(mockedEventDB.createEvent(anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs
        }

        When(mockedEventDB.createEventEdit(anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs
        }
        return obs
    }

    private fun addUpdateListener(): Observable<Boolean> {
        val obs = Observable<Boolean>()
        When(mockedEventDB.updateEvent(anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs
        }

        When(mockedEventDB.updateEventEdit(anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs
        }


        return obs
    }

    @Test
    fun updateOnlyWhenAllConditionsSatisfied1() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                EventManagementListActivity::class.java
            )

        scenario = ActivityScenario.launch(intent)
        onView(withId(R.id.id_new_event_button)).perform(click())

        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.minusDays(
                1
            )
        )
        onView(withId(R.id.id_event_management_name_et)).check(matches(isDisplayed()))
        clickAndCheckNotRedirect()
        onView(withId(R.id.id_event_management_name_et)).perform(scrollTo(), replaceText(eventName))
        clickAndCheckNotRedirect()
        onView(withId(R.id.id_description_event_edittext)).perform(scrollTo(),replaceText(eventDesc))
        clickAndCheckNotRedirect()
        closeKeyboard()
        onView(withId(R.id.id_swt_limited_event)).perform(click())
        onView(withId(R.id.id_swt_limited_event)).perform(click())

        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.plusDays(
                1
            )
        )
        val obs = addAddListener()
        closeSoftKeyboard()
        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())
        obs.postValue(true)

        onView(withId(R.id.event_management_list_id)).check(matches(isDisplayed()))
    }

    @Test
    fun updateOnlyWhenAllConditionsSatisfied2() {
        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                EventManagementListActivity::class.java
            )

        scenario = ActivityScenario.launch(intent)

        onView(withId(R.id.id_new_event_button)).perform(click())

        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.minusDays(
                1
            )
        )
        clickAndCheckNotRedirect()
        onView(withId(R.id.id_event_management_name_et)).perform(scrollTo(), replaceText(eventName))
        clickAndCheckNotRedirect()
        onView(withId(R.id.id_description_event_edittext)).perform(scrollTo(), replaceText(eventDesc))
        clickAndCheckNotRedirect()
        closeKeyboard()
        onView(withId(R.id.id_swt_limited_event)).perform(click())
        onView(withId(R.id.it_et_nb_part)).perform(replaceText(EventManagementActivity.EMPTY_PART_NB))
        clickAndCheckNotRedirect()
        onView(withId(R.id.it_et_nb_part)).perform(replaceText(EventManagementActivity.MIN_PART_NB.toString()))
        clickAndCheckNotRedirect()

        EventManagementActivity.dateEnd.postValue(
            EventManagementActivity.dateStart.value!!.plusDays(
                1
            )
        )
        val obs = addAddListener()
        closeSoftKeyboard()
        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())
        obs.postValue(true)

    }

    @Test
    fun redirectToManagementListIfFailToGetZone() {
        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        mockedEventDB = mock(EventDatabaseInterface::class.java)
        mockedZoneDB = mock(ZoneDatabaseInterface::class.java)
        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)
        When(mockedDatabase.userDatabase).thenReturn(mockedUserDb)
        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(
                Observable(true)
            )
        Mockito.`when`(mockedUserDb.getUserProfilesList(anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[0] as ObservableList<UserProfile>).add(
                    UserProfile(
                        pid = "test",
                        userRole = UserRole.ADMIN
                    )
                )
                Observable(true)
            }

        val obs = Observable<Boolean>()
        When(
            mockedZoneDB.getActiveZones(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[0] as ObservableList<Zone>).addAll(listZone)
            obs
        }

        Database.currentDatabase = mockedDatabase

        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                EventManagementListActivity::class.java
            )

        ActivityScenario.launch<EventManagementListActivity>(intent)
        onView(withId(R.id.id_new_event_button)).perform(click())

        obs.postValue(false)
        Thread.sleep(10)
        onView(withId(R.id.event_management_list_id)).check(matches(isDisplayed()))
    }

    @Test
    fun redirectToManagementListIfFailToGetUsers() {
        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        mockedEventDB = mock(EventDatabaseInterface::class.java)
        mockedZoneDB = mock(ZoneDatabaseInterface::class.java)
        mockedUserDb = mock(UserDatabaseInterface::class.java)
        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDB)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)
        When(mockedDatabase.userDatabase).thenReturn(mockedUserDb)


        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(
                Observable(true)
            )


        When(
            mockedZoneDB.getActiveZones(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[0] as ObservableList<Zone>).addAll(listZone)
            Observable(true)
        }
        Mockito.`when`(mockedUserDb.getUserProfilesList(anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[0] as ObservableList<UserProfile>).add(
                    UserProfile(
                        pid = "test",
                        userRole = UserRole.ADMIN
                    )
                )
                Observable(true)
            }

        val obs = Observable<Boolean>()
        When(
            mockedUserDb.getListAllUsers(
                anyOrNull()
            )
        ).thenAnswer {
            obs
        }

        Database.currentDatabase = mockedDatabase

        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                EventManagementListActivity::class.java
            )

        ActivityScenario.launch<EventManagementListActivity>(intent)
        onView(withId(R.id.id_new_event_button)).perform(click())

        obs.postValue(false)
        Thread.sleep(10)
        onView(withId(R.id.event_management_list_id)).check(matches(isDisplayed()))
    }

    @Test
    fun redirectToManagementListIfFailToGetEventInformation() {
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            obs
        }

        Mockito.`when`(mockedEventDB.getEvents(anyOrNull(), anyOrNull(), anyOrNull()))
            .thenAnswer {
                (it.arguments[0] as ObservableList<Event>).addAll(events)
                Observable(true, this)
            }


        val intent =
            Intent(
                ApplicationProvider.getApplicationContext(),
                EventManagementListActivity::class.java
            )
        scenario = ActivityScenario.launch(intent)
        onView(withId(R.id.recycler_events_list_admin)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventListAdapter.CustomViewHolder<EventListAdapter.ZoneViewHolder>>(
                0, click()
            )
        )
        onView(withId(R.id.recycler_events_list_admin)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventListAdapter.CustomViewHolder<EventListAdapter.EventViewHolder>>(
                1, TestHelper.clickChildViewWithId(R.id.id_edit_event_button)
            )
        )


        obs.postValue(false)
        onView(withId(R.id.event_management_list_id)).check(matches(isDisplayed()))
    }


    @Test
    fun gottenInformationCorrectlySet() {
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
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
        onView(withId(R.id.it_et_nb_part)).check(matches(withText(CoreMatchers.containsString(eventNb))))
        onView(withId(R.id.id_event_management_name_et)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        eventName
                    )
                )
            )
        )
        onView(withId(R.id.id_description_event_edittext)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        eventDesc
                    )
                )
            )
        )

        onView(withId(R.id.event_management_tags_edit)).check(
            matches(
                withText(
                    CoreMatchers.containsString(
                        tags
                    )
                )
            )
        )
        onView(withId(R.id.id_swt_limited_event)).check(matches(isChecked()))

    }

    @Test
    fun addReturnTheCorrectlySetField() {
        val startDate = EventManagementActivity.dateStart.value!!
        val endDate = EventManagementActivity.dateEnd.value!!
        onView(withId(R.id.id_event_management_name_et)).perform(typeText(eventName))
        onView(withId(R.id.id_description_event_edittext)).perform(replaceText(eventDesc))
        onView(withId(R.id.id_manage_event_button)).perform(scrollTo())
        val obs = addAddListener()
        obs.postValue(true)
        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())

        assertEquals(event!!.endTime, endDate)
        assertEquals(event!!.startTime, startDate)
        assertEquals(event!!.eventName, eventName)
        assertEquals(event!!.description, eventDesc)
    }

    @Test
    fun addReturnTheCorrectlySetFieldEdit() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EventManagementActivity::class.java)
        intent.putExtra(EventManagementListActivity.INTENT_MANAGER, "edit")
        intent.putExtra(
            EventManagementListActivity.EVENT_ID_INTENT,
            EventManagementListActivity.NEW_EVENT_ID
        )
        ActivityScenario.launch<EventManagementActivity>(intent)

        val startDate = EventManagementActivity.dateStart.value!!
        val endDate = EventManagementActivity.dateEnd.value!!
        onView(withId(R.id.id_event_management_name_et)).perform(typeText(eventName))
        onView(withId(R.id.id_description_event_edittext)).perform(replaceText(eventDesc))
        val obs = addAddListener()
        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())
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

        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())
        obs2.postValue(false)

        onView(withId(R.id.id_manage_event_button)).perform(scrollTo())
        onView(withId(R.id.id_manage_event_button)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulUpdateEditManager() {
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
            mockedEventDB.getEventEditFromId(
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

        intent.putExtra(EventManagementListActivity.INTENT_MANAGER_EDIT, "edit")
        intent.putExtra(EventManagementListActivity.INTENT_MANAGER, "edit")
        scenario = ActivityScenario.launch(intent)
        obs.postValue(true)
        closeKeyboard()
        onView(withId(R.id.id_swt_limited_event)).perform(click())
        onView(withId(R.id.it_et_nb_part)).perform(replaceText(eventNb))

        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())

        obs2.postValue(true)

        assertEquals(event!!.endTime, endTime)
        assertEquals(event!!.startTime, startTime)
        assertEquals(event!!.eventName, eventName)
        assertEquals(event!!.description, eventDesc)
        assertEquals(event!!.getMaxNumberOfSlots(), eventNb.toInt())
        assertEquals(event!!.isLimitedEvent(), true)
        assertEquals(event!!.eventId, eventId)
    }

    @Test
    fun successfulUpdateEditManager2() {
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
        val obs = Observable<Boolean>()
        When(
            mockedEventDB.getEventFromId(
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
        val obs2 = Observable<Boolean>()
        When(mockedEventDB.createEventEdit(anyOrNull())).thenAnswer {
            event = (it.arguments[0] as Event)
            obs2
        }

        intent.putExtra(EventManagementListActivity.INTENT_MANAGER, "edit")

        scenario = ActivityScenario.launch(intent)
        obs.postValue(true)
        closeKeyboard()
        onView(withId(R.id.id_swt_limited_event)).perform(click())
        onView(withId(R.id.it_et_nb_part)).perform(replaceText(eventNb))

        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())

        obs2.postValue(true)

        assertEquals(event!!.endTime, endTime)
        assertEquals(event!!.startTime, startTime)
        assertEquals(event!!.eventName, eventName)
        assertEquals(event!!.description, eventDesc)
        assertEquals(event!!.getMaxNumberOfSlots(), eventNb.toInt())
        assertEquals(event!!.isLimitedEvent(), true)
        assertEquals(event!!.eventId, eventId)
    }


    @Test
    fun successfulUpdate() {
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
        onView(withId(R.id.id_swt_limited_event)).perform(click())
        onView(withId(R.id.it_et_nb_part)).perform(replaceText(eventNb))

        onView(withId(R.id.id_manage_event_button)).perform(scrollTo(), click())

        obs2.postValue(true)

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

        onView(withId(R.id.id_start_date_button)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.id_btn_end_date)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.id_start_time_button)).perform(scrollTo(), click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.id_btn_end_time)).perform(scrollTo(), click())
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
package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.admin.ZoneManagementListActivity.Companion.EXTRA_ID
import com.github.sdpteam15.polyevents.view.adapter.ZoneItemAdapter
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
@Suppress("UNCHECKED_CAST")
class ZoneManagementListTest {
    lateinit var testUser: UserEntity

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var zoneDatabase: ZoneDatabaseInterface
    lateinit var eventDatabase: EventDatabaseInterface
    lateinit var zones: MutableList<Zone>
    val obsValue = Observable<Boolean>()

    @Before
    fun setup() {
        mockedDatabase = HelperTestFunction.defaultMockDatabase()

        zoneDatabase = mock(ZoneDatabaseInterface::class.java)
        eventDatabase = mock(EventDatabaseInterface::class.java)
        When(mockedDatabase.zoneDatabase).thenReturn(zoneDatabase)
        When(mockedDatabase.eventDatabase).thenReturn(eventDatabase)

        Database.currentDatabase = mockedDatabase

        UserLogin.currentUserLogin.signOut()

        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )

        val obs = Observable(testUser)
        obs.postValue(testUser)
        testUser.userProfiles.add(UserProfile("pid", userRole = UserRole.ADMIN))

        zones = mutableListOf(
            Zone(zoneId = "zid1", zoneName = "zoneName1"),
            Zone(zoneId = "zid2", zoneName = "zoneName2"),
            Zone(zoneId = "zid3", zoneName = "zoneName3")
        )

        Mockito.`when`(zoneDatabase.getActiveZones(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as ObservableList<Zone>?)?.clear()
            (it.arguments[0] as ObservableList<Zone>?)?.addAll(zones)
            obsValue
        }
        Mockito.`when`(eventDatabase.getEvents(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            Observable(true)
        }

        When(mockedDatabase.currentUser).thenReturn(testUser)
        When(mockedDatabase.currentUserObservable).thenReturn(obs)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent)

        Intents.init()
    }

    private fun navigate() {
        Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.id_zone_management_button))
            .perform(scrollTo(), click())
    }

    @After
    fun teardown() {
        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun clickOnBtnCreateZoneLaunchCorrectActivityWithEmptyFields() {
        obsValue.postValue(true)
        navigate()
        Espresso.onView(ViewMatchers.withId(R.id.id_new_zone_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ZoneManagementActivity::class.java.name))

        Espresso.onView(ViewMatchers.withId(R.id.id_btn_manage))
            .check(ViewAssertions.matches(ViewMatchers.withText("Create zone")))
        Espresso.onView(ViewMatchers.withId(R.id.id_zone_management_description_edittext))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        Espresso.onView(ViewMatchers.withId(R.id.id_zone_management_name_edittext))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
    }

    @Test
    fun failedToLoadZoneRedirect() {
        obsValue.postValue(false)
        navigate()
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickOnZoneLaunchCorrectActivity() {
        obsValue.postValue(true)

        When(
            zoneDatabase.getZoneInformation(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            Observable(true)
        }

        navigate()
        Espresso.onView(ViewMatchers.withId(R.id.recycler_zones_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ZoneItemAdapter.ItemViewHolder>(
                0, click()
            )
        )

        Intents.intended(IntentMatchers.hasExtra(EXTRA_ID, "zid1"))
    }
}
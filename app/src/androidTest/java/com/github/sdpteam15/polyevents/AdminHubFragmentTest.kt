package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdpteam15.polyevents.admin.EventManagementActivity
import com.github.sdpteam15.polyevents.admin.ItemRequestManagementActivity
import com.github.sdpteam15.polyevents.admin.UserManagementActivity
import com.github.sdpteam15.polyevents.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import kotlin.concurrent.thread
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class AdminHubFragmentTest {
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)
    lateinit var scenario : ActivityScenario<MainActivity>


    lateinit var testUser: UserEntity

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"

    @Before
    fun setup() {
        val mockedDatabase = mock(DatabaseInterface::class.java)
        val mockedUserProfile = UserProfile("TestID","TestName")
        When(mockedDatabase.currentProfile).thenReturn(mockedUserProfile)
        When(mockedDatabase.getUpcomingEvents()).thenReturn(initEvents())
        Database.currentDatabase = mockedDatabase

        FirebaseAuth.getInstance().signOut()
        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )
        MainActivity.currentUser = testUser

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)

        Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_admin_hub))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Intents.init()
    }

    private fun initEvents(): MutableList<Event> {
        val events: MutableList<Event> = mutableListOf()
        events.add(
            Event(
                eventId = "event1",
                eventName = "Sushi demo",
                description = "Super hungry activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                organizer = "The fish band",
                zoneName = "Kitchen",
                tags = mutableSetOf("sushi", "japan", "cooking")
            )
        )
        events.add(
            Event(
                eventId = "event2",
                eventName = "Saxophone demo",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "The music band",
                zoneName = "Concert Hall"
            )
        )
        events.add(
            Event(
                eventId = "event3",
                eventName = "Aqua Poney",
                description = "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
                organizer = "The Aqua Poney team",
                zoneName = "Swimming pool"
            )
        )
        return events
    }

    @After
    fun teardown() {
        MainActivity.currentUser = null
        scenario.close()
        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun clickOnBtnItemRequestManagementDisplayCorrectActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectItemReqManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemRequestManagementActivity::class.java.name))
    }

    @Test
    fun clickOnBtnZoneManagementDisplayCorrectActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ZoneManagementActivity::class.java.name))
    }

    @Test
    fun clickOnBtnEventDisplayCorrectActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectEventManager)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
    }

    @Test
    fun clickOnBtnUserManagementDisplayCorrectActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectUserManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(UserManagementActivity::class.java.name))
    }
}
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
import com.github.sdpteam15.polyevents.admin.*
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import java.time.LocalDateTime
import kotlin.concurrent.thread
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class ZoneManagementListTest {
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
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(click())
        Intents.init()
    }

    @After
    fun teardown() {
        MainActivity.currentUser = null
        scenario.close()
        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun clickOnBtnCreateZoneLaunchCorrectActivityWithEmptyFields() {
        Espresso.onView(ViewMatchers.withId(R.id.btnNewZone)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ZoneManagementActivity::class.java.name))

        Espresso.onView(ViewMatchers.withId(R.id.btnManage))
            .check(ViewAssertions.matches(ViewMatchers.withText("Create zone")))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
    }
}
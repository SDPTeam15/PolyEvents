package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.TimeTableActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class VisitorHomeFragmentTests {

    lateinit var testUser: UserEntity
    lateinit var mockedDatabase: DatabaseInterface
    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"

    @Before
    fun setup() {
        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )
        testUser.userProfiles.add(UserProfile("testprofile", userRole = UserRole.PARTICIPANT))
        val observableUser = Observable(testUser)

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        Database.currentDatabase = mockedDatabase

        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(observableUser)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(testUser)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent)

        Intents.init()
    }

    @After
    fun teardown() {
        MainActivity.currentUser = null
        MainActivity.currentUserObservable = null

        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun correctFragmentIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_visitor))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun canLaunchPlanningActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.id_timetable_button))
            .perform(scrollTo(), click())
        Intents.intended(IntentMatchers.hasComponent(TimeTableActivity::class.java.name))
    }
}

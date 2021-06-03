package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MapFragmentTest {
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
        testUser.userProfiles.add(UserProfile("testprofile", userRole = UserRole.ADMIN))
        val observableUser = Observable(testUser)
        PolyEventsApplication.inTest = true

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        Database.currentDatabase = mockedDatabase

        MainActivity.instance = null
        MainActivity.selectedRole = UserRole.ADMIN

        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(observableUser)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(testUser)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent)

        Espresso.onView(ViewMatchers.withId(R.id.ic_map))
            .perform(click())

        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_map))
            .check(matches(isDisplayed()))
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun btnIsCorrectlyDisplayed(){
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_map))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.id_location_button))
            .check(matches(isDisplayed()))
    }
}
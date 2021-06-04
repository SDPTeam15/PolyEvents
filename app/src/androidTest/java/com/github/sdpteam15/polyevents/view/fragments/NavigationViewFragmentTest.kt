package com.github.sdpteam15.polyevents.view.fragments


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
class NavigationViewFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)
    lateinit var mockedDatabase: DatabaseInterface

    @Before
    fun setup() {
        //Initial state
        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        Database.currentDatabase = mockedDatabase
        MainActivity.selectedRole = UserRole.PARTICIPANT
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity("uid"))
        Mockito.`when`(mockedDatabase.currentUserObservable)
            .thenReturn(Observable(UserEntity("uid")))
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
        UserLogin.currentUserLogin = GoogleUserLogin
        MainActivity.selectedRole = null
    }

    @Test
    fun NavigationBarDisplaysCorrectFragmentIfNotConnected() {
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(null)
        Thread.sleep(200)

        //Initial state
        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home_visitor)).check(matches(isDisplayed()))

        Thread.sleep(200)
        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_event_list)).check(matches(isDisplayed()))

        Thread.sleep(200)
        Espresso.onView(withId(R.id.ic_login)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        Thread.sleep(200)
        Espresso.onView(withId(R.id.ic_settings)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_settings)).check(matches(isDisplayed()))
    }
}
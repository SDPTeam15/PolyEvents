package com.github.sdpteam15.polyevents.fragments


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.login.UserLoginInterface
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as When

@RunWith(MockitoJUnitRunner::class)
class NavigationViewFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        //Initial state
        Database.currentDatabase = FirestoreDatabaseProvider
        val mockedUserLogin = Mockito.mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockedUserLogin
        When(mockedUserLogin.isConnected()).thenReturn(false)

        MainActivity.currentUser = null
        Database.currentDatabase.currentUser = null
    }
    @After
    fun teardown(){
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun NavigationBarDisplaysCorrectFragment() {
        MainActivity.currentUser = null
        Database.currentDatabase.currentUser = null

        //Initial state
        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))

        //Espresso.onView(withId(R.id.ic_map)).perform(click())
        //Espresso.onView(withId(R.id.id_fragment_map)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_event_list)).check(matches(isDisplayed()))

        //Espresso.onView(withId(R.id.ic_map)).perform(click())
        //Espresso.onView(withId(R.id.id_fragment_map)).check(matches(isDisplayed()))

        if (Database.currentDatabase.currentUser == null) {
            Espresso.onView(withId(R.id.ic_login)).perform(click())
            Espresso.onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
        } else {
            Espresso.onView(withId(R.id.ic_login)).perform(click())
            Espresso.onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))
        }

        Espresso.onView(withId(R.id.ic_more)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_more)).check(matches(isDisplayed()))
/*
        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.nav_search)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))*/

        //TODO Add a check when the user rank is implemented
    }

    @Test
    fun loginFragmentDisplayedIfNoAuth() {
        UserLogin.currentUserLogin.signOut()
        Espresso.onView(withId(R.id.ic_login)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
    }
}
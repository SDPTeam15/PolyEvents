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
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.google.firebase.auth.AuthResult
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

@RunWith(MockitoJUnitRunner::class)
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
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity("uid"))
        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(Observable(UserEntity("uid")))

    }
    @After
    fun teardown(){
        Database.currentDatabase = FirestoreDatabaseProvider
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun NavigationBarDisplaysCorrectFragmentIfNotConnected() {
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(null)

        //Initial state
        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home_visitor)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_event_list)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_login)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_settings)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_settings)).check(matches(isDisplayed()))
    }

    @Test
    fun NavigationBarDisplaysCorrectFragmentIfConnected() {
        val mockedUserDatabase = mock(UserDatabaseInterface::class.java)
        When(mockedDatabase.userDatabase).thenReturn(mockedUserDatabase)
        When(mockedUserDatabase.addUserProfileAndAddToUser(anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )
        When(mockedUserDatabase.getUserProfilesList(anyOrNull(), anyOrNull())).thenReturn(Observable(true))

        //Initial state
        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home_visitor)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_event_list)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_login)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_settings)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_settings)).check(matches(isDisplayed()))
    }
}
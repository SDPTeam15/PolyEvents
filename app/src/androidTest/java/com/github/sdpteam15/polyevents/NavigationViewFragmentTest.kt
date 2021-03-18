package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationViewFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun NavigationBarDisplaysCorrectFragment() {
        //Initial state
        Espresso.onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))

        //Espresso.onView(withId(R.id.ic_map)).perform(click())
        //Espresso.onView(withId(R.id.id_fragment_map)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_list)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_list)).check(matches(isDisplayed()))

        //Espresso.onView(withId(R.id.ic_map)).perform(click())
        //Espresso.onView(withId(R.id.id_fragment_map)).check(matches(isDisplayed()))

        if (FirebaseAuth.getInstance().currentUser == null) {
            Espresso.onView(withId(R.id.ic_login)).perform(click())
            Espresso.onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
        } else {
            Espresso.onView(withId(R.id.ic_login)).perform(click())
            Espresso.onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))
        }

        Espresso.onView(withId(R.id.ic_more)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_more)).check(matches(isDisplayed()))

        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))


        Espresso.onView(withId(R.id.nav_search)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))
    }

    @Test
    fun loginFragmentDisplayedIfNoAuth() {
        FirebaseAuth.getInstance().signOut()
        Espresso.onView(withId(R.id.ic_login)).perform(click())
        Espresso.onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
    }
}
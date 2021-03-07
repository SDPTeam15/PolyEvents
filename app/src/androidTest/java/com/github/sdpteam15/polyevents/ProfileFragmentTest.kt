package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    @Rule
    @JvmField
    var testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Test
    fun signOutButtonRedirectToLoginFragment(){
        /*onView(withId(R.id.ic_profile)).perform(click())
        onView(withId(R.id.btnLogout)).perform(click())
        onView(withId(R.id.fragment_login_id)).check(matches(isDisplayed()))*/
    }
}
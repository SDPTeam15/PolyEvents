package com.github.sdpteam15.polyevents

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdpteam15.polyevents.fragments.HomeFragment
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HelperFunctionsTest {
    @Rule
    @JvmField
    var testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Test
    fun IntentFired(){
        /*Intents.init()
        HelperFunctions.startActivityAndTerminate(ApplicationProvider.getApplicationContext(),MainActivity::class.java)
        Intents.intended(IntentMatchers.toPackage("com.github.sdpteam15.polyevents"))
        Intents.release()*/
    }

    @Test
    fun fragmentSwitched(){
        /*HelperFunctions.changeFragment(ApplicationProvider.getApplicationContext(),HomeFragment())
        onView(withId(R.id.id_fragment_home)).check(matches(isDisplayed()))*/
    }
}
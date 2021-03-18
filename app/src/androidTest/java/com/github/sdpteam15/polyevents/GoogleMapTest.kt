package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GoogleMapTest {
    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    @Before
    fun clickOnMapFragment() {
        Espresso.onView(ViewMatchers.withId(R.id.ic_map)).perform(ViewActions.click())
    }

    @Test
    fun mapClickTest() {
        val uiDevice: UiDevice = UiDevice.getInstance(getInstrumentation())
        uiDevice.click(100, 600)
        Thread.sleep(4000)
    }
}
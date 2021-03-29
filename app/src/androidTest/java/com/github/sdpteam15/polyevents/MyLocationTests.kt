package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MyLocationTests {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun goToMapFragment() {
        // Go to the map fragment
        onView(withId(R.id.ic_map)).perform(click())
    }

    @Test
    fun enablingLocationChangeIcon() {
        onView(withTagValue(equalTo(R.drawable.ic_location_off)))
        onView(withId(R.id.id_location_button)).perform(click())
        onView(withTagValue(equalTo(R.drawable.ic_location_on)))
    }

    /*
    private fun givePermission() {
        permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

     */
}


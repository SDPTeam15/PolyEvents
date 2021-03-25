package com.github.sdpteam15.polyevents


import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MapsFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    lateinit var permissionRule: GrantPermissionRule

    @Before
    fun goToMapFragment() {
        // Go to the map fragment
        onView(withId(R.id.ic_map)).perform(click())
    }

    @Test
    fun enablingLocationChangeIcon() {
        givePermission()

        onView(withTagValue(equalTo(R.drawable.ic_location_off)))
        onView(withId(R.id.id_location_button)).perform(click())
        onView(withTagValue(equalTo(R.drawable.ic_location_on)))
    }


    private fun denyPermission() {

    }

    private fun givePermission() {
        permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun isPermissionGranted() {

    }
}
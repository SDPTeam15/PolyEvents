package com.github.sdpteam15.polyevents

import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MyLocationTests {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    //@get:Rule
    //var mGrantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    fun goToMapFragment() {
        // Go to the map fragment
        onView(withId(R.id.ic_map)).perform(click())
    }

    @Test
    fun enablingLocationChangeIcon() {
        // From off to on
        onView(withTagValue(equalTo(R.drawable.ic_location_off)))
        onView(withId(R.id.id_location_button)).perform(click())
        onView(withTagValue(equalTo(R.drawable.ic_location_on)))

        // From on to off
        onView(withId(R.id.id_location_button)).perform(click())
        onView(withTagValue(equalTo(R.drawable.ic_location_off)))
    }

    @Test
    fun locationButtonsAreDisplayed() {
        val bottomNavigationItemView = onView(
            Matchers.allOf(
                withId(R.id.ic_map), ViewMatchers.withContentDescription("Map"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navigation_bar),
                        0
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val imageButton = onView(
            Matchers.allOf(
                withId(R.id.id_location_button),
                ViewMatchers.withParent(ViewMatchers.withParent(withId(R.id.fl_wrapper))),
                ViewMatchers.isDisplayed()
            )
        )
        imageButton.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        val imageButton2 = onView(
            Matchers.allOf(
                withId(R.id.id_locate_me_button),
                ViewMatchers.withParent(ViewMatchers.withParent(withId(R.id.fl_wrapper))),
                ViewMatchers.isDisplayed()
            )
        )
        imageButton2.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun denyPermissionKeepsLocationOff() {
        // Revoke all the permissions
        //InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm reset-permissions")

        val bottomNavigationItemView = onView(
            Matchers.allOf(
                withId(R.id.ic_map), ViewMatchers.withContentDescription("Map"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.navigation_bar),
                        0
                    ),
                    1
                ),
                ViewMatchers.isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        denyPermissions()

        // Click on the "location" button to try to activate it.
        onView(withId(R.id.id_location_button)).perform(click())

        // Check the location is not enabled
        onView(withTagValue(equalTo(R.drawable.ic_location_off)))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    private fun denyPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            val allowPermissions = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).findObject(
                UiSelector().text("Deny")
            )
            Log.d("SDP", "Entering")
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click()
                    Log.d("SDP", "Permission denied")
                } catch (e: UiObjectNotFoundException) {
                    Log.d("SDP", "No permission dialog found.")
                }
            }
        }
    }
}


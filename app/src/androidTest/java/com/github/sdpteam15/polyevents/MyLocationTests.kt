package com.github.sdpteam15.polyevents

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
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

    // Source : https://stackoverflow.com/questions/43462172/android-revoke-permission-at-start-of-each-test
    /**
     * Need to revoke permissions AFTER each test and not before. Otherwise it restarts the app
     * and this makes the process crash.
     */

    /*
    @After
    fun tearDown(){
        //InstrumentationRegistry.getInstrumentation().uiAutomation.
        //executeShellCommand("pm revoke ${getTargetContext().packageName} android.permission.ACCESS_FINE_LOCATION")
    }

     */

    @Test
    fun denyPermissionKeepsLocationOff() {
        denyPermissions()

        // Click on the "location" button to try to activate it.
        onView(withId(R.id.id_location_button)).perform(click())

        // Check the location is not enabled
        onView(withId(R.id.id_location_button)).check(matches(withTagValue(equalTo(R.drawable.ic_location_off))))
    }

    @Test
    fun enablingLocationChangeIcon() {
        grantPermission()

        // From off to on
        onView(withId(R.id.id_location_button)).perform(click())
        onView(withId(R.id.id_location_button)).check(matches(withTagValue(equalTo(R.drawable.ic_location_on))))

        // From on to off
        onView(withId(R.id.id_location_button)).perform(click())
        onView(withId(R.id.id_location_button)).check(matches(withTagValue(equalTo(R.drawable.ic_location_off))))
    }

    @Test
    fun locationButtonsAreDisplayed() {
        grantPermission()

        val imageButton = onView(
            Matchers.allOf(
                withId(R.id.id_location_button),
                withParent(withParent(withId(R.id.fl_wrapper))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        val imageButton2 = onView(
            Matchers.allOf(
                withId(R.id.id_locate_me_button),
                withParent(withParent(withId(R.id.fl_wrapper))),
                isDisplayed()
            )
        )
        imageButton2.check(matches(isDisplayed()))
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

    /**
     * Source : https://alexzh.com/ui-testing-of-android-runtime-permissions/
     */
    private fun denyPermissions() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val denyPermission = UiDevice.getInstance(instrumentation).findObject(
            UiSelector().text(
                when (Build.VERSION.SDK_INT) {
                    in 24..28 -> "DENY"
                    else -> "Deny"
                }
            )
        )
        if (denyPermission.exists()) {
            denyPermission.click()
        }
    }

    /**
     * Source : https://alexzh.com/ui-testing-of-android-runtime-permissions/
     */
    private fun grantPermission() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        val allowPermission = UiDevice.getInstance(instrumentation).findObject(
            UiSelector().text(
                when {
                    Build.VERSION.SDK_INT == 23 -> "Allow"
                    Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                    Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                    else -> "While using the app"
                }
            )
        )
        if (allowPermission.exists()) {
            allowPermission.click()
        }
    }
}


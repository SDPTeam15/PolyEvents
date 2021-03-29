package com.github.sdpteam15.polyevents

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
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

    @get:Rule
    var mGrantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

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
}


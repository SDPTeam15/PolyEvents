package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(AndroidJUnit4::class)
class MoreFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
    }


    @Test
    fun itemRequestActivityOpensOnClick() {
        onView(withId(R.id.ic_more)).perform(click())

        onView(withId(R.id.id_request_button)).perform(click())
        intended(hasComponent(ItemRequestActivity::class.java.name))
    }

    @Test
    fun itemsAdminActivity() {
        onView(withId(R.id.ic_more)).perform(click())
        onView(withId(R.id.btn_admin_items_list)).perform(click())
        intended(hasComponent(ItemsAdminActivity::class.java.name))
    }

}
package com.github.sdpteam15.polyevents


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MoreFragmentTest {

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun itemRequestActivityOpensOnClick() {
        onView(withId(R.id.ic_more)).perform(click())

        Intents.init()
        onView(withId(R.id.id_request_button)).perform(click())
        intended(hasComponent(ItemRequestActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun itemsAdminActivity() {
        onView(withId(R.id.ic_more)).perform(click())

        Intents.init()
        onView(withId(R.id.btn_admin_items_list)).perform(click())
        intended(hasComponent(ItemsAdminActivity::class.java.name))

        Intents.release()
    }

}
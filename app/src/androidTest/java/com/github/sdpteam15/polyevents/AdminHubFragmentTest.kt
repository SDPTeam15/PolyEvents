package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.admin.EventManagementActivity
import com.github.sdpteam15.polyevents.admin.ItemRequestManagementActivity
import com.github.sdpteam15.polyevents.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.database.DatabaseUserInterface
import com.github.sdpteam15.polyevents.user.User
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AdminHubFragmentTest {
    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        FirebaseAuth.getInstance().signOut()
        val user = mock(DatabaseUserInterface::class.java)
        MainActivity.currentUser = User.Companion.invoke(user)

        Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_admin_hub))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickOnBtnEventDisplayCorrectActivity() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectEventManager)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun clickOnBtnUserManagementDisplayCorrectActivity() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectEventManager)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun clickOnBtnItemRequestManagementDisplayCorrectActivity() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectItemReqManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemRequestManagementActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun clickOnBtnZoneManagementDisplayCorrectActivity() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ZoneManagementActivity::class.java.name))
        Intents.release()
    }
}
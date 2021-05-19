package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.fakedatabase.FakeDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

@RunWith(MockitoJUnitRunner::class)
class AdminHubFragmentTest {
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    lateinit var testUser: UserEntity

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"

    @Before
    fun setup() {
        val mockedDatabase = HelperTestFunction.defaultMockDatabase()
        val mockedUserProfile = UserProfile("TestID", "TestName")
        When(mockedDatabase.currentProfile).thenReturn(mockedUserProfile)
        Database.currentDatabase = mockedDatabase

        UserLogin.currentUserLogin.signOut()
        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )
        MainActivity.currentUser = testUser
        MainActivity.currentUserObservable = Observable(testUser)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent)

        Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Intents.init()
    }

    @After
    fun teardown() {
        MainActivity.currentUser = null

        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun clickOnBtnItemRequestManagementDisplayCorrectActivity() {
        Database.currentDatabase = FakeDatabase
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectItemReqManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemRequestManagementActivity::class.java.name))
    }

    @Test
    fun clickOnBtnZoneManagementDisplayCorrectActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ZoneManagementListActivity::class.java.name))
    }

    @Test
    fun clickOnBtnEventDisplayCorrectActivity() {
        val mockedDatabase = HelperTestFunction.defaultMockDatabase()
        val mockedUserProfile = UserProfile("TestID", "TestName")
        When(mockedDatabase.currentProfile).thenReturn(mockedUserProfile)
        Database.currentDatabase = mockedDatabase
        val mockedEventDatabase = mock(EventDatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDatabase)

        Mockito.`when`(
            mockedEventDatabase.getEvents(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(
            Observable(true)
        )
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectEventManager)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
    }

    @Test
    fun clickOnBtnUserManagementDisplayCorrectActivity() {
        Database.currentDatabase = FakeDatabase
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectUserManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(UserManagementListActivity::class.java.name))
    }

    /*
    @Test
    fun itemRequestActivityOpensOnClick() {
        Espresso.onView(ViewMatchers.withId(R.id.ic_more)).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectItemReqManagement)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemRequestActivity::class.java.name))
    }

    @Test
    fun itemsAdminActivity() {
        var availableItems: MutableMap<Item, Int> = mutableMapOf()
        availableItems[Item(null, "Chocolat", "OTHER")] = 30
        availableItems[Item(null, "Kiwis", "OTHER")] = 10
        availableItems[Item(null, "230V Plugs", "PLUG")] = 30
        availableItems[Item(null, "Fridge (large)", "OTHER")] = 5
        availableItems[Item(null, "Cord rewinder (15m)", "PLUG")] = 30
        availableItems[Item(null, "Cord rewinder (50m)", "PLUG")] = 10
        availableItems[Item(null, "Cord rewinder (25m)", "PLUG")] = 20

        Database.currentDatabase = FakeDatabase

        FakeDatabaseItem.items.clear()
        for ((item, count) in availableItems) {
            Database.currentDatabase.itemDatabase!!.createItem(item, count)
        }

        Espresso.onView(ViewMatchers.withId(R.id.ic_more)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.btn_admin_items_list)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemsAdminActivity::class.java.name))
    }
    */
}
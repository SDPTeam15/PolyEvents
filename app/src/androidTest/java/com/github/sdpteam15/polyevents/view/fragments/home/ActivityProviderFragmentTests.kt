package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.activityprovider.ItemRequestActivity
import com.github.sdpteam15.polyevents.view.activity.activityprovider.MyEventEditsActivity
import com.github.sdpteam15.polyevents.view.activity.activityprovider.MyItemRequestsActivity
import com.github.sdpteam15.polyevents.view.activity.admin.EventManagementListActivity
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

class ActivityProviderFragmentTests {
    lateinit var mockedDatabase: DatabaseInterface
    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"

    @Before
    fun setup() {
        val testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )
        testUser.userProfiles.add(UserProfile("testprofile", userRole = UserRole.ORGANIZER))
        val observableUser = Observable(testUser)

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        Database.currentDatabase = mockedDatabase

        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(observableUser)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(testUser)

        MainActivity.instance = null
        MainActivity.selectedRole = UserRole.ORGANIZER

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent)

        Intents.init()
    }

    @After
    fun teardown() {
        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun correctFragmentIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_provider))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun canLaunchItemRequestActvityActivity() {
        val mockedItemDatabase = mock(ItemDatabaseInterface::class.java)
        When(mockedDatabase.itemDatabase).thenReturn(
            mockedItemDatabase
        )
        When(mockedItemDatabase.getAvailableItems(anyOrNull())).thenReturn(Observable(true))

        Espresso.onView(ViewMatchers.withId(R.id.id_request_button))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        Intents.intended(IntentMatchers.hasComponent(ItemRequestActivity::class.java.name))
    }

    @Test
    fun canLaunchEditRequestsActivity() {
        val mockedEventDatabase = mock(EventDatabaseInterface::class.java)
        When(mockedDatabase.eventDatabase).thenReturn(
            mockedEventDatabase
        )

        When(
            mockedEventDatabase.getEventEdits(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Observable(true))
        When(mockedEventDatabase.getEvents(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )

        Espresso.onView(ViewMatchers.withId(R.id.id_provider_edit_requests_button))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MyEventEditsActivity::class.java.name))
    }

    @Test
    fun canLaunchMyItemRequestActivity() {
        val mockedMaterialRequest = mock(MaterialRequestDatabaseInterface::class.java)
        val mockedItemDb = mock(ItemDatabaseInterface::class.java)
        When(mockedDatabase.materialRequestDatabase).thenReturn(
            mockedMaterialRequest
        )
        When(mockedDatabase.itemDatabase).thenReturn(
            mockedItemDb
        )
        When(
            mockedMaterialRequest.getMaterialRequestListByUser(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Observable(true))
        When(
            mockedItemDb.getItemsList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Observable(true))

        Espresso.onView(ViewMatchers.withId(R.id.id_my_items_request_button))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MyItemRequestsActivity::class.java.name))
    }

    @Test
    fun canLaunchEventManagementActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.id_event_manager_button))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
    }
}
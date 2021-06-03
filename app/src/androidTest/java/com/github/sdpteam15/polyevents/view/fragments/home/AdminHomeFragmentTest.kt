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
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
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
    lateinit var testUser: UserEntity
    lateinit var mockedDatabase: DatabaseInterface
    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"

    @Before
    fun setup() {
        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )
        testUser.userProfiles.add(UserProfile("testprofile", userRole = UserRole.ADMIN))
        val observableUser = Observable(testUser)

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        Database.currentDatabase = mockedDatabase

        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(observableUser)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(testUser)

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
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_home_admin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickOnBtnItemRequestManagementDisplayCorrectActivity() {
        val mockedMaterialRequest = mock(MaterialRequestDatabaseInterface::class.java)
        val mockedItemDb = mock(ItemDatabaseInterface::class.java)

        When(mockedDatabase.materialRequestDatabase).thenReturn(
            mockedMaterialRequest
        )
        When(mockedMaterialRequest.getMaterialRequestList(anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )
        When(mockedDatabase.itemDatabase).thenReturn(
            mockedItemDb
        )
        When(
            mockedItemDb.getItemsList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Observable(true))

        Espresso.onView(ViewMatchers.withId(R.id.id_item_request_management_button))
            .perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemRequestManagementActivity::class.java.name))
    }

    @Test
    fun clickOnBtnItemListManagementDisplayCorrectActivity() {
        val mockedItemDb = mock(ItemDatabaseInterface::class.java)

        When(mockedDatabase.itemDatabase).thenReturn(
            mockedItemDb
        )
        When(
            mockedItemDb.getItemsList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Observable(true))
        When(
            mockedItemDb.getItemTypes(
                anyOrNull()
            )
        ).thenReturn(Observable(true))

        Espresso.onView(ViewMatchers.withId(R.id.id_items_list_management_button))
            .perform(click())
        Intents.intended(IntentMatchers.hasComponent(ItemsAdminActivity::class.java.name))
    }

    @Test
    fun clickOnBtnZoneManagementDisplayCorrectActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.id_zone_management_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ZoneManagementListActivity::class.java.name))
    }

    @Test
    fun clickOnBtnEventDisplayCorrectActivity() {
        val mockedEventDatabase = mock(EventDatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDatabase)

        Mockito.`when`(
            mockedEventDatabase.getEvents(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(
            Observable(true)
        )
        Espresso.onView(ViewMatchers.withId(R.id.id_event_manager_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementListActivity::class.java.name))
    }

    @Test
    fun clickOnBtnEventEditDisplayCorrectActivity() {
        val mockedEventDatabase = mock(EventDatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.eventDatabase).thenReturn(mockedEventDatabase)

        Mockito.`when`(
            mockedEventDatabase.getEvents(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(
            Observable(true)
        )

        Mockito.`when`(
            mockedEventDatabase.getEventEdits(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(
            Observable(true)
        )
        Espresso.onView(ViewMatchers.withId(R.id.id_event_edit_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(EventEditManagementActivity::class.java.name))
    }

    @Test
    fun clickOnBtnUserManagementDisplayCorrectActivity() {
        val mockedUserDatabase = mock(UserDatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.userDatabase).thenReturn(mockedUserDatabase)
        When(mockedUserDatabase.getListAllUsers(anyOrNull())).thenReturn(Observable(true))

        Espresso.onView(ViewMatchers.withId(R.id.id_user_management_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(UserManagementListActivity::class.java.name))
    }


}
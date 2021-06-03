package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.staff.StaffRequestsActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

class StaffHomeFragmentTests {
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

        testUser.userProfiles.add(UserProfile("testprofile", userRole = UserRole.STAFF))
        val observableUser = Observable(testUser)

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        Database.currentDatabase = mockedDatabase

        MainActivity.instance = null
        MainActivity.selectedRole = UserRole.STAFF

        When(mockedDatabase.currentUserObservable).thenReturn(observableUser)
        When(mockedDatabase.currentUser).thenReturn(testUser)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
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
        onView(withId(R.id.id_fragment_home_staff)).check(matches(isDisplayed()))
    }

    @Test
    fun canLaunchDeliveriesActivity() {
        val mockedDatabaseRequestDb = mock(MaterialRequestDatabaseInterface::class.java)
        val mockedDatabaseItem = mock(ItemDatabaseInterface::class.java)
        val mockedUserDatabase = mock(UserDatabaseInterface::class.java)

        When(mockedDatabase.materialRequestDatabase).thenReturn(mockedDatabaseRequestDb)
        When(mockedDatabase.itemDatabase).thenReturn(mockedDatabaseItem)
        When(mockedDatabase.userDatabase).thenReturn(mockedUserDatabase)

        When(mockedDatabaseRequestDb.getMaterialRequestList(anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )
        When(mockedDatabaseItem.getItemsList(anyOrNull(), anyOrNull(), anyOrNull())).thenReturn(
            Observable(true)
        )
        When(mockedUserDatabase.getListAllUsers(anyOrNull())).thenReturn(Observable(true))

        onView(withId(R.id.id_deliveries_button)).perform(scrollTo(), click())
        Intents.intended(IntentMatchers.hasComponent(StaffRequestsActivity::class.java.name))
    }
}
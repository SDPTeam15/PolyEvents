package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MapFragmentTest {
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
        PolyEventsApplication.inTest = true

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        currentDatabase = mockedDatabase

        MainActivity.instance = null
        MainActivity.selectedRole = UserRole.ADMIN

        Mockito.`when`(mockedDatabase.currentUserObservable).thenReturn(observableUser)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(testUser)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent)

        Espresso.onView(ViewMatchers.withId(R.id.ic_map))
            .perform(click())

        Thread.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_map))
            .check(matches(isDisplayed()))
    }

    @After
    fun teardown() {
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun btnIsCorrectlyDisplayedVisitorMap() {
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_map))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.id_heatmap))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.id_location_button))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.id_locate_me_button))
            .check(matches(isDisplayed()))
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_delete_areas)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.saveNewRoute)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.removeRoute)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.addNewRoute)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.saveAreas)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_edit_area)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.addNewArea)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.acceptNewArea)
    }

    @Test
    fun btnIsCorrectlyDisplayedRouteMap() {
        Espresso.onView(ViewMatchers.withId(R.id.ic_home))
            .perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.id_route_manager_button))
            .perform(scrollTo(), click())
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_delete_areas)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.saveNewRoute)
        BaristaVisibilityAssertions.assertDisplayed(R.id.removeRoute)
        BaristaVisibilityAssertions.assertDisplayed(R.id.addNewRoute)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.saveAreas)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_edit_area)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.addNewArea)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.acceptNewArea)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_location_button)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_heatmap)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_locate_me_button)
    }

    @Test
    fun btnIsCorrectlyDisplayedZone() {
        Espresso.onView(ViewMatchers.withId(R.id.ic_home))
            .perform(click())
        val mockedDb = HelperTestFunction.defaultMockDatabase()
        currentDatabase = mockedDb

        Espresso.onView(ViewMatchers.withId(R.id.id_zone_management_button))
            .perform(scrollTo(), click())
        Espresso.onView(ViewMatchers.withId(R.id.id_new_zone_button))
            .perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.id_btn_modify_coordinates))
            .perform(click())

        BaristaVisibilityAssertions.assertDisplayed(R.id.id_delete_areas)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.saveNewRoute)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.removeRoute)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.addNewRoute)
        BaristaVisibilityAssertions.assertDisplayed(R.id.saveAreas)
        BaristaVisibilityAssertions.assertDisplayed(R.id.id_edit_area)
        BaristaVisibilityAssertions.assertDisplayed(R.id.addNewArea)
        BaristaVisibilityAssertions.assertDisplayed(R.id.acceptNewArea)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_location_button)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_heatmap)
        BaristaVisibilityAssertions.assertNotDisplayed(R.id.id_locate_me_button)

        currentDatabase = FirestoreDatabaseProvider
    }
}
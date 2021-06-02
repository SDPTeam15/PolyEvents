package com.github.sdpteam15.polyevents.view.fragments

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserSettingsDatabaseInterface
import com.github.sdpteam15.polyevents.model.room.UserSettings
import com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertUnchecked
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class SettingsFragmentTest {

    private lateinit var mockedDatabase: DatabaseInterface
    private lateinit var mockedUserSettingsDatabase: UserSettingsDatabaseInterface
    private lateinit var localTestDatabase: LocalDatabase

    private lateinit var testUserSettings: UserSettings

    private lateinit var scenario: FragmentScenario<SettingsFragment>

    @Before
    fun setup() {
        testUserSettings =
            UserSettings(trackLocation = true, isSendingLocationOn = false, locationId = "here")

        mockedDatabase = mock(DatabaseInterface::class.java)
        mockedUserSettingsDatabase = mock(UserSettingsDatabaseInterface::class.java)
        currentDatabase = mockedDatabase

        // Create local db
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localTestDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()

        scenario = launchFragmentInContainer(
            themeResId = R.style.Theme_PolyEvents
        )

        SettingsFragment.localDatabase = localTestDatabase
        scenario.recreate()
    }

    @After
    fun teardown() {
        localTestDatabase.close()
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun testInitial() {
        assertDisplayed(R.id.fragment_settings_is_sending_location_switch)
        assertUnchecked(R.id.fragment_settings_is_sending_location_switch)

        assertDisplayed(R.id.fragment_settings_track_location_switch)
        assertUnchecked(R.id.fragment_settings_track_location_switch)
    }

    /*@Test
    fun testChangeSettingsFragment() = runBlocking {
        scenario.recreate()
        assertDisplayed(R.id.fragment_settings_button_save)
        assertEnabled(R.id.fragment_settings_button_save)

        assertDisplayed(R.id.fragment_settings_is_sending_location_switch)
        assertUnchecked(R.id.fragment_settings_is_sending_location_switch)

        assertDisplayed(R.id.fragment_settings_track_location_switch)
        assertUnchecked(R.id.fragment_settings_track_location_switch)

        // Set track location on
        clickOn(R.id.fragment_settings_track_location_switch)
        clickOn(R.id.fragment_settings_button_save)

        val retrievedUserSettings = localTestDatabase.userSettingsDao().get()
        assert(retrievedUserSettings.isNotEmpty())
        assertEquals(retrievedUserSettings[0].trackLocation, true)
    }

    @Test
    fun testSettingsFragmentCorrectlyRetrievesUserSettingsInCache() = runBlocking {
        localTestDatabase.userSettingsDao()
            .insert(UserSettings(isSendingLocationOn = true, trackLocation = true))

        scenario.recreate()

        assertDisplayed(R.id.fragment_settings_button_save)
        assertEnabled(R.id.fragment_settings_button_save)

        assertDisplayed(R.id.fragment_settings_is_sending_location_switch)
        assertChecked(R.id.fragment_settings_is_sending_location_switch)

        assertDisplayed(R.id.fragment_settings_track_location_switch)
        assertChecked(R.id.fragment_settings_track_location_switch)
    }*/
}
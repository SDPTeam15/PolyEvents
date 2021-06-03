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
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModel
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModelFactory
import com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertChecked
import com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertUnchecked
import com.schibsted.spain.barista.assertion.BaristaEnabledAssertions.assertEnabled
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class SettingsFragmentTest {

    private lateinit var mockedDatabase: DatabaseInterface
    private lateinit var mockedUserSettingsDatabase: UserSettingsDatabaseInterface
    private lateinit var localTestDatabase: LocalDatabase

    private lateinit var testUserSettings: UserSettings

    private lateinit var scenario: FragmentScenario<SettingsFragment>

    @Before
    fun setup() {
        // default user settings (every setting set to false)
        testUserSettings = UserSettings()

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

        SettingsFragment.localSettingsViewModel = UserSettingsViewModelFactory(
            localTestDatabase.userSettingsDao()
        ).create(
            UserSettingsViewModel::class.java
        )
    }

    @After
    fun teardown() {
        localTestDatabase.close()
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun testResetSettingsToDefault() = runBlocking {
        assertDisplayed(R.id.fragment_settings_button_reset_settings)
        assertEnabled(R.id.fragment_settings_button_reset_settings)

        clickOn(R.id.fragment_settings_button_reset_settings)

        assertUnchecked(R.id.fragment_settings_track_location_switch)
        assertUnchecked(R.id.fragment_settings_is_sending_location_switch)

        val retrieved = localTestDatabase.userSettingsDao().get()
        assert(retrieved.isNotEmpty())
        assertEquals(retrieved[0], testUserSettings)
    }

    @Test
    fun testChangeSettingsFragment() = runBlocking {
        assertDisplayed(R.id.fragment_settings_button_save)
        assertEnabled(R.id.fragment_settings_button_save)

        // reset user settings to default first
        clickOn(R.id.fragment_settings_button_reset_settings)
        assertDisplayed(R.id.fragment_settings_is_sending_location_switch)
        assertUnchecked(R.id.fragment_settings_is_sending_location_switch)

        assertDisplayed(R.id.fragment_settings_track_location_switch)
        assertUnchecked(R.id.fragment_settings_track_location_switch)

        // Set track location on
        clickOn(R.id.fragment_settings_track_location_switch)
        clickOn(R.id.fragment_settings_button_save)

        assertChecked(R.id.fragment_settings_track_location_switch)

        // Check if correctly updated in local database
        val retrievedUserSettings = localTestDatabase.userSettingsDao().get()
        assert(retrievedUserSettings.isNotEmpty())
        assertEquals(retrievedUserSettings[0].trackLocation, true)
    }
}
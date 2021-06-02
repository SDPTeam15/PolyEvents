package com.github.sdpteam15.polyevents.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.UserSettings
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModel
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial


/**
 * User settings fragment.
 * IMPORTANT: This class and its relevant methods should be updated everytime
 * a new setting is added
 */
class SettingsFragment : Fragment() {

    companion object {
        const val TAG = "SettingsFragment"

        // for testing purposes
        lateinit var localDatabase: LocalDatabase
        lateinit var localSettingsViewModel: UserSettingsViewModel
    }

    private lateinit var sendLocationSwitchButton: SwitchMaterial
    private lateinit var trackLocationSwitchButton: SwitchMaterial

    private lateinit var userSettingsSaveButton: Button
    private lateinit var userSettingsResetToDefaults: Button

    private lateinit var userSettings: UserSettings

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_settings, container, false)

        localDatabase = (requireActivity().application as PolyEventsApplication).database
        localSettingsViewModel = UserSettingsViewModelFactory(
            localDatabase.userSettingsDao()
        ).create(UserSettingsViewModel::class.java)

        sendLocationSwitchButton =
            fragmentView.findViewById(R.id.fragment_settings_is_sending_location_switch)
        trackLocationSwitchButton =
            fragmentView.findViewById(R.id.fragment_settings_track_location_switch)

        userSettingsSaveButton = fragmentView.findViewById(R.id.fragment_settings_button_save)
        userSettingsSaveButton.setOnClickListener {
            saveUserSettings()
            showToast(getString(R.string.settings_saved), context)
        }

        userSettingsResetToDefaults =
            fragmentView.findViewById(R.id.fragment_settings_button_reset_settings)
        userSettingsResetToDefaults.setOnClickListener {
            resetToDefault()
            saveUserSettings()
            showToast(getString(R.string.settings_reset_to_default), context)
        }

        Log.d(TAG, "ON CREATE VIEW")
        fetchUserSettings()

        return fragmentView
    }

    /**
     * Method to reset the user settings to defaults.
     */
    private fun resetToDefault() {
        trackLocationSwitchButton.isChecked = false
        sendLocationSwitchButton.isChecked = false
    }

    /**
     * Save user settings in local cache, and if user logged in, update his settings in the
     * remote database as well
     */
    private fun saveUserSettings() {
        userSettings.isSendingLocationOn = sendLocationSwitchButton.isChecked
        userSettings.trackLocation = trackLocationSwitchButton.isChecked
        localSettingsViewModel.updateUserSettings(userSettings)

        if (currentDatabase.currentUser != null) {
            // if user logged in save his settings in remote database
            currentDatabase.userSettingsDatabase?.updateUserSettings(
                userSettings.copy(
                    userUid = currentDatabase.currentUser!!.uid,
                    // We don't want to share the location of the user in the remote database
                    locationId = null
                )
            )
        }
    }

    /**
     * Fetch user settings from the local cache. If none create a new instance of UserSettings in local
     * cache.
     */
    private fun fetchUserSettings() {
        Log.d(TAG, "FETCHING USER SETTINGS")
        val userSettingsObservable = ObservableList<UserSettings>()
        userSettingsObservable.observe(this, update = false) {
            Log.d(TAG, "OBSERVED SOMETHING")
            if (it.value.isEmpty()) {
                // No user settings yet in the local cache, update with fresh new settings
                userSettings = UserSettings()
                localSettingsViewModel.updateUserSettings(userSettings)
            } else {
                userSettings = it.value[0]
            }
            updateSettingsView(userSettings)
        }
        Log.d(TAG, "FETCHING USER SETTINGS FROM VIEW MODEL")
        localSettingsViewModel.getUserSettings(userSettingsObservable)
    }

    /**
     * Update views to reflect upon the retrieved user settings
     * @param userSettings the user settings to update the views with
     */
    private fun updateSettingsView(userSettings: UserSettings) {
        Log.d(TAG, "UPDATING SETTINGS VIEW")
        trackLocationSwitchButton.isChecked = userSettings.trackLocation
        sendLocationSwitchButton.isChecked = userSettings.isSendingLocationOn
        userSettingsSaveButton.isEnabled = true
        userSettingsResetToDefaults.isEnabled = true
    }
}
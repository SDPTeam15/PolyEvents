package com.github.sdpteam15.polyevents.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
 * A simple [Fragment] subclass
 */
class SettingsFragment : Fragment() {

    companion object {
        const val TAG = "SettingsFragment"

        // for testing purposes
        lateinit var localDatabase: LocalDatabase
    }

    private lateinit var sendLocationSwitchButton: SwitchMaterial
    private lateinit var trackLocationSwitchButton: SwitchMaterial

    private lateinit var userSettingsSaveButton: Button

    private lateinit var userSettings: UserSettings

    // Lazily initialized view model, instantiated only when accessed for the first time
    private val localSettingsViewModel: UserSettingsViewModel by viewModels {
        UserSettingsViewModelFactory(localDatabase.userSettingsDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_settings, container, false)

        localDatabase = (requireActivity().application as PolyEventsApplication).database

        sendLocationSwitchButton =
            fragmentView.findViewById(R.id.fragment_settings_is_sending_location_switch)
        trackLocationSwitchButton =
            fragmentView.findViewById(R.id.fragment_settings_track_location_switch)

        userSettingsSaveButton = fragmentView.findViewById(R.id.fragment_settings_button_save)
        userSettingsSaveButton.setOnClickListener {
            saveUserSettings()
        }

        fetchUserSettings()

        return fragmentView
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
                userSettings.copy(userUid = currentDatabase.currentUser!!.uid)
            )
        }
        showToast("Your settings have been saved", context)
    }

    /**
     * Fetch user settings from the local cache. If none create a new instance of UserSettings in local
     * cache.
     */
    private fun fetchUserSettings() {
        val userSettingsObservable = ObservableList<UserSettings>()
        userSettingsObservable.observe(this, update = false) {
            if (it.value.isEmpty()) {
                // No user settings yet in the local cache, update with fresh new settings
                userSettings = UserSettings()
                localSettingsViewModel.updateUserSettings(userSettings)
            } else {
                userSettings = it.value[0]
            }
            updateSettingsView(userSettings)
        }
        localSettingsViewModel.getUserSettings(userSettingsObservable)
    }

    /**
     * Update views to reflect upon the retrieved user settings
     * @param userSettings the user settings to update the views with
     */
    private fun updateSettingsView(userSettings: UserSettings) {
        trackLocationSwitchButton.isChecked = userSettings.trackLocation
        sendLocationSwitchButton.isChecked = userSettings.isSendingLocationOn
        userSettingsSaveButton.isEnabled = true
    }
}
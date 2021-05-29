package com.github.sdpteam15.polyevents.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial


/**
 * A simple [Fragment] subclass
 */
class SettingsFragment : Fragment() {

    companion object {
        lateinit var localDatabase: LocalDatabase
    }

    private lateinit var sendLocationSwitchButton: SwitchMaterial
    private lateinit var trackLocationSwitchButton: SwitchMaterial

    private lateinit var userSettingsSaveButton: Button

    // Lazily initialized view model, instantiated only when accessed for the first time
    private val localSettingsViewModel: UserSettingsViewModel by viewModels {
        UserSettingsViewModel.UserSettingsViewModelFactory(localDatabase.userSettingsDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_settings, container, false)

        localDatabase = (requireActivity().application as PolyEventsApplication).database

        sendLocationSwitchButton = fragmentView.findViewById(R.id.fragment_settings_is_sending_location_switch)
        trackLocationSwitchButton = fragmentView.findViewById(R.id.fragment_settings_track_location_switch)

        userSettingsSaveButton = fragmentView.findViewById(R.id.fragment_settings_button_save)
        userSettingsSaveButton.setOnClickListener {
            
        }
        return fragmentView
    }
}
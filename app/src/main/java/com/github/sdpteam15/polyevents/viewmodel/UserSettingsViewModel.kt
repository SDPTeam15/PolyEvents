package com.github.sdpteam15.polyevents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdpteam15.polyevents.model.database.local.dao.UserSettingsDao
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import kotlinx.coroutines.launch

/**
 * A view model for user settings. All its operations are run on coroutines launched
 * on the viewModelScope
 */
class UserSettingsViewModel(private val userSettingsDao: UserSettingsDao) : ViewModel() {

    /**
     * Get the user settings stored in local cache
     * @param obs the observable to be updated with the user settings once they're fetched from
     * the local database
     */
    fun getUserSettings(obs: ObservableList<UserSettings>) = viewModelScope.launch {
        obs.updateAll(userSettingsDao.get())
    }

    /**
     * Update the user settings in the local cache
     * @param userSettings the new settings to store in the local cache
     */
    fun updateUserSettings(userSettings: UserSettings) = viewModelScope.launch {
        userSettingsDao.insert(userSettings)
    }

    companion object {
        const val TAG = "UserSettingsViewModel"
    }
}

/**
 * A ViewModelProvider.Factory that gets as a parameter the dependencies needed to create
 * an UserSettingsViewModel.
 */
class UserSettingsViewModelFactory(private val userSettingsDao: UserSettingsDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserSettingsViewModel(userSettingsDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
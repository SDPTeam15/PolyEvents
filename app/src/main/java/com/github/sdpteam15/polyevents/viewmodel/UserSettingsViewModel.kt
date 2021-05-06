package com.github.sdpteam15.polyevents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.sdpteam15.polyevents.model.database.local.dao.UserSettingsDao
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.room.UserSettings
import kotlinx.coroutines.launch

/**
 * A view model for user settings. All its operations are run on coroutines launched
 * on the viewModelScope
 */
class UserSettingsViewModel(private val userSettingsDao: UserSettingsDao): ViewModel() {

    fun getUserSettings(obs: Observable<UserSettings>) = viewModelScope.launch {
        obs.postValue(userSettingsDao.get())
    }

    fun updateUserSettings(userSettings: UserSettings) = viewModelScope.launch {
        userSettingsDao.insert(userSettings)
    }

    companion object {
        const val TAG = "UserSettingsViewModel"
    }
}
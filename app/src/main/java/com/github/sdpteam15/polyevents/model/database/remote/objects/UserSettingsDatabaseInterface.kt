package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings

/**
 * Database interface for getting the current user preferences
 */
interface UserSettingsDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * Update or request an update for the user settings
     * @param userSettings the user settings to update
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateUserSettings(
        userSettings: UserSettings,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get the preferences and settings of the current user, if any.
     * @param id the id of the user, typically the current user connected
     * @param userSettingsObservable the observable of the user settings
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserSettings(
        id: String? = currentUser?.uid,
        userSettingsObservable: Observable<UserSettings>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>
}
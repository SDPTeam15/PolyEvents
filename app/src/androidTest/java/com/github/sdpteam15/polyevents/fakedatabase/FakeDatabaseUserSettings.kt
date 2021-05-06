package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.UserSettingsDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.room.UserSettings

object FakeDatabaseUserSettings: UserSettingsDatabaseInterface {
    override fun updateUserSettings(userSettings: UserSettings, userAccess: UserProfile?): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserSettings(id: String?, userSettingsObservable: Observable<UserSettings>, userAccess: UserProfile?): Observable<Boolean> {
        TODO("Not yet implemented")
    }
}
package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserSettingsDatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable

object FakeDatabaseUserSettings : UserSettingsDatabaseInterface {
    val settings = mutableMapOf<String, UserSettings>()
    override fun updateUserSettings(userSettings: UserSettings): Observable<Boolean> {
        settings[userSettings.userUid] = userSettings
        return Observable(true, FakeDatabase)
    }

    override fun getUserSettings(
        id: String?,
        userSettingsObservable: Observable<UserSettings>
    ): Observable<Boolean> {
        userSettingsObservable.postValue(settings[id])
        return Observable(true)
    }
}
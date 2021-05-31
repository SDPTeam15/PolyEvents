package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserSettingsAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserSettingsDatabase
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class UserSettingsDatabaseFirestoreTest {
    lateinit var mockUserSettingsDatabase: UserSettingsDatabase
    lateinit var mockRemoteDatabase: DatabaseInterface
    val mockUserId = "mockId"

    @Before
    fun setup() {
        mockRemoteDatabase = HelperTestFunction.mockDatabaseInterface()
        mockUserSettingsDatabase = UserSettingsDatabase(mockRemoteDatabase)

        When(mockRemoteDatabase.currentUser).thenReturn(
            UserEntity(
                uid = mockUserId
            )
        )

        HelperTestFunction.clearQueue()
    }

    @Test
    fun testUpdatingUserSettings() {
        val userSettings = UserSettings()
        HelperTestFunction.nextSetEntity { true }
        mockUserSettingsDatabase.updateUserSettings(userSettings)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val setUserSettings = HelperTestFunction.lastSetEntity()!!

        assertEquals(userSettings, setUserSettings.element)
        assertEquals(
            DatabaseConstant.CollectionConstant.USER_SETTINGS_COLLECTION,
            setUserSettings.collection
        )
        assertEquals(UserSettingsAdapter, setUserSettings.adapter)
    }

    @Test
    fun testGettingUserSettings() {
        val userSettingsObservable = Observable<UserSettings>()

        HelperTestFunction.nextGetListEntity { true }
        mockUserSettingsDatabase.getUserSettings(
            id = mockUserId,
            userSettingsObservable = userSettingsObservable
        ).observeOnce {
            assert(it.value)
        }.then.postValue(false)

        val retrievedUserSettings = HelperTestFunction.lastGetEntity()!!

        assertEquals(userSettingsObservable, retrievedUserSettings.element)
        assertEquals(mockUserId, retrievedUserSettings.id)
        assertEquals(
            DatabaseConstant.CollectionConstant.USER_SETTINGS_COLLECTION,
            retrievedUserSettings.collection
        )
        assertEquals(UserSettingsAdapter, retrievedUserSettings.adapter)
    }
}
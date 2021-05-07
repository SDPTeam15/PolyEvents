package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserSettingsAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserSettingsDatabase
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.room.UserSettings
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When

@Suppress("UNCHECKED_CAST")
class UserSettingsDatabaseFirestoreTest {
    lateinit var mockUserSettingsDatabase: UserSettingsDatabase
    lateinit var mockRemoteDatabase: DatabaseInterface
    val mockUserId = "mockId"

    @Before
    fun setup() {
        mockRemoteDatabase = HelperTestFunction.mockFor()
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
        HelperTestFunction.nextBoolean(true)
        mockUserSettingsDatabase.updateUserSettings(userSettings, userAccess = UserProfile())
                .observeOnce { assert(it.value) }.then.postValue(false)

        val setUserSettings = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(userSettings, setUserSettings.element)
        assertEquals(DatabaseConstant.CollectionConstant.USER_SETTINGS_COLLECTION, setUserSettings.collection)
        assertEquals(UserSettingsAdapter, setUserSettings.adapter)
    }

    @Test
    fun testGettingUserSettings() {
        val userSettingsObservable = Observable<UserSettings>()
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockUserSettingsDatabase.getUserSettings(
                id = mockUserId,
                userSettingsObservable = userSettingsObservable,
                userAccess = userAccess
        ).observeOnce {
            assert(it.value)
        }.then.postValue(false)

        val retrievedUserSettings = HelperTestFunction.getEntityQueue.poll()!!

        assertEquals(userSettingsObservable, retrievedUserSettings.element)
        assertEquals(mockUserId, retrievedUserSettings.id)
        assertEquals(
                DatabaseConstant.CollectionConstant.USER_SETTINGS_COLLECTION,
                retrievedUserSettings.collection
        )
        assertEquals(UserSettingsAdapter, retrievedUserSettings.adapter)
    }
}
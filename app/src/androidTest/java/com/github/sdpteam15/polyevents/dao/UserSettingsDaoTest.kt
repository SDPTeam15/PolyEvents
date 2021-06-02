package com.github.sdpteam15.polyevents.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.model.database.local.dao.UserSettingsDao
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals

class UserSettingsDaoTest {
    private lateinit var userSettingsDao: UserSettingsDao
    private lateinit var localDatabase: LocalDatabase

    private lateinit var userSettings: UserSettings

    @Before
    fun createDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        userSettingsDao = localDatabase.userSettingsDao()

        userSettings = UserSettings()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        localDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertUserSettings() = runBlocking {
        userSettingsDao.insert(userSettings)
        val retrievedUserSettings = userSettingsDao.get()
        assertEquals(retrievedUserSettings, userSettings)
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateUserSettings() = runBlocking {
        userSettingsDao.insert(userSettings)

        val newUserSettings = userSettings.copy(trackLocation = true)
        userSettingsDao.insert(newUserSettings)
        val retrievedUserSettings = userSettingsDao.get()
        assertEquals(retrievedUserSettings, newUserSettings)
    }
}
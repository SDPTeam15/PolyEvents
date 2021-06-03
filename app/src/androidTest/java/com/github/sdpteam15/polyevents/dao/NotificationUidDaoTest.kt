package com.github.sdpteam15.polyevents.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.model.database.local.dao.NotificationUidDao
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid.Companion.DEFAULT_UID
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid.Companion.SENTINEL_VALUE
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NotificationUidDaoTest {
    private lateinit var notificationUidDao: NotificationUidDao
    private lateinit var localDatabase: LocalDatabase

    @Before
    fun createDB() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        localDatabase = Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        notificationUidDao = localDatabase.notificationUidDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        localDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun testInsertNotificationUid() = runBlocking {
        notificationUidDao.insert(NotificationUid())
        val retrieved = notificationUidDao.getNotificationUid()
        assertFalse(retrieved.isEmpty())
        val retrievedNotificationUid = retrieved[0]
        assertEquals(
            NotificationUid(id = SENTINEL_VALUE, uid = DEFAULT_UID),
            retrievedNotificationUid
        )
    }

    @Test
    @Throws(Exception::class)
    fun testUpdateNotificationUid() = runBlocking {
        notificationUidDao.insert(NotificationUid())
        var retrieved = notificationUidDao.getNotificationUid()
        assertFalse(retrieved.isEmpty())

        val newNotificationUid = retrieved[0].copy(uid = 5)
        notificationUidDao.insert(newNotificationUid)

        retrieved = notificationUidDao.getNotificationUid()
        assertFalse(retrieved.isEmpty())
        val retrievedNotificationUid = retrieved[0]
        assertEquals(newNotificationUid, retrievedNotificationUid)
    }
}
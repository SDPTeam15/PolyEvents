package com.github.sdpteam15.polyevents.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.dao.GenericEntityDao
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import com.github.sdpteam15.polyevents.model.database.local.adapter.LocalAdapter
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GenericEntityDaoTest {
    private lateinit var genericEntityDao: GenericEntityDao
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
        genericEntityDao = localDatabase.genericEntityDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        localDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun test() = runBlocking {
        genericEntityDao.insert(GenericEntity("id"))
        assertEquals("id", genericEntityDao.getAll("")[0].id)
        assertEquals("id", genericEntityDao.get("id", "")?.id)
        assertNull(genericEntityDao.get("", ""))


        genericEntityDao.insertAll(listOf(GenericEntity("id2")))
        assertEquals("id2", genericEntityDao.get("id2", "")?.id)

        genericEntityDao.delete(GenericEntity("id2"))
        assertNull(genericEntityDao.get("id2", ""))

        genericEntityDao.deleteAll("")
        assertNull(genericEntityDao.get("id2", ""))

        val date = LocalAdapter.SimpleDateFormat.format(HelperFunctions.localDateTimeToDate(LocalDateTime.now())!!).toString()
        genericEntityDao.insert(GenericEntity("id", update_time = date))

        assertEquals(LocalAdapter.SimpleDateFormat.parse(date), genericEntityDao.lastUpdateDate(""))
    }
}
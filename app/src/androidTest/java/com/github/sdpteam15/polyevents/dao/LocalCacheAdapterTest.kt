package com.github.sdpteam15.polyevents.dao

import com.github.sdpteam15.polyevents.model.database.local.LocalCacheAdapter
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

/*
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.Scope
import com.github.sdpteam15.polyevents.model.database.local.LocalAdapter
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.TEST_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.StringWithID
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterInterface
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import kotlinx.coroutines.*
import org.junit.After
import org.mockito.kotlin.anyOrNull
import java.io.IOException
import org.mockito.Mockito.`when` as When
*/

@Suppress("UNCHECKED_CAST")
class LocalCacheAdapterTest {
    lateinit var database: DatabaseInterface
    lateinit var localCacheDatabase: DatabaseInterface

    @Before
    fun setup() {
        database = object : DatabaseInterface {
            override val currentUserObservable = Observable<UserEntity>()
            override var itemDatabase =
                mock(ItemDatabaseInterface::class.java)
            override var zoneDatabase =
                mock(ZoneDatabaseInterface::class.java)
            override var userDatabase =
                mock(UserDatabaseInterface::class.java)
            override var heatmapDatabase =
                mock(HeatmapDatabaseInterface::class.java)
            override var eventDatabase =
                mock(EventDatabaseInterface::class.java)
            override var materialRequestDatabase =
                mock(MaterialRequestDatabaseInterface::class.java)
            override var userSettingsDatabase =
                mock(UserSettingsDatabaseInterface::class.java)
            override var routeDatabase =
                mock(RouteDatabaseInterface::class.java)


            override fun <T : Any> addEntityAndGetId(
                element: T,
                collection: DatabaseConstant.CollectionConstant,
                adapter: AdapterToDocumentInterface<in T>
            ) = Observable<String>()

            override fun <T : Any> addListEntity(
                elements: List<T>,
                collection: DatabaseConstant.CollectionConstant,
                adapter: AdapterToDocumentInterface<in T>
            ) = Observable<Pair<Boolean, List<String>>>()

            override fun <T : Any> setEntity(
                element: T?,
                id: String,
                collection: DatabaseConstant.CollectionConstant,
                adapter: AdapterToDocumentInterface<in T>
            ) = Observable<Boolean>()

            override fun <T : Any> setListEntity(
                elements: List<Pair<String, T?>>,
                collection: DatabaseConstant.CollectionConstant,
                adapter: AdapterToDocumentInterface<in T>
            ) = Observable<Pair<Boolean, List<Boolean>>>()

            override fun <T : Any> getEntity(
                element: Observable<T>,
                id: String,
                collection: DatabaseConstant.CollectionConstant,
                adapter: AdapterFromDocumentInterface<out T>
            ) = Observable<Boolean>()

            override fun <T : Any> getMapEntity(
                elements: ObservableMap<String, T>,
                ids: List<String>?,
                matcher: Matcher?,
                collection: DatabaseConstant.CollectionConstant,
                adapter: AdapterFromDocumentInterface<out T>
            ) = Observable<Boolean>()

        }
        localCacheDatabase = LocalCacheAdapter(database)
        /*
        val scope = mock(Scope::class.java)
        PolyEventsApplication.application.applicationScope = scope

        When(scope.launch(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            runBlocking {
                (it.arguments[2] as suspend CoroutineScope.() -> Unit)()
            }
        }

        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        PolyEventsApplication.application.localDatabase =
            Room.inMemoryDatabaseBuilder(context, LocalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()

        PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
            PolyEventsApplication.application.localDatabase.genericEntityDao().deleteAll()
        }*/
    }

    /*
    @After
    fun closeDb() {
        PolyEventsApplication.application.localDatabase.close()
        PolyEventsApplication.application.localDatabase = PolyEventsApplication.application.defaultLocalDatabase
    }
    */
    @Test
    fun returnRightSubDataBase() {
        assertEquals(
            database.currentUserObservable,
            localCacheDatabase.currentUserObservable
        )

        assertEquals(
            database.currentUser,
            localCacheDatabase.currentUser
        )
        localCacheDatabase.currentUser =
            database.currentUser

        assertEquals(
            database.itemDatabase,
            localCacheDatabase.itemDatabase
        )
        localCacheDatabase.itemDatabase =
            database.itemDatabase

        assertEquals(
            database.zoneDatabase,
            localCacheDatabase.zoneDatabase
        )
        localCacheDatabase.zoneDatabase =
            database.zoneDatabase

        assertEquals(
            database.userDatabase,
            localCacheDatabase.userDatabase
        )
        localCacheDatabase.userDatabase =
            database.userDatabase

        assertEquals(
            database.heatmapDatabase,
            localCacheDatabase.heatmapDatabase
        )
        localCacheDatabase.heatmapDatabase =
            database.heatmapDatabase

        assertEquals(
            database.eventDatabase,
            localCacheDatabase.eventDatabase
        )
        localCacheDatabase.eventDatabase =
            database.eventDatabase

        assertEquals(
            database.materialRequestDatabase,
            localCacheDatabase.materialRequestDatabase
        )
        localCacheDatabase.materialRequestDatabase =
            database.materialRequestDatabase

        assertEquals(
            database.userSettingsDatabase,
            localCacheDatabase.userSettingsDatabase
        )
        localCacheDatabase.userSettingsDatabase =
            database.userSettingsDatabase

        assertEquals(
            database.routeDatabase,
            localCacheDatabase.routeDatabase
        )
        localCacheDatabase.routeDatabase =
            database.routeDatabase
    }
    /*
    @Test
    fun addEntityAndGetId() {
        localCacheDatabase.addEntityAndGetId(
            StringWithID("ID", "STR"),
            TEST_COLLECTION
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID",
                        TEST_COLLECTION.value
                    ).apply(false) {
                        it.id == "ID"
                    }
                )
            }
        }.then.postValue("ID")

        localCacheDatabase.addEntityAndGetId(
            StringWithID("ID2", "STR"),
            TEST_COLLECTION
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID2",
                        TEST_COLLECTION.value
                    ).apply(true) { false }
                )
            }
        }.then.postValue("")
    }

    @Test
    fun addListEntity() {
        localCacheDatabase.addListEntity(
            listOf(
                StringWithID("ID", "STR"),
                StringWithID("ID2", "STR"),
            ),
            TEST_COLLECTION
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID",
                        TEST_COLLECTION.value
                    ).apply(false) { it.id == "ID" }
                )
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID2",
                        TEST_COLLECTION.value
                    ).apply(true) { false }
                )
            }
        }.then.postValue(Pair(false, listOf("ID", "")))
    }

    @Test
    fun setEntity() {
        localCacheDatabase.setEntity(
            StringWithID("ID", "STR"),
            "ID",
            TEST_COLLECTION
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID",
                        TEST_COLLECTION.value
                    ).apply(false) { it.id == "ID" }
                )
            }
        }.then.postValue(true)

        localCacheDatabase.setEntity(
            StringWithID("ID2", "STR"),
            "ID",
            TEST_COLLECTION
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID2",
                        TEST_COLLECTION.value
                    ).apply(true) { false }
                )
            }
        }.then.postValue(false)
    }

    @Test
    fun setListEntity() {
        localCacheDatabase.setListEntity(
            listOf(
                Pair("ID", StringWithID("ID", "STR")),
                Pair("ID2", StringWithID("ID2", "STR")),
            ),
            TEST_COLLECTION
        ).observeOnce {
            PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID",
                        TEST_COLLECTION.value
                    ).apply(false) { it.id == "ID" }
                )
                assert(
                    PolyEventsApplication.application.localDatabase.genericEntityDao().get(
                        "ID2",
                        TEST_COLLECTION.value
                    ).apply(true) { false }
                )
            }
        }.then.postValue(Pair(false, listOf(true, false)))
    }

    @Test
    fun getEntity() {
        PolyEventsApplication.application.applicationScope.launch(Dispatchers.IO) {
            PolyEventsApplication.application.localDatabase.genericEntityDao().insert(
                LocalAdapter.toDocument(
                    (TEST_COLLECTION.adapter as AdapterInterface<StringWithID>).toDocument(
                        StringWithID("ID", "STR")
                    ),
                    "ID",
                    TEST_COLLECTION.value
                )
            )
        }

        localCacheDatabase.getEntity(
            Observable<StringWithID>().observeOnce {
                val i = 0
                assert(i == 0)
            }.then,
            "ID",
            TEST_COLLECTION
        ).observeOnce {
            val i = 0
            assert(i == 0)
        }
    }

    @Test
    fun getMapEntity() {

    }
    */
}
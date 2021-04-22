package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.objects.*
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.util.AdapterInterface
import com.github.sdpteam15.polyevents.util.AdapterToDocumentInterface
import kotlin.random.Random

object FakeDatabase : DatabaseInterface {
    var CURRENT_USER: UserEntity = UserEntity(
        uid = "FakeUID",
        name = "FakeName",
        email = "Fake@mail.ch"
    )
    var userToNull = false
    override val currentUserObservable: Observable<UserEntity> = Observable()

    override var currentUser: UserEntity?=null
        get() = if (userToNull) {
            null
        } else {
            CURRENT_USER
        }
    override var currentProfile: UserProfile? = null
        get() = null

    override var itemDatabase: ItemDatabaseInterface? = null
        get() = field ?: FakeDatabaseItem

    override var zoneDatabase: ZoneDatabaseInterface? = null
        get() = field ?: FakeDatabaseZone

    override var userDatabase: UserDatabaseInterface? = null
        get() = field ?: FakeDatabaseUser

    override var heatmapDatabase: HeatmapDatabaseInterface? = null
        get() = field ?: FakeDatabaseHeatmap

    override var eventDatabase: EventDatabaseInterface? = null
        get() = field ?: FakeDatabaseEvent

    override var materialRequestDatabase: MaterialRequestDatabaseInterface? = null
        get() = field ?: FakeDatabaseMaterialRequest

    override fun <T> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<T>
    ): Observable<String> {
        TODO("Not yet implemented")
    }

    override fun <T> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<T>?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun deleteEntity(
        id: String,
        collection: DatabaseConstant.CollectionConstant
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>?,
        matcher: Matcher?,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    /**
     * Generates a random key simulating fireBase
     */
    fun generateRandomKey(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..20)
            .map { _ -> Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
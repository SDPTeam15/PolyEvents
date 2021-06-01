package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import kotlin.random.Random

object FakeDatabase : DatabaseInterface {
    var CURRENT_USER: UserEntity = UserEntity(
        uid = "FakeUID",
        name = "FakeName",
        email = "Fake@mail.ch"
    )
    var userToNull = false
    override val currentUserObservable: Observable<UserEntity> = Observable()

    override var currentUser: UserEntity? = null
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

    override var routeDatabase: RouteDatabaseInterface?=null
        get() = field ?: FakeDatabaseRoute

    override var userSettingsDatabase: UserSettingsDatabaseInterface? = null
        get() = field ?: FakeDatabaseUserSettings

    override fun <T : Any> addEntityAndGetId(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<String> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> addEntity(
        element: T,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> addListEntity(
        elements: List<T>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<String?>>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> setEntity(
        element: T?,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> setListEntity(
        elements: List<Pair<String, T?>>,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterToDocumentInterface<in T>
    ): Observable<Pair<Boolean, List<Boolean>>> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getEntity(
        element: Observable<T>,
        id: String,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getMapEntity(
        elements: ObservableMap<String, T>,
        ids: List<String>?,
        matcher: Matcher?,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getListEntity(
        elements: ObservableList<T>,
        ids: List<String>?,
        matcher: Matcher?,
        collection: DatabaseConstant.CollectionConstant,
        adapter: AdapterFromDocumentInterface<out T>
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
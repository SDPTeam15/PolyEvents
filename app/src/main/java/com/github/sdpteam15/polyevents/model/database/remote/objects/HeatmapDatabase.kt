package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.Settings
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.LOCATION_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelper.neBound
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelper.swBound
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.DeviceLocation
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.DeviceLocationAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Query
import java.time.LocalDateTime
import kotlin.random.Random

class HeatmapDatabase(private val db: DatabaseInterface) : HeatmapDatabaseInterface {

    override fun setLocation(
        location: LatLng
    ): Observable<Boolean> {
        val element = DeviceLocation(Settings.LocationId, location, LocalDateTime.now())
        return if (Settings.LocationId == "")
            db.addEntityAndGetId(
                element,
                LOCATION_COLLECTION
            ).mapOnce {
                Settings.LocationId = it
                it != ""
            }.then
        else db.setEntity(
            element,
            Settings.LocationId,
            LOCATION_COLLECTION
        )
    }

    //TODO : remove the added points for the final
    override fun getLocations(
        usersLocations: ObservableList<LatLng>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        val tempUsersLocations = ObservableList<LatLng>()
        val end = Observable<Boolean>()
        db.getListEntity(
            tempUsersLocations,
            null,
            object : Matcher {
                override fun match(collection: Query) = collection.whereGreaterThan(
                    DatabaseConstant.LocationConstant.LOCATIONS_TIME.value,
                    HelperFunctions.localDateTimeToDate(
                        LocalDateTime.now().minusMinutes(10)
                    )!!
                )
            },
            LOCATION_COLLECTION,
            object : AdapterFromDocumentInterface<LatLng> {
                override fun fromDocument(document: MutableMap<String, Any?>, id: String): LatLng =
                    DeviceLocationAdapter.fromDocument(document, id).location
            }
        ).observeOnce {
            if (!it.value)
                end.postValue(it.value, it.sender)
        }
        tempUsersLocations.observeOnce {
            val list = mutableListOf<LatLng>()
            for (e in it.value)
                list.add(e)

            var dl = 0.0002
            for (i in 1..5) {
                val latitude = Random.nextDouble(swBound.latitude, neBound.latitude)
                val longitude = Random.nextDouble(swBound.longitude, neBound.longitude)
                for (j in 0..Random.nextInt(5, 75)) {
                    list.add(
                        LatLng(
                            latitude + Random.nextDouble(-dl, dl),
                            longitude + Random.nextDouble(-dl, dl)
                        )
                    )
                }
            }

            usersLocations.addAll(list, it.sender)
            end.postValue(true, it.sender)
        }
        return end
    }
}
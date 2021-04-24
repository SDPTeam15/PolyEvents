package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.Settings
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.LOCATION_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper.neBound
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper.swBound
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.DeviceLocation
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.util.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.util.DeviceLocationAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Query
import java.time.LocalDateTime
import kotlin.random.Random

class HeatmapDatabase(private val db: DatabaseInterface) : HeatmapDatabaseInterface {

    override fun setLocation(
        location: LatLng
    ): Observable<Boolean> {
        val element = DeviceLocation(Settings.LocationId, location, LocalDateTime.now())
        if (Settings.LocationId == "") {
            val ended = Observable<Boolean>()
            db.addEntityAndGetId(
                element,
                LOCATION_COLLECTION,
                DeviceLocationAdapter
            ).observeOnce {
                Settings.LocationId = it.value
                ended.postValue(it.value != "", it.sender)
            }
            return ended
        } else
            return FirestoreDatabaseProvider.setEntity(
                element,
                Settings.LocationId,
                LOCATION_COLLECTION,
                DeviceLocationAdapter
            )
    }

    override fun getLocations(
        usersLocations: ObservableList<LatLng>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        val tempusersLocations = ObservableList<LatLng>()
        val end = Observable<Boolean>()
        db.getListEntity(
            tempusersLocations,
            null,
            object : Matcher {
                override fun match(collection: Query): Query {
                    var query = collection/*
                    query = query.whereGreaterThan(
                        DatabaseConstant.LocationConstant.LOCATIONS_POINT_LATITUDE.value,
                        swBound.latitude
                    )
                    query = query.whereGreaterThan(
                        DatabaseConstant.LocationConstant.LOCATIONS_POINT_LONGITUDE.value,
                        swBound.longitude
                    )
                    query = query.whereLessThan(
                        DatabaseConstant.LocationConstant.LOCATIONS_POINT_LATITUDE.value,
                        neBound.latitude
                    )
                    query = query.whereLessThan(
                        DatabaseConstant.LocationConstant.LOCATIONS_POINT_LONGITUDE.value,
                        neBound.longitude
                    )*/
                    query = query.whereGreaterThan(
                        DatabaseConstant.LocationConstant.LOCATIONS_TIME.value,
                        HelperFunctions.localDateTimeToDate(
                            LocalDateTime.now().minusMinutes(10)
                        )!!
                    )
                    return query
                }
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
        tempusersLocations.observeOnce {
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
package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.Settings
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.LOCATION_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.DeviceLocation
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.util.DeviceLocationAdapter
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

class HeatmapDatabase(private val db : DatabaseInterface) : HeatmapDatabaseInterface {

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
                ended.postValue(it.value != "", this)
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
        TODO()
    }
}
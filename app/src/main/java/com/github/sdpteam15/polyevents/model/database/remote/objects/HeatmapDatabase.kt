package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.LOCATION_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.DeviceLocationAdapter
import com.github.sdpteam15.polyevents.model.entity.DeviceLocation
import com.github.sdpteam15.polyevents.model.map.GoogleMapOptions.neBound
import com.github.sdpteam15.polyevents.model.map.GoogleMapOptions.swBound
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import kotlin.random.Random

class HeatmapDatabase(private val db: DatabaseInterface) : HeatmapDatabaseInterface {

    override fun setLocation(
        location: LatLng,
        userSettings: ObservableList<UserSettings>
    ): Observable<Boolean> {
        val userSettingsInstance: UserSettings = userSettings[0]
        val element = DeviceLocation(userSettingsInstance.locationId, location, LocalDateTime.now())
        return if (userSettingsInstance.locationId == null)
            db.addEntityAndGetId(
                element,
                LOCATION_COLLECTION
            ).mapOnce {
                userSettings.updateAll(listOf(userSettingsInstance.copy(locationId = it)))
                it != ""
            }.then
        else db.setEntity(
            element,
            userSettingsInstance.locationId!!,
            LOCATION_COLLECTION
        )
    }

    //TODO : remove the added points for the final
    override fun getLocations(
        usersLocations: ObservableList<LatLng>
    ): Observable<Boolean> {
        val tempUsersLocations = ObservableList<LatLng>()
        val end = Observable<Boolean>()
        db.getListEntity(
            tempUsersLocations,
            null,
            { collection ->
                collection.whereGreaterThan(
                    DatabaseConstant.LocationConstant.LOCATIONS_TIME.value,
                    HelperFunctions.localDateTimeToDate(
                        LocalDateTime.now().minusMinutes(10)
                    )!!
                )
            },
            LOCATION_COLLECTION,
            object : AdapterFromDocumentInterface<LatLng> {
                override fun fromDocument(document: Map<String, Any?>, id: String): LatLng =
                    DeviceLocationAdapter.fromDocument(document, id).location
            }
        ).observeOnce {
            if (!it.value)
                end.postValue(it.value, it.sender)
        }
        tempUsersLocations.observeOnce(false) {
            val list = mutableListOf<LatLng>()
            for (e in it.value)
                list.add(e)

            val dl = 0.0002
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
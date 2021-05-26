package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseZone : ZoneDatabaseInterface {
    override fun createZone(zone: Zone, userAccess: UserProfile?): Observable<Boolean> {
        return Observable(true)
    }

    override fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        zone.postValue(Zone("ID1", "Esplanade", "Espla", "a cool zone"), this)
        return Observable(true)
    }

    override fun updateZoneInformation(
        zoneId: String,
        newZone: Zone,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return Observable(true)
    }

    override fun getAllZones(
        matcher: Matcher?,
        number: Long?,
        zones: ObservableList<Zone>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        zones.add(Zone("ID2", "2222", "222222", "22222"), this)
        return Observable(true)
    }

    override fun deleteZone(zone: Zone, userAccess: UserProfile?): Observable<Boolean> {
        return Observable(true)
    }
}
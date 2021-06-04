package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseZone : ZoneDatabaseInterface {

    val zones = mutableListOf<Zone>()
    override fun createZone(zone: Zone): Observable<Boolean> {
        zones.add(zone)
        return Observable(true)
    }

    override fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>
    ): Observable<Boolean> {
        zone.postValue(zones.first { it.zoneId == zoneId }, FakeDatabase)
        return Observable(true)
    }

    override fun updateZoneInformation(
        zoneId: String,
        newZone: Zone
    ): Observable<Boolean> {
        zones[zones.indexOfFirst { it.zoneId == zoneId }] = newZone
        return Observable(true)
    }

    override fun getActiveZones(
        zones: ObservableList<Zone>,
        number: Long?
    ): Observable<Boolean> {
        zones.clear()
        zones.addAll(this.zones)
        return Observable(true)
    }

    override fun deleteZone(zone: Zone): Observable<Boolean> {
        zones.removeIf { it.zoneId == zone.zoneId }
        return Observable(true)
    }
}
package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.ZONE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ZoneAdapter
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class ZoneDatabase(private val db: DatabaseInterface) : ZoneDatabaseInterface {
    override fun createZone(zone: Zone): Observable<Boolean> =
        db.addEntity(zone, ZONE_COLLECTION, ZoneAdapter)

    override fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>
    ): Observable<Boolean> = db.getEntity(zone, zoneId, ZONE_COLLECTION, ZoneAdapter)

    override fun updateZoneInformation(
        zoneId: String,
        newZone: Zone
    ): Observable<Boolean> = db.setEntity(newZone, zoneId, ZONE_COLLECTION)

    override fun getAllZones(
        matcher: Matcher?,
        number: Long?,
        zones: ObservableList<Zone>
    ): Observable<Boolean> =
        db.getListEntity(zones, null, matcher, ZONE_COLLECTION, ZoneAdapter)

    override fun deleteZone(zone: Zone): Observable<Boolean> =
        db.deleteEntity(zone.zoneId!!, ZONE_COLLECTION)
}
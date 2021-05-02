package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserProfile

object FakeDatabaseMaterialRequest : MaterialRequestDatabaseInterface {
    val requests = mutableListOf<MaterialRequest>()

    override fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: Matcher?,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return Observable(materialList.addAll(requests))
    }

    override fun createMaterialRequest(
        request: MaterialRequest,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return Observable(requests.add(request))
    }

}
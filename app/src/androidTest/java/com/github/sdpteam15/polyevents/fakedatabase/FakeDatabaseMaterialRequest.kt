package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseMaterialRequest : MaterialRequestDatabaseInterface {
    val requests = mutableMapOf<String,MaterialRequest>()
    override fun updateMaterialRequest(
        id: String,
        materialRequest: MaterialRequest
    ): Observable<Boolean> {
        requests[id] = materialRequest
        return Observable(true)
    }

    override fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: Matcher?
    ): Observable<Boolean> {
        materialList.clear(FakeDatabase)
        materialList.addAll(requests.values,FakeDatabase)
        return Observable(true)
    }

    override fun createMaterialRequest(
        request: MaterialRequest
    ): Observable<Boolean> {
        val key = FakeDatabase.generateRandomKey()
        requests[key] = request.copy(requestId = key)
        return Observable(true)
    }

    override fun getMaterialRequestListByUser(
        materialList: ObservableList<MaterialRequest>,
        userId: String
    ): Observable<Boolean> {
        materialList.clear(FakeDatabase)
        for (request in requests.values)
            materialList.add(request,FakeDatabase)
        return Observable(true)
    }

    override fun deleteMaterialRequest(
        materialRequestId: String
    ): Observable<Boolean> {
        requests.remove(materialRequestId)
        return Observable(true)
    }

    override fun getMaterialRequestById(
        materialRequest: Observable<MaterialRequest>,
        requestId: String
    ): Observable<Boolean> {
        materialRequest.postValue(requests[requestId],FakeDatabase)
        return Observable(true)
    }

}
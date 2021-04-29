package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserProfile

object FakeDatabaseMaterialRequest : MaterialRequestDatabaseInterface {
    override fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: Matcher?,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun createMaterialRequest(
        request: MaterialRequest,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

}
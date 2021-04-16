package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserProfile

object FakeDatabaseMaterialRequest : MaterialRequestDatabaseInterface {

    override fun answerMaterialRequest(
        id: String,
        answer: Boolean,
        profile: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getMaterialRequestList(
        materialList: Observable<MaterialRequest>,
        matcher: String?,
        profile: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun createMaterialRequest(request: MaterialRequest, profile: UserProfile?) {
        TODO("Not yet implemented")
    }
}
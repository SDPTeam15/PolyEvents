package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface MaterialRequestDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser


    /**
     * Update a material request
     * @param id id of the items
     * @param materialRequest updated material request
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateMaterialRequest(
        id: String,
        materialRequest: MaterialRequest
    ): Observable<Boolean>


    /**
     * Get the list of all material request
     * @param materialList list in which the list of all material request will be set after retrieving from database
     * @param matcher matcher for the search
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: Matcher? = null
    ): Observable<Boolean>

    /**
     * @param request the request we want to add in the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createMaterialRequest(
        request: MaterialRequest
    ): Observable<Boolean>

    /**
     * Get the list of all material request for a user
     * @param materialList list in which the list of all material request will be set after retrieving from database
     * @param userId id of the user
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMaterialRequestListByUser(
        materialList: ObservableList<MaterialRequest>,
        userId: String
    ): Observable<Boolean>

    /**
     * Delete a material request
     * @param materialRequestId updated material request
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun deleteMaterialRequest(
        materialRequestId: String
    ): Observable<Boolean>

    /**
     * Get the material request from a specific request id
     * @param materialRequest request observable to store the material request
     * @param requestId id of the material request to retrieve
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMaterialRequestById(
        materialRequest: Observable<MaterialRequest>,
        requestId: String
    ): Observable<Boolean>
}
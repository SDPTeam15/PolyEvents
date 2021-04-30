package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile

interface MaterialRequestDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile
/*
    // TODO maybe split this function into "answerMaterialRequest() and refuseMaterialRequest()"
    /**
     * Answer a material request
     * @param id id of the items
     * @param answer true or false depending if we accept the request or not
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun answerMaterialRequest(
        id: String,
        answer: Boolean,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>
*/

    /**
     * Get the list of all material request
     * @param materialList list in which the list of all material request will be set after retrieving from database
     * @param matcher matcher for the search
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getMaterialRequestList(
        materialList: ObservableList<MaterialRequest>,
        matcher: Matcher? = null,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param request the request we want to add in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createMaterialRequest(
        request: MaterialRequest,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

}
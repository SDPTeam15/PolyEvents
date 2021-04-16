package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.MaterialRequest
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

interface MaterialRequestDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

  /**
   * Answer a material request
   * @param id: id of the items
   * @param answer true or false depending if we accept the request or not
   * @param profile profile for database access
   * @return An observer that will be set to true if the communication with the DB is over and no error
   */
  fun answerMaterialRequest(
      id:String,
      answer:Boolean,
      profile: UserProfile? = currentProfile
  ): Observable<Boolean>

  /**
   * Get the list of all material request
   * @param materialList list in which the list of all material request will be set after retrieving from database
   * @param matcher: matcher for the search
   * @param profile profile for database access
   * @return An observer that will be set to true if the communication with the DB is over and no error
   */
  fun getMaterialRequestList(
      materialList: Observable<MaterialRequest>,
      matcher: String? = null,
      profile: UserProfile? = currentProfile
  ): Observable<Boolean>

  /**
   * @param the request we want to add in the database
   * @param profile profile for database access
   * @return An observer that will be set to true if the communication with the DB is over and no error
   */
  fun createMaterialRequest(
      request: MaterialRequest,
      profile: UserProfile? = currentProfile
  )

}
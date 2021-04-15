package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity

object UserDatabaseFirestore: UserDatabaseInterface {
    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser

    override fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun updateProfile(
        newValues: Map<String, String>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun firstConnexion(user: UserEntity, userAccess: UserEntity?): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String?,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }
}
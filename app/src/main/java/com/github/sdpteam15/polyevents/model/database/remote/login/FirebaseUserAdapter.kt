package com.github.sdpteam15.polyevents.model.database.remote.login

import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.google.firebase.auth.FirebaseUser

/**
 * FirebaseUser adapter
 */
object FirebaseUserAdapter {
    fun toUser(firebaseUser: FirebaseUser): UserEntity {
        return UserEntity(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName,
            email = firebaseUser.email,
            profiles = ArrayList()
        )
    }
}
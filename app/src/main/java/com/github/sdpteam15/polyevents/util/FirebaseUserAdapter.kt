package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.UserEntity
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
            telephone = firebaseUser.phoneNumber
        )
    }
}
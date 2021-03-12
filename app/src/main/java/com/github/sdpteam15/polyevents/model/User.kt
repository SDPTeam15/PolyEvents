package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties

// TODO: builder pattern maybe?
@IgnoreExtraProperties
data class UserEntity (
    val googleId: String,
    val userType: String = ADMIN,
    val username: String? = null,
    val name: String? = null,
    val age: Int? = null,
    val displayName: String? = null,
    val email: String? = null) {

    private constructor() : this(
        "",  ADMIN, null, null, null,
        null
    )

    fun isAdmin(): Boolean =
        this.userType == ADMIN

    companion object {
        const val ADMIN = "admin"
        const val ORGANIZER = "organizer"
        const val STAFF = "staff"
        const val PARTICIPANT = "participant"
    }
}
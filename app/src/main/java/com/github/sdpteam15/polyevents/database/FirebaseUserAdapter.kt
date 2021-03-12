package com.github.sdpteam15.polyevents.database

import com.google.firebase.auth.FirebaseUser

/**
 * FirebaseUser adapter
*/
class FirebaseUserAdapter constructor(private val firebaseUser : FirebaseUser) : FirebaseUserInterface{
    override val displayName: String
        get() = firebaseUser.displayName
    override val uid: String
        get() = firebaseUser.uid
    override val email: String?
        get() = firebaseUser.email
}
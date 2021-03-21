package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.user.Rank
import com.google.firebase.auth.FirebaseUser

/**
 * FirebaseUser adapter
*/
class FirebaseUserAdapter constructor(private val firebaseUser : FirebaseUser) : DatabaseUserInterface{
    override val displayName: String?
        get() = firebaseUser.displayName
    override val uid: String
        get() = firebaseUser.uid
    override val email: String?
        get() = firebaseUser.email
    override val rank: Rank
        get() = Rank.Admin
}
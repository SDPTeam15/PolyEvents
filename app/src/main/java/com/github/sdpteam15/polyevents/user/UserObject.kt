package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.FirebaseUserAdapter
import com.github.sdpteam15.polyevents.database.FirebaseUserInterface
import com.google.firebase.auth.FirebaseAuth

/**
 * Application user constants
 */
object UserObject {
    private var lastCurrentUserUid : String? = null

    /**
     * Current user of the application
     */
    val CurrentUser : UserInterface? get() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser?.uid != lastCurrentUserUid)
            User.invoke(lastCurrentUserUid)?.removeCache()
        return User.invoke(FirebaseUserAdapter(currentUser))
    }
}
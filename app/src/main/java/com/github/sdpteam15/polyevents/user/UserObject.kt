package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.FirebaseUserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.lang.Exception

/**
 * Application user constants
 */
object UserObject {
    private var lastCurrentUserUid: String? = null

    /**
     * Current user of the application
     */
    val CurrentUser: UserInterface?
        get(){
            try {
                val currentUser : FirebaseUser = FirebaseAuth.getInstance().currentUser ?: return null
                if (currentUser.uid != lastCurrentUserUid)
                    User.invoke(lastCurrentUserUid)?.removeCache()
                return User.invoke(FirebaseUserAdapter(currentUser))
            } catch (e : ExceptionInInitializerError) {
                return null
            } catch (e : NoClassDefFoundError){
                return null
            }
        }
}
package com.github.sdpteam15.polyevents.model.database.remote.login

import com.google.firebase.auth.AuthResult


object UserLogin {
    private var mutableCurrentUserLogin: UserLoginInterface<AuthResult>? = null

    /**
     * Current user login method in the application
     */
    var currentUserLogin: UserLoginInterface<AuthResult>
        get() {
            mutableCurrentUserLogin = mutableCurrentUserLogin ?: GoogleUserLogin
            return mutableCurrentUserLogin!!
        }
        set(value) {
            mutableCurrentUserLogin = value
        }
}
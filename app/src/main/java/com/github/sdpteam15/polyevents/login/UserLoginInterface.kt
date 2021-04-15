package com.github.sdpteam15.polyevents.login

import android.app.Activity
import android.content.Intent
import com.github.sdpteam15.polyevents.fragments.LoginFragment
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.tasks.Task

interface UserLoginInterface<T> {
    fun getResultFromIntent(data: Intent?, activity: Activity, errorMsg: String): Task<T>?
    fun getCurrentUser(): UserEntity?
    fun signOut()
    fun signIn(activity: Activity, fragment: LoginFragment, reqCode: Int)
}
package com.github.sdpteam15.polyevents.model.database.remote.login

import android.app.Activity
import android.content.Intent
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.view.fragments.LoginFragment
import com.google.android.gms.tasks.Task

interface UserLoginInterface<T> {
    /**
     * Analyse the received data, display error messages or get the account information and terminate
     * @param data Data received in the login fragment
     * @param activity the current activity to display error messages
     * @param errorMsg the error message to display
     * @return A task that get the information and completes the login
     */
    fun getResultFromIntent(data: Intent?, activity: Activity, errorMsg: String): Task<T>?

    /**
     * Return the current user logged in the app
     * @return the user entity currently logged into the app
     */
    fun getCurrentUser(): UserEntity?

    /**
     * Log out the user from the application
     */
    fun signOut()

    /**
     * Return true if the user currently connected, false otherwise
     */
    fun isConnected(): Boolean

    /**
     * Method to log the user in the application
     * @param activity: The current activity to use the context
     * @param fragment: the fragment to which the information will be sent
     * @param reqCode: The request code to which the login fragment reacts
     */
    fun signIn(activity: Activity, fragment: LoginFragment, reqCode: Int)
}
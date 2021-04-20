package com.github.sdpteam15.polyevents.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.tasks.OnCompleteListener

/**
 * [Fragment] subclass representing the login page allowing the user to connect to the application
 */


class LoginFragment : Fragment() {
    val inDbObservable = Observable<Boolean>()
    val SIGN_IN_RC: Int = 200
    // Return CurrentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserEntity? = null
        get() = field ?: currentDatabase.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If the user is already logged in, we redirect him directly to the Profile fragment
        if (currentUser != null) {
            HelperFunctions.changeFragment(
                activity,
                MainActivity.fragments[R.id.id_fragment_profile]
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Requested code is the sign in code
        if (requestCode == SIGN_IN_RC) {
            //Redirect the data received to the login object
            // If the task is successful we redirect otherwise we display an error message
            UserLogin.currentUserLogin
                .getResultFromIntent(
                    data,
                    activity as Activity,
                    getString(R.string.login_failed_text)
                )?.addOnCompleteListener(activity as Activity) { task ->
                    if (task.isSuccessful) {
                        addIfNotInDB()
                    } else {
                        HelperFunctions.showToast(getString(R.string.login_failed_text), activity)
                    }
                }
        }
    }

    /**
     * This method will call the inDatabase method to check whether the users has already been registered to the database or not
     */
    private fun addIfNotInDB() {
        currentDatabase.userDatabase!!.inDatabase(inDbObservable, currentUser!!.uid)
            .observe(this) { success ->
                if (success.value) {
                    if (inDbObservable.value!!) {
                        //If already in database, redirect to the profile fragment
                        HelperFunctions.changeFragment(
                            activity,
                            MainActivity.fragments[R.id.id_fragment_profile]
                        )
                    } else {
                        //If not in DB, i.e. first connection, register it
                        createAccountAndRedirect()
                    }
                } else {
                    //if a problem occurs, display an error message
                    HelperFunctions.showToast(getString(R.string.login_failed_text), activity)
                }
            }
    }

    /**
     * This method is called when the user log in for the first item with its account
     * It registers the users into the database and redirect him to the ProfileFragment if no problem during the communication with the database
     */
    private fun createAccountAndRedirect() {
        currentDatabase
            .userDatabase!!
            .firstConnexion(currentUser!!)
            .observe(this) { success ->
                if (success.value) {
                    //If correctly registered, redirect it
                    HelperFunctions.changeFragment(
                        activity,
                        MainActivity.fragments[R.id.id_fragment_profile]
                    )
                } else {
                    //Display error message
                    HelperFunctions.showToast(getString(R.string.login_failed_text), activity)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        //Attach an event to sign-in button
        rootView.findViewById<com.google.android.gms.common.SignInButton>(R.id.btnLogin)
            .setOnClickListener {
                if (currentUser == null) {
                    UserLogin.currentUserLogin.signIn(activity as Activity, this, SIGN_IN_RC)
                } else {
                    //This branch allow us to test the communication between ProfileFragment and LoginFragment. During a normal execution, it won't be used.
                    addIfNotInDB()
                }
            }
        return rootView
    }
}
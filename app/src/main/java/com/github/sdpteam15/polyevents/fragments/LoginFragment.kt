package com.github.sdpteam15.polyevents.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * [Fragment] subclass representing the login page.
 */
private const val SIGN_IN_RC: Int = 200

class LoginFragment : Fragment() {
    val inDbObservable = Observable<Boolean>()

    //Return CurrentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserEntity? = null
        get() = field ?: currentDatabase.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If the user is already logged in, we redirect him to the Profile fragment
        if (currentUser != null) {
            HelperFunctions.changeFragment(
                activity,
                MainActivity.fragments[R.id.id_fragment_profile]
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RC) {
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



    private fun addIfNotInDB(){
        currentDatabase.inDatabase(inDbObservable, currentUser!!.uid, currentUser!!)
            .observe(this) { newValue ->
                if (newValue!!) {
                    if (inDbObservable.value!!) {
                        //If already in database redirect
                        HelperFunctions.changeFragment(
                            activity,
                            MainActivity.fragments[R.id.id_fragment_profile]
                        )
                    } else {
                        //If not in DB, i.e. first connection, need to register
                        connectAndRedirect()
                    }
                } else {
                    HelperFunctions.showToast(getString(R.string.login_failed_text), activity)
                }
            }
    }

    private fun connectAndRedirect() {
        currentDatabase
            .firstConnexion(currentUser!!, currentUser!!)
            .observe(this) { newValue2 ->
                if (newValue2!!) {
                    //If correctly registered, redirect it
                    HelperFunctions.changeFragment(
                        activity,
                        MainActivity.fragments[R.id.id_fragment_profile]
                    )
                } else {
                    //otherwise display error
                    HelperFunctions.showToast(getString(R.string.login_failed_text), activity)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        rootView.findViewById<com.google.android.gms.common.SignInButton>(R.id.btnLogin)
            .setOnClickListener { _ ->
                if (currentUser == null) {
                    UserLogin.currentUserLogin.signIn(activity as Activity,this, SIGN_IN_RC)
                } else {
                    //This branch allow us to test the communication between ProfileFragment and LoginFragment. During a normal execution, it won't be used.
                    addIfNotInDB()
                }
            }
        return rootView
    }
}
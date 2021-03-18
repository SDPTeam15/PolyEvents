package com.github.sdpteam15.polyevents.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * [Fragment] subclass representing the login page.
 */
private const val SIGN_IN_RC: Int = 200

class LoginFragment : Fragment() {
    private lateinit var signIn: GoogleSignInClient
    private lateinit var failedLogin: AlertDialog
    val inDbObservable = Observable<Boolean>()

    //Return CurrentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserInterface? = null
        get() = field ?: User.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If the user is already logged in, we redirect him to the Profile fragment
        if (currentUser != null) {
            HelperFunctions.changeFragment(
                activity,
                MainActivity.fragments[R.id.id_fragment_profile]
            )
        }

        //Sign in options for Google Login.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signIn = GoogleSignIn.getClient(activity as Activity, gso)

        /* Precreate and store the AlertDialog that will be shown any time there is an error*/
        val builder = AlertDialog.Builder(activity as Activity)
        builder.setMessage(R.string.login_failed_text)
            .setTitle(R.string.login_failed_title)
            .setPositiveButton(R.string.ok_button_text) { _, _ -> }
        failedLogin = builder.create()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RC) {
            //get the google account
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    //Google Sign In was successful, authenticate with firebase
                    firebaseAuthWithGoogle(task.getResult(ApiException::class.java)!!.idToken!!)
                } catch (e: ApiException) {
                    failedLogin.show()
                }
            } else {
                failedLogin.show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        //Get the credential back and instantiate FirebaseAuth object
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    addIfNotInDB()
                } else {
                    failedLogin.show()
                }
            }
    }

    private fun addIfNotInDB() {
        currentDatabase.inDatabase(inDbObservable, currentUser!!.uid, currentUser!!)
            .observe(this) { newValue ->
                if (newValue!!) {
                    if (inDbObservable.value!!) {
                        //If already in database redirect
                        HelperFunctions.changeFragment(activity, MainActivity.fragments[R.id.id_fragment_profile])
                    } else {
                        //If not in DB, i.e. first connection, need to register
                        currentDatabase
                            .firstConnexion(currentUser!!, currentUser!!)
                            .observe(this){ newValue2 ->
                                if (newValue2!!) {
                                    //If correctly registered, redirect it
                                    HelperFunctions.changeFragment(activity, MainActivity.fragments[R.id.id_fragment_profile])
                                } else {
                                    //otherwise display error
                                    failedLogin.show()
                                }
                            }
                    }
                } else {
                    failedLogin.show()
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
                    //This "remove" the cache of the chosen user. Without it, google doesn't propose the choice of account anymore and log aumatically into the previously logged one
                    signIn.signOut()
                    startActivityForResult(signIn.signInIntent, SIGN_IN_RC)
                } else {
                    //This branch allow us to test the communication between ProfileFragment and LoginFragment. During a normal execution, it won't be used.
                    addIfNotInDB()
                }
            }
        return rootView
    }
}
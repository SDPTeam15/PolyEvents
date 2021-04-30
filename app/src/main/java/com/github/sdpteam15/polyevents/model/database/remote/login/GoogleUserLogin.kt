package com.github.sdpteam15.polyevents.model.database.remote.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.view.fragments.LoginFragment
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

object GoogleUserLogin : UserLoginInterface<AuthResult> {
    /**
     * The sign option chosen for Google
     */
    var gso: GoogleSignInOptions? = null

    @SuppressLint("StaticFieldLeak")
    var signIn: GoogleSignInClient? = null

    var firebaseAuth:FirebaseAuth? = null
        get()= field?: FirebaseAuth.getInstance()

    override fun getResultFromIntent(
        data: Intent?,
        activity: Activity,
        errorMsg: String
    ): Task<AuthResult>? {
        //get the google account
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            try {
                //Google Sign In was successful, authenticate with firebase
                return firebaseAuthWithGoogle(
                    task.getResult(ApiException::class.java)!!.idToken!!
                )
            } catch (e: ApiException) {
                HelperFunctions.showToast(errorMsg, activity)
            }
        } else {
            HelperFunctions.showToast(errorMsg, activity)
        }
        return null
    }

    private fun firebaseAuthWithGoogle(
        idToken: String
    ): Task<AuthResult> {
        //Get the credential back and instantiate FirebaseAuth object
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth!!.signInWithCredential(credential)
    }

    override fun getCurrentUser(): UserEntity? {
        return if (firebaseAuth!!.currentUser != null) {
            FirebaseUserAdapter.toUser(firebaseAuth!!.currentUser!!)
        } else {
            null
        }
    }

    override fun signOut() {
        firebaseAuth!!.signOut()
    }

    override fun signIn(activity: Activity, fragment: LoginFragment, reqCode: Int) {
        if (gso == null) {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        }

        if (signIn == null) {
            signIn = GoogleSignIn.getClient(activity, gso!!)
        }
        //This "remove" the cache of the chosen user. Without it, google doesn't propose the choice of account anymore and log automatically into the previously logged one
        signIn!!.signOut()
        fragment.startActivityForResult(signIn!!.signInIntent, reqCode)
    }

    override fun isConnected(): Boolean {
        return firebaseAuth!!.currentUser!=null
    }

}
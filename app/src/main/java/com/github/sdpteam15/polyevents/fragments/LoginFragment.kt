package com.github.sdpteam15.polyevents.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.Debug
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.user.UserObject
import com.github.sdpteam15.polyevents.user.UserObject.CurrentUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment(){
    private lateinit var signIn : GoogleSignInClient
    private val SIGN_IN_RC: Int = 200
    private lateinit var failedLogin: AlertDialog

    private var testUser: UserInterface?=null
    //Allow us to use a fake user for the tests
    var currentUser: UserInterface?
        get(){
            if(testUser!= null) { return testUser } else { return UserObject.CurrentUser}
        }
        set(value){
            testUser = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(currentUser !=null){
            HelperFunctions.changeFragment(activity, MainActivity.fragments[R.id.id_fragment_profile])
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        signIn = GoogleSignIn.getClient(activity as Activity, gso)

        /* Create and store the AlertDialog */
        val builder = AlertDialog.Builder(activity as Activity)
        builder.setMessage(R.string.login_failed_text)
            .setTitle(R.string.login_failed_title)
            .setPositiveButton(R.string.ok_button_text, { dialog, id -> {}})
        failedLogin = builder.create()
    }

    fun signInGoogle(){
        signIn.signOut()
        startActivityForResult(signIn.signInIntent, SIGN_IN_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN_RC){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if(task.isSuccessful){
                try{ //Google Sign In was successful, authenticate with firebase
                    firebaseAuthWithGoogle(task.getResult(ApiException::class.java)!!.idToken!!)
                }catch(e: ApiException){ failedLogin.show() }
            }else{
                failedLogin.show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(activity as Activity){
            task-> if(task.isSuccessful){
                HelperFunctions.changeFragment(activity, MainActivity.fragments[R.id.id_fragment_profile])
            }else{
                failedLogin.show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_login, container, false)
        rootView.findViewById<com.google.android.gms.common.SignInButton>(R.id.btnLogin).setOnClickListener {
            _ ->
            if(currentUser == null) {
                signInGoogle()
            } else{
                //In test
                HelperFunctions.changeFragment(activity,MainActivity.fragments[R.id.id_fragment_profile])
            }
        }
        return rootView
    }
}
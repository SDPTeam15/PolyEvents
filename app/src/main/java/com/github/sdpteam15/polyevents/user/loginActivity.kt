package com.github.sdpteam15.polyevents.user

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.github.sdpteam15.polyevents.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlin.math.sign

class loginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var signIn : GoogleSignInClient
    private final val SIGN_IN_RC: Int = 200



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        if(auth.currentUser !=null){
            val profileActivityIntent = Intent(this, profileActivity::class.java)
            startActivity(profileActivityIntent)
            finish()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        signIn = GoogleSignIn.getClient(this, gso)
    }

    fun signInGoogle(view: View){
        val signInIntent = signIn.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_RC)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==SIGN_IN_RC){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try{
                    //Google Sign in was successful, authenticate with firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                }catch(e: ApiException){
                    //Fail to sign in
                }
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.login_failed)
                        .setTitle(R.string.login_failed_title)
                builder.setPositiveButton(R.string.ok_button, DialogInterface.OnClickListener { dialog, id ->
                    {
                    }
                })
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener(this){
            task-> if(task.isSuccessful){
                //sign in success
                val loggedIntent = Intent(this, profileActivity::class.java)
                startActivity(loggedIntent)
                finish()
            }else{
                //Sign in fail, display a message to the user
            }
        }
    }
}
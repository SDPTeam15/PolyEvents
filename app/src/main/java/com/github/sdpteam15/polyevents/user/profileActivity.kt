package com.github.sdpteam15.polyevents.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.google.firebase.auth.FirebaseAuth

class profileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            HelperFunctions.startActivityAndTerminate(this, loginActivity::class.java)
        }
        val token = findViewById<TextView>(R.id.displayName)
        token.setText(auth.currentUser.displayName+" " + auth.currentUser.email +" "+ auth.currentUser.uid)
    }

    fun signOut(view: View){
        auth.signOut()
        HelperFunctions.startActivityAndTerminate(this, loginActivity::class.java)
    }
}
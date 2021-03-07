package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class profileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            val loginIntent = Intent(this, loginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }
        val token = findViewById<TextView>(R.id.displayName)
        token.setText(auth.currentUser.displayName)
    }

    fun signOut(view: View){
        auth.signOut()

        val loginIntent = Intent(this, loginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}
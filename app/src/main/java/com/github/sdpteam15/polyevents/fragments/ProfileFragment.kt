package com.github.sdpteam15.polyevents.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.Companion.changeFragment
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), View.OnClickListener{
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            changeFragment(activity, LoginFragment())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewRoot =  inflater.inflate(R.layout.fragment_profile, container, false)
        viewRoot.findViewById<Button>(R.id.btnLogout).setOnClickListener(this)
        viewRoot.findViewById<TextView>(R.id.displayName).setText(auth.currentUser.displayName+" " + auth.currentUser.email +" "+ auth.currentUser.uid)

        return viewRoot
    }

    override fun onClick(v: View) {
        auth.signOut()
        changeFragment(activity, LoginFragment())
    }
}
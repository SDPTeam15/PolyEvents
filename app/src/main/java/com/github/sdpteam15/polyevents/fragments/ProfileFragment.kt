package com.github.sdpteam15.polyevents.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.auth.FirebaseAuth

/**
 *  [Fragment] subclass that represents the profile page.
 */
class ProfileFragment : Fragment() {
    //User that we can set manually for testing
    private var testUser: UserInterface? = null

    //Allow us to use a fake user for the tests
    var currentUser: UserInterface?
        get() {
            if (testUser != null) {
                return testUser
            } else {
                return User.currentUser
            }
        }
        set(value) {
            testUser = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If the user is not logged in, redirect him to the login page
        if (currentUser == null) {
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_profile, container, false)
        viewRoot.findViewById<Button>(R.id.btnLogout).setOnClickListener { _ ->
            FirebaseAuth.getInstance().signOut()
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }
        //Replace the fields in the fragment by the user informations
        viewRoot.findViewById<TextView>(R.id.displayName).setText(currentUser?.name)
        viewRoot.findViewById<TextView>(R.id.displayUID).setText(currentUser?.uid)
        viewRoot.findViewById<TextView>(R.id.displayEmail).setText(currentUser?.email)
        return viewRoot
    }
}
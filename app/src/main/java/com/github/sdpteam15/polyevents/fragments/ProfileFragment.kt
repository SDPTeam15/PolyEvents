package com.github.sdpteam15.polyevents.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.Companion.changeFragment
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.user.UserObject.CurrentUser
import com.google.firebase.auth.FirebaseAuth

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ProfileFragment : Fragment(){
    private var testUser: UserInterface?=null
    //Allow us to use a fake user for the tests
    var currentUser: UserInterface?
        get(){
            if(testUser!= null){
                return testUser
            }else{
                return CurrentUser
            }
        }
        set(value){
            testUser = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(currentUser == null){
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewRoot = inflater.inflate(R.layout.fragment_profile, container, false)

        viewRoot.findViewById<Button>(R.id.btnLogout).setOnClickListener { v ->
            FirebaseAuth.getInstance().signOut()
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }

        viewRoot.findViewById<TextView>(R.id.displayName).setText(currentUser?.Name)
        viewRoot.findViewById<TextView>(R.id.displayUID).setText(currentUser?.UID)
        viewRoot.findViewById<TextView>(R.id.displayEmail).setText(currentUser?.Email)
        return viewRoot
    }
}
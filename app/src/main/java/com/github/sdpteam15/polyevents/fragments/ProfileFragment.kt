package com.github.sdpteam15.polyevents.fragments

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.User.Companion.CurrentUser
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

/**
 *  [Fragment] subclass that represents the profile page.
 */
class ProfileFragment : Fragment(){
    //User that we can set manually for testing
    private var testUser: UserInterface?=null
    //Allow us to use a fake user for the tests
    var currentUser: UserInterface?
        get(){
            if( testUser!= null) {return testUser}
            else {return User.CurrentUser}
        }
        set(value){
            testUser = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If the user is not logged in, redirect him to the login page
        if(currentUser == null){
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_profile, container, false)
        viewRoot.findViewById<Button>(R.id.btnLogout).setOnClickListener { _ ->
            FirebaseAuth.getInstance().signOut()
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }

        val update = MutableLiveData<Boolean>()
        viewRoot.findViewById<Button>(R.id.btnUpdateInfos).setOnClickListener {
            val map = HashMap<String,String>()
            map["username"] = viewRoot.findViewById<EditText>(R.id.profileUsernameET).text.toString()
            currentDatabase.updateUserInformation(map,update,"Alessio2", CurrentUser!!)

        }
        //Replace the fields in the fragment by the user informations
        viewRoot.findViewById<EditText>(R.id.profileName).setText(currentUser?.Name)
        viewRoot.findViewById<TextView>(R.id.profileUID).setText(currentUser?.UID)
        viewRoot.findViewById<EditText>(R.id.ProfileEmail).setText(currentUser?.Email)

        val string2 = MutableLiveData<String>()
        val observer = Observer<String>{
            newValue ->  viewRoot.findViewById<TextView>(R.id.ProfileEmail).setText(newValue)
        }
        string2.observe(this,observer)

        val profileObservable = MutableLiveData<Profile>()
        val observer2 = Observer<Profile>{
                newValue ->
            run {
                viewRoot.findViewById<TextView>(R.id.ProfileEmail).setText(newValue.Name)
            }
        }

        profileObservable.observe(this,observer2)
        return viewRoot
    }
}
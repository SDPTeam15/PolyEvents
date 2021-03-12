package com.github.sdpteam15.polyevents.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.auth.FirebaseAuth

/**
 *  [Fragment] subclass that represents the profile page.
 */
class ProfileFragment : Fragment() {
    //User that we can set manually for testing
    //Return CurrentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserInterface? = null
        get() = field ?: User.currentUser

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

        //Replace the fields in the fragment by the user informations
        viewRoot.findViewById<EditText>(R.id.profileName).setText(currentUser?.name)
        viewRoot.findViewById<TextView>(R.id.profileUID).setText(currentUser?.uid)
        viewRoot.findViewById<EditText>(R.id.ProfileEmail).setText(currentUser?.email)

        val update = MutableLiveData<String>()
        viewRoot.findViewById<Button>(R.id.btnUpdateInfos).setOnClickListener {
            val map = HashMap<String,String>()
            map["username"] = viewRoot.findViewById<EditText>(R.id.profileUsernameET).text.toString()
            currentDatabase.updateUserInformation(map,update,"Alessio2", currentUser!!)
        }

        val string2 = MutableLiveData<String>()
        val observer = Observer<String>{
            newValue ->  viewRoot.findViewById<TextView>(R.id.ProfileEmail).setText(newValue)
        }
        update.observe(this,observer)

        val profileObservable = MutableLiveData<Profile>()
        val observer2 = Observer<Profile>{
                newValue ->
            run {
                viewRoot.findViewById<TextView>(R.id.ProfileEmail).setText(newValue.name)
            }
        }
        val string3 = MutableLiveData<User>()
        val getter = currentDatabase.getUserInformation(string3,"Alessio2", currentUser!!)
        getter.observe(this,Observer<Boolean>{
                newValue ->println(newValue)
        })

        profileObservable.observe(this, observer2)
        return viewRoot
    }
}
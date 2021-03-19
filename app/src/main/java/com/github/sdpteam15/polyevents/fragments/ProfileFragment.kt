package com.github.sdpteam15.polyevents.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_BIRTHDAY
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
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
    val userInfoLiveData = Observable<UserInterface>()
    val hashMapNewInfo = HashMap<String, String>()

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

        //Logout button handler
        viewRoot.findViewById<Button>(R.id.btnLogout).setOnClickListener { _ ->
            FirebaseAuth.getInstance().signOut()
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }

        //When user Info live data is updated, set the correct value in the textview
        userInfoLiveData.observe(this) { userInfo ->
            viewRoot.findViewById<EditText>(R.id.profileName).setText(userInfo!!.name)
            viewRoot.findViewById<EditText>(R.id.profileEmail).setText(userInfo!!.email)
            //TODO Line for the future when the user class will have all the attributes
            //viewRoot.findViewById<EditText>(R.id.profileUsernameET).setText(userInfo!!.username)
            //viewRoot.findViewById<EditText>(R.id.profileBirthdayET).setText(userInfo!!.birthday)
        }

        currentDatabase.getUserInformation(userInfoLiveData, currentUser!!.uid, currentUser!!)

        viewRoot.findViewById<Button>(R.id.btnUpdateInfos).setOnClickListener {
            //Clear the previous map and add every field
            hashMapNewInfo.clear()
            hashMapNewInfo[USER_USERNAME] =
                viewRoot.findViewById<EditText>(R.id.profileUsernameET).text.toString()
            hashMapNewInfo[USER_BIRTHDAY] = viewRoot.findViewById<EditText>(R.id.profileBirthdayET).text.toString()

            //Call the DB to update the user information and getUserInformation once it is done
            currentDatabase.updateUserInformation(hashMapNewInfo, currentUser!!.uid, currentUser!!)
                .observe(this) { newValue ->
                    if (newValue!!) {
                        currentDatabase.getUserInformation(
                            userInfoLiveData,
                            currentUser!!.uid,
                            currentUser!!
                        )
                    } else {
                        println("Update impossible")
                    }
                }
        }
        return viewRoot
    }
}
package com.github.sdpteam15.polyevents.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.model.UserEntity
import java.time.format.DateTimeFormatter

/**
 *  [Fragment] subclass that represents the profile page allowing the user to modify its private information
 */
class ProfileFragment : Fragment() {
    //Return currentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserEntity? = null
        get() = field ?: currentDatabase.currentUser

    val userInfoLiveData = Observable<UserEntity>()
    val hashMapNewInfo = HashMap<String, String>()
    lateinit var profileNameET: EditText
    lateinit var profileEmailET: EditText
    lateinit var profileUsernameET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //If the user is not logged in, redirect him to the login page
        if (currentUser == null) {
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_profile, container, false)
        profileNameET = viewRoot.findViewById(R.id.profileName)
        profileEmailET = viewRoot.findViewById(R.id.profileEmail)
        profileUsernameET = viewRoot.findViewById(R.id.profileUsernameET)

        //Logout button handler
        viewRoot.findViewById<Button>(R.id.btnLogout).setOnClickListener { _ ->
            UserLogin.currentUserLogin.signOut()
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }

        //When user Info live data is updated, set the correct value in the textview
        userInfoLiveData.observe(this) { userInfo ->
            profileNameET.setText(userInfo!!.name)
            profileEmailET.setText(userInfo.email)
            profileUsernameET.setText(userInfo.username)

            val userBirthDate = userInfo.value.birthDate
            val birthDateFormatted =
                if (userBirthDate == null) ""
                else userBirthDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            viewRoot.findViewById<EditText>(R.id.profileBirthdayET).setText(birthDateFormatted)
        }
        currentDatabase.getUserInformation(userInfoLiveData, currentUser!!.uid, currentUser!!)

        viewRoot.findViewById<Button>(R.id.btnUpdateInfos).setOnClickListener {
            //Clear the previous map and add every field
            hashMapNewInfo.clear()
            hashMapNewInfo[USER_USERNAME] = profileUsernameET.text.toString()
            // TODO: editText should have birthday input and convert it to Timestamp otherwise things crash
            //hashMapNewInfo[USER_BIRTH_DATE] = viewRoot.findViewById<EditText>(R.id.profileBirthdayET).text.toString()

            //Call the DB to update the user information and getUserInformation once it is done
            currentDatabase.updateUserInformation(hashMapNewInfo, currentUser!!.uid, currentUser!!)
                .observe(this) { newValue ->
                    if (newValue.value) {
                        currentDatabase.getUserInformation(
                            userInfoLiveData,
                            currentUser!!.uid,
                            currentUser!!
                        )
                    } else {
                        HelperFunctions.showToast(getString(R.string.fail_to_update), activity)
                    }
                }
        }
        return viewRoot
    }
}
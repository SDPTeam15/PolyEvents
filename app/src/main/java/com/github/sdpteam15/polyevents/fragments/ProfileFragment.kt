package com.github.sdpteam15.polyevents.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.*
import com.github.sdpteam15.polyevents.adapter.ProfileAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole
import com.google.firebase.auth.FirebaseAuth
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

    /**
     * Recycler containing all the items
     */
    lateinit var recyclerView: RecyclerView

    val profiles = ObservableList<UserProfile>()


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
            val userInfoValue = userInfo.value
            profileNameET.setText(userInfoValue.name)
            profileEmailET.setText(userInfoValue.email)
            profileUsernameET.setText(userInfoValue.username)

            val userBirthDate = userInfoValue.birthDate
            val birthDateFormatted =
                if (userBirthDate == null) ""
                else userBirthDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            viewRoot.findViewById<EditText>(R.id.profileBirthdayET).setText(birthDateFormatted)
        }
        currentDatabase.userDatabase!!.getUserInformation(userInfoLiveData, currentUser!!.uid, currentUser!!)

        viewRoot.findViewById<Button>(R.id.btnUpdateInfos).setOnClickListener {
            //Clear the previous map and add every field
            hashMapNewInfo.clear()
            hashMapNewInfo[USER_USERNAME] = profileUsernameET.text.toString()
            // TODO: editText should have birthday input and convert it to Timestamp otherwise things crash
            //hashMapNewInfo[USER_BIRTH_DATE] = viewRoot.findViewById<EditText>(R.id.profileBirthdayET).text.toString()

            //Call the DB to update the user information and getUserInformation once it is done
            currentDatabase.userDatabase!!.updateUserInformation(hashMapNewInfo, currentUser!!.uid, currentUser!!)
                .observe(this) { newValue ->
                    if (newValue.value) {
                        currentDatabase.userDatabase!!.getUserInformation(
                            userInfoLiveData,
                            currentUser!!.uid,
                            currentUser!!
                        )
                    } else {
                        HelperFunctions.showToast(getString(R.string.fail_to_update), activity)
                    }
                }
        }

        initProfileList(viewRoot)

        return viewRoot
    }

    fun initProfileList(viewRoot: View) {
        profiles.observeRemove {
            if (it.sender != currentDatabase)
                currentDatabase.userDatabase!!.removeProfile(it.value)
        }
        profiles.observeAdd {
            if (it.sender != currentDatabase)
                currentDatabase.userDatabase!!.addUserProfileAndAddToUser(it.value, currentUser!!)
        }

        recyclerView = viewRoot.findViewById(R.id.id_recycler_profile_list)
        recyclerView.adapter = ProfileAdapter(this, profiles)

        if(currentUser!!.profiles.size > 0)
            currentDatabase.userDatabase!!.getUserProfilesList(profiles, currentUser!!).observe(this) {
                if (!it.value)
                    HelperFunctions.showToast(getString(R.string.fail_to_update), activity)
            }

        viewRoot.findViewById<ImageButton>(R.id.id_add_profile_button)
            .setOnClickListener { createProfilePopup() }
    }

    private fun createProfilePopup() {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            (activity)!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.popup_profile, null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn

        // Slide animation for popup window exit transition
        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut


        // Get the widgets reference from custom view
        val profileName = view.findViewById<EditText>(R.id.id_edittext_profile_name)
        val confirmButton = view.findViewById<ImageButton>(R.id.id_confirm_add_item_button)

        //set focus on the popup
        popupWindow.isFocusable = true

        // Set a click listener for popup's button widget
        confirmButton.setOnClickListener {
            profiles.add(
                UserProfile(
                    profileName = profileName.text.toString()
                ), this
            )
            // Dismiss the popup window
            popupWindow.dismiss()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }

    var remove: () -> Boolean = { true }

    fun editProfile(item: UserProfile) {
        val intent = Intent(activity, EditProfileActivity::class.java)
        intent.putExtra(
            CALLER_RANK,
            if (currentUser!!.isAdmin()) UserRole.ADMIN.toString() else UserRole.PARTICIPANT.toString()
        )
        intent.putExtra(EDIT_PROFILE_ID, item.pid)
        startActivity(intent)
        remove = EditProfileActivity.end.observe {
            if (it.value) {
                remove()
                currentDatabase.userDatabase!!.getUserProfilesList(profiles, currentUser!!).observe(this) {
                    if (!it.value)
                        HelperFunctions.showToast(getString(R.string.fail_to_update), activity)
                }
            }
        }
    }
}
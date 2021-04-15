package com.github.sdpteam15.polyevents.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.*
import com.github.sdpteam15.polyevents.adapter.ProfileAdapter
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import java.time.format.DateTimeFormatter

/**
 *  [Fragment] subclass that represents the profile page.
 */
class ProfileFragment : Fragment() {
    //User that we can set manually for testing
    //Return CurrentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserEntity? = null
        get() = field ?: currentDatabase.currentUser
    val userInfoLiveData = Observable<UserEntity>()
    val hashMapNewInfo = HashMap<String, String>()

    /**
     * Recycler containing all the items
     */
    lateinit var recyclerView: RecyclerView

    private val profiles = ObservableList<UserProfile>()


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
            viewRoot.findViewById<EditText>(R.id.profileName).setText(userInfo.value.name)
            viewRoot.findViewById<EditText>(R.id.profileEmail).setText(userInfo.value.email)
            viewRoot.findViewById<EditText>(R.id.profileUsernameET).setText(userInfo.value.username)

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
            hashMapNewInfo[USER_USERNAME] =
                viewRoot.findViewById<EditText>(R.id.profileUsernameET).text.toString()
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

        initProfileList(viewRoot)

        return viewRoot
    }

    fun initProfileList(viewRoot: View) {
        currentDatabase.getUserProfilesList(profiles, currentUser!!).observe {
            if (!it.value)
                HelperFunctions.showToast(getString(R.string.fail_to_update), activity)
        }
        profiles.observeRemove({ (activity)!!.lifecycle })
        {
            if (it.sender != currentDatabase)
                currentDatabase.removeProfile(it.value)
        }
        profiles.observeAdd(this) {
            if (it.sender != currentDatabase)
                currentDatabase.addUserProfileAndAddToUser(it.value, currentUser!!)
        }

        recyclerView = viewRoot.findViewById(R.id.id_recycler_profile_list)
        recyclerView.adapter = ProfileAdapter(this, profiles)

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
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
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

    fun editProfile(item: UserProfile) {
        val intent = Intent(activity, EditProfileActivity::class.java)
        intent.putExtra(CALLER_RANK, if(currentUser!!.isAdmin()) UserRole.ADMIN.toString() else UserRole.PARTICIPANT.toString())
        intent.putExtra(EDIT_PROFILE_ID, item.pid)
        startActivity(intent)
    }
}
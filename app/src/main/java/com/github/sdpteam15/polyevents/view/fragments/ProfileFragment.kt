package com.github.sdpteam15.polyevents.view.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
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
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.changeFragment
import com.github.sdpteam15.polyevents.helper.NotificationsHelper
import com.github.sdpteam15.polyevents.helper.NotificationsScheduler
import com.github.sdpteam15.polyevents.model.callback.UserModifiedInterface
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.activity.EditProfileActivity
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.adapter.ProfileAdapter
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModel
import com.github.sdpteam15.polyevents.viewmodel.EventLocalViewModelFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *  [Fragment] subclass that represents the profile page allowing the user to modify its private information
 *  @param userId the id of the user we display the information or null if it is the current user of the application
 */
class ProfileFragment(private val userId: String? = null) : Fragment(), UserModifiedInterface {

    companion object {
        // for testing purposes
        lateinit var localDatabase: LocalDatabase
        lateinit var eventLocalViewModel: EventLocalViewModel
        lateinit var notificationsScheduler: NotificationsScheduler
    }

    //Return currentUser if we are not in test, but we can use a fake user in test this way
    var currentUser: UserEntity? = null
        get() = field ?: currentDatabase.currentUser

    val userInfoLiveData = Observable<UserEntity>()
    private val adminMode = userId != null
    private val obsDate = Observable<LocalDate>()
    private lateinit var profileNameET: EditText
    private lateinit var profileEmailET: EditText
    private lateinit var profileUsernameET: EditText
    private val currentUID: String
        get() = userId ?: currentUser!!.uid
    private lateinit var viewR: View

    /**
     * Recycler containing all the profile
     */
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_profile, container, false)
        //If the user is not logged in, redirect him to the login page
        if (currentUser == null) {
            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        } else {
            localDatabase = (requireActivity().application as PolyEventsApplication).localDatabase
            eventLocalViewModel = EventLocalViewModelFactory(
                localDatabase.eventDao()
            ).create(
                EventLocalViewModel::class.java
            )
            notificationsScheduler = NotificationsHelper(requireActivity().applicationContext)

            profileNameET = viewRoot.findViewById(R.id.id_profile_name_edittext)
            profileEmailET = viewRoot.findViewById(R.id.id_profile_email_edittext)
            profileUsernameET = viewRoot.findViewById(R.id.id_profile_username_edittext)

            //call method to bind the listerner and observer to the correct fields
            addListener(viewRoot)
            addObserver()

            obsDate.observe(this) {
                val europeanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

                viewRoot.findViewById<EditText>(R.id.id_profile_birthday_edittext)
                    .setText(europeanDateFormatter.format(it.value))
            }

            if (adminMode) {
                //If an admin want to see the profile of somme user
                setupAdminMode(viewRoot)
                userInfoLiveData.observe(this) {
                    initProfileList(viewRoot, it.value)
                }
            } else {
                //If not an admin, display the profile information of the current user
                setupUserMode(viewRoot)
                currentDatabase.currentUserObservable.observe(this) {
                    initProfileList(viewRoot, it.value)
                }
            }
            getUserInformation()
        }
        viewR = viewRoot
        return viewRoot
    }

    private fun getUserInformation() {
        val observableDBAnswer = Observable<Boolean>()
        //Get user information in the database
        currentDatabase.userDatabase.getUserInformation(
            userInfoLiveData,
            currentUID
        ).updateOnce(requireActivity(), observableDBAnswer)

        HelperFunctions.showProgressDialog(
            requireActivity(), listOf(
                observableDBAnswer
            ), requireActivity().supportFragmentManager
        )
    }

    /**
     * Method that will make some edit text uneditable and some button invisible for the admin
     * @param viewRoot the current view of the fragment
     */
    private fun setupAdminMode(viewRoot: View) {
        viewRoot.findViewById<Button>(R.id.id_update_infos_button).visibility = View.INVISIBLE
        viewRoot.findViewById<Button>(R.id.id_logout_button).visibility = View.INVISIBLE
        viewRoot.findViewById<Button>(R.id.id_birthday_button).visibility = View.INVISIBLE
        viewRoot.findViewById<EditText>(R.id.id_profile_birthday_edittext).isEnabled = false
        viewRoot.findViewById<EditText>(R.id.id_profile_username_edittext).isEnabled = false
    }

    /**
     * Method that will display the button and edit text needed to update the user information
     * @param viewRoot the current view of the fragment
     */
    private fun setupUserMode(viewRoot: View) {
        viewRoot.findViewById<Button>(R.id.id_update_infos_button).visibility = View.VISIBLE
        viewRoot.findViewById<Button>(R.id.id_logout_button).visibility = View.VISIBLE
        viewRoot.findViewById<Button>(R.id.id_birthday_button).visibility = View.VISIBLE
        viewRoot.findViewById<EditText>(R.id.id_profile_birthday_edittext).isEnabled = true
        viewRoot.findViewById<EditText>(R.id.id_profile_username_edittext).isEnabled = true
    }

    /**
     * Add the observers to make the fragment works properly
     */
    private fun addObserver() {
        //When user Info live data is updated, set the correct value in the textview
        userInfoLiveData.observe(this) { userInfo ->
            val userInfoValue = userInfo.value
            profileNameET.setText(userInfoValue.name)
            profileEmailET.setText(userInfoValue.email)
            profileUsernameET.setText(userInfoValue.username)
            obsDate.postValue(userInfoValue.birthDate)
        }
    }

    /**
     * Add the listener to the buttons to make the fragment works properly
     * @param viewRoot the current view of the fragment
     */
    private fun addListener(viewRoot: View) {
        viewRoot.findViewById<Button>(R.id.id_update_infos_button).setOnClickListener {
            //Clear the previous map and add every field
            currentUser!!.username = profileUsernameET.text.toString()
            if (obsDate.value != null) {
                currentUser!!.birthDate = obsDate.value
            }
            //Call the DB to update the user information and getUserInformation once it is done
            currentDatabase.userDatabase.updateUserInformation(
                currentUser!!
            ).observe(requireActivity()) { newValue ->
                if (newValue.value) {
                    val observableDBAnswer = Observable<Boolean>()
                    currentDatabase.userDatabase.getUserInformation(
                        userInfoLiveData,
                        currentUser!!.uid
                    ).updateOnce(requireActivity(), observableDBAnswer)

                    HelperFunctions.showProgressDialog(
                        requireActivity(), listOf(
                            observableDBAnswer
                        ), requireActivity().supportFragmentManager
                    )
                } else {
                    HelperFunctions.showToast(getString(R.string.fail_to_update), activity)
                }
            }
        }

        viewRoot.findViewById<Button>(R.id.id_birthday_button).setOnClickListener {
            val date = obsDate.value ?: LocalDate.now()
            val dialog = DatePickerDialog(
                requireContext(),
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    obsDate.postValue(LocalDate.of(year, month + 1, day))
                },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth
            )
            dialog.show()
        }

        //Logout button handler
        viewRoot.findViewById<Button>(R.id.id_logout_button).setOnClickListener { _ ->
            UserLogin.currentUserLogin.signOut()
            // On logout delete all user subscribed and followed events from local cache
            val eventsLocalObservable = ObservableList<EventLocal>()
            eventsLocalObservable.observe(requireActivity()) {
                it.value.forEach {
                    notificationsScheduler.cancelNotification(it.eventBeforeNotificationId)
                    notificationsScheduler.cancelNotification(it.eventStartNotificationId)
                    eventLocalViewModel.delete(it)
                }
            }
            eventLocalViewModel.getAllEvents(eventsLocalObservable)

            changeFragment(activity, MainActivity.fragments[R.id.ic_login])
        }
    }

    fun initProfileList(viewRoot: View, user: UserEntity) {
        user.userProfiles.observeRemove(this) {
            if (it.sender != currentDatabase) {
                currentDatabase.userDatabase.removeProfileFromUser(it.value, user)
                    .observeOnce(this) {
                        if (!it.value)
                            HelperFunctions.showToast(
                                getString(R.string.fail_to_remove_profiles),
                                context
                            )
                    }
            }
        }
        user.userProfiles.observeAdd(this) {
            if (it.sender != currentDatabase)
                currentDatabase.userDatabase.addUserProfileAndAddToUser(it.value, user)
                    .observeOnce(this) {
                        if (!it.value)
                            HelperFunctions.showToast(
                                getString(R.string.fail_to_add_profiles),
                                context
                            )
                    }
        }

        recyclerView = viewRoot.findViewById(R.id.id_recycler_profile_list)

        recyclerView.adapter = ProfileAdapter(this, user, user.userProfiles)

        viewRoot.findViewById<ImageButton>(R.id.id_add_profile_button)
            .setOnClickListener { createProfilePopup(user) }
    }

    @SuppressLint("InflateParams")
    private fun createProfilePopup(user: UserEntity) {
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
            user.userProfiles.add(
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
        intent.putExtra(
            EditProfileActivity.CALLER_RANK,
            if (currentUser!!.isAdmin()) UserRole.ADMIN.userRole else UserRole.PARTICIPANT.userRole
        )

        intent.putExtra(EditProfileActivity.EDIT_PROFILE_ID, item.pid.toString())
        EditProfileActivity.callback = this
        startActivity(intent)

        /*
        EditProfileActivity.end.observeOnce(this, false) {
            userInfoLiveData.value!!.loadSuccess = false
            userInfoLiveData.value!!.userProfiles.observeOnce(this) {
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }*/
    }

    override fun profileHasChanged() {
        currentDatabase.userDatabase.getUserInformation(
            userInfoLiveData,
            currentUID
        )
    }
}
package com.github.sdpteam15.polyevents.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.map.MapsFragmentMod
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.UserSettings
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import com.github.sdpteam15.polyevents.view.fragments.*
import com.github.sdpteam15.polyevents.view.fragments.home.AdminHomeFragment
import com.github.sdpteam15.polyevents.view.fragments.home.ProviderHomeFragment
import com.github.sdpteam15.polyevents.view.fragments.home.StaffHomeFragment
import com.github.sdpteam15.polyevents.view.fragments.home.VisitorHomeFragment
import com.github.sdpteam15.polyevents.view.service.TimerService
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModel
import com.github.sdpteam15.polyevents.viewmodel.UserSettingsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    companion object {
        private var mapFragment: MutableMap<Int, Fragment>? = null

        //make the fragments available from outside of the event and instantiate only once
        val fragments: Map<Int, Fragment>
            get() {
                if (mapFragment == null) {
                    mapFragment = HashMap()
                    mapFragment!![R.id.id_fragment_home_admin] = AdminHomeFragment()
                    mapFragment!![R.id.id_fragment_home_provider] = ProviderHomeFragment()
                    mapFragment!![R.id.id_fragment_home_staff] = StaffHomeFragment()
                    mapFragment!![R.id.id_fragment_home_visitor] = VisitorHomeFragment()
                    mapFragment!![R.id.ic_map] = MapsFragment(MapsFragmentMod.Visitor)
                    mapFragment!![R.id.ic_list] = EventListFragment()
                    mapFragment!![R.id.ic_login] = LoginFragment()
                    mapFragment!![R.id.ic_settings] = SettingsFragment()
                    mapFragment!![R.id.id_fragment_profile] = ProfileFragment()
                }
                //return type immutable
                return HashMap<Int, Fragment>(mapFragment as HashMap)
            }

        //Return CurrentUser if we are not in test, but we can use a fake user in test this way
        var currentUser: UserEntity? = null
            get() = field ?: currentDatabase.currentUser
        var currentUserObservable: Observable<UserEntity>? = null
            get() = field ?: currentDatabase.currentUserObservable


        var instance: MainActivity? = null
        var selectedRole: UserRole? = null

        lateinit var localDatabase: LocalDatabase
    }

    // Lazily initialized view models, instantiated only when accessed for the first time
    private val userSettingsViewModel: UserSettingsViewModel by viewModels {
        UserSettingsViewModelFactory(localDatabase.userSettingsDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_main)

        // Create notification channel for the app
        (application as PolyEventsApplication).createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        localDatabase = (application as PolyEventsApplication).database

        // Start a timed service in the background to send the device location id
        // to the database, for heatmap functionality. Have to check if the user enabled
        // sending location first in the User settings
        if (TimerService.instance.value == null) {
            // Get the user settings from local cache and create callback to update the user settings if they changed
            val userSettingsObservable = ObservableList<UserSettings>()
            userSettingsObservable.observe(this) {
                if (it.value.isNotEmpty()) {
                    userSettingsViewModel.updateUserSettings(
                        it.value[0]
                    )
                }
            }

            val intent = Intent(applicationContext, TimerService::class.java)
            startService(intent)
            TimerService.instance.observeOnce {
                it.value.addTask {
                    userSettingsViewModel.getUserSettings(userSettingsObservable)
                    if (userSettingsObservable.isNotEmpty() && userSettingsObservable[0].isSendingLocationOn) {
                        HelperFunctions.getLoc(this).observeOnce { LatLng ->
                            if (LatLng.value != null)
                                currentDatabase.heatmapDatabase!!.setLocation(
                                    LatLng.value,
                                    userSettingsObservable
                                )
                        }
                    }
                }
            }
        }

        //Set the basic fragment to the home one or to admin hub if it is logged in
        //TODO Add a condition to see if the user is an admin or not and if so, redirect him to the admin hub
        redirectHome()

        //Add a listener to the menu to switch between fragments
        findViewById<BottomNavigationView>(R.id.navigation_bar).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_map -> HelperFunctions.changeFragment(this, fragments[R.id.ic_map])
                R.id.ic_list -> HelperFunctions.changeFragment(this, fragments[R.id.ic_list])
                R.id.ic_login -> if (currentUser == null) {
                    HelperFunctions.changeFragment(this, fragments[R.id.ic_login])
                } else {
                    HelperFunctions.changeFragment(this, fragments[R.id.id_fragment_profile])
                }
                R.id.ic_settings -> HelperFunctions.changeFragment(
                    this,
                    fragments[R.id.ic_settings]
                )
                else ->
                    //TODO Add a condition to see if the user is an admin or not and if so, redirect him to the admin hub
                    redirectHome()
            }
            true
        }

        currentUserObservable!!.observe(this) {
            it.value.roles.observe(this) {
                roles.clear()
                val list = resources.getStringArray(R.array.Ranks).mapIndexed { index, value ->
                    Pair(
                        value, when (index) {
                            0 -> UserRole.ADMIN
                            1 -> UserRole.ORGANIZER
                            2 -> UserRole.STAFF
                            else -> UserRole.PARTICIPANT
                        }
                    )
                }.toMutableList()
                if (!it.value.contains(UserRole.ADMIN)) {
                    list.remove(list.find { it.second == UserRole.ADMIN })
                    if (!it.value.contains(UserRole.ORGANIZER))
                        list.remove(list.find { it.second == UserRole.ORGANIZER })
                    if (!it.value.contains(UserRole.STAFF))
                        list.remove(list.find { it.second == UserRole.STAFF })
                }
                roles.addAll(list)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(applicationContext, TimerService::class.java)
        stopService(intent)
    }

    private fun redirectHome() {
        if (currentUser == null) {
            selectedRole = UserRole.PARTICIPANT
        }
        when (selectedRole) {
            UserRole.ADMIN -> HelperFunctions.changeFragment(
                this,
                fragments[R.id.id_fragment_home_admin]
            )
            UserRole.ORGANIZER -> HelperFunctions.changeFragment(
                this,
                fragments[R.id.id_fragment_home_provider]
            )
            UserRole.STAFF -> HelperFunctions.changeFragment(
                this,
                fragments[R.id.id_fragment_home_staff]
            )
            else -> HelperFunctions.changeFragment(
                this,
                fragments[R.id.id_fragment_home_visitor]
            )
        }
    }

    private var roles = ObservableList<Pair<String, UserRole>>()
    fun switchRoles(spinner: Spinner, role: UserRole) {
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val startPosition = roles.indexOf(roles.find { it.second == role })
                if (roles.size > position && position != startPosition) {
                    selectedRole = roles[position].second
                    val tempListener = spinner.onItemSelectedListener
                    spinner.onItemSelectedListener = null
                    spinner.setSelection(startPosition)
                    spinner.onItemSelectedListener = tempListener
                    redirectHome()
                }
            }
        }
        roles.observe(this) {
            if (it.value.size > 1) {
                spinner.visibility = VISIBLE
                if (selectedRole == null) {
                    selectedRole = roles.first().second
                    redirectHome()
                }
                spinner.adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    roles.toList().map { it.first }
                )
                spinner.setSelection(it.value.indexOf(it.value.find { it.second == role }))
            } else
                spinner.visibility = INVISIBLE
        }
    }
}
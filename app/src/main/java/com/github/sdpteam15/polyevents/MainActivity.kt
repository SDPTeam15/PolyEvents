package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.fragments.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    companion object {
        private var mapFragment: MutableMap<Int, Fragment>? = null

        //make the fragments available from outside of the event and instantiate only once
        val fragments: Map<Int, Fragment>
            get() {
                if (mapFragment == null) {
                    mapFragment = HashMap()
                    mapFragment!![R.id.id_fragment_admin_hub] = AdminHubFragment()
                    mapFragment!![R.id.ic_home] = HomeFragment()
                    mapFragment!![R.id.ic_map] = MapsFragment()
                    mapFragment!![R.id.ic_list] = EventListFragment()
                    mapFragment!![R.id.ic_login] = LoginFragment()
                    mapFragment!![R.id.ic_more] = MoreFragment()
                    mapFragment!![R.id.id_fragment_profile] = ProfileFragment()
                }
                //return type immutable
                return HashMap<Int, Fragment>(mapFragment as HashMap)
            }

        //Return CurrentUser if we are not in test, but we can use a fake user in test this way
        var currentUser: UserEntity? = null
            get() = field ?: currentDatabase.currentUser

        const val NUMBER_EVENT_TO_DISPLAY = 25
    }

    private lateinit var adapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set the basic fragment to the home one or to admin hub if it is logged in
        //TODO Add a condition to see if the user is an admin or not and if so, redirect him to the admin hub
        redirectAdmin()

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
                R.id.ic_more -> HelperFunctions.changeFragment(this, fragments[R.id.ic_more])
                else ->
                    //TODO Add a condition to see if the user is an admin or not and if so, redirect him to the admin hub
                    redirectAdmin()
            }
            true
        }
/*
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.countries_array)
        )*/
        /*
        var lv_listView = findViewById<ListView>(R.id.lv_listView)
        lv_listView.adapter = adapter
        lv_listView.onItemClickListener = AdapterView.OnItemClickListener{parent, view, position, id ->
            Toast.makeText(applicationContext, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
        }
        var tv_emptyTextView = findViewById<TextView>(R.id.tv_emptyTextView)
        lv_listView.emptyView = tv_emptyTextView
        */
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val search = menu.findItem(R.id.nav_search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search something!"
        /*
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

         */

        return super.onCreateOptionsMenu(menu)
    }

    private fun redirectAdmin() {
        if (currentUser == null) {
            HelperFunctions.changeFragment(this, fragments[R.id.ic_home])
        } else {
            HelperFunctions.changeFragment(this, fragments[R.id.id_fragment_admin_hub])
        }
    }
}

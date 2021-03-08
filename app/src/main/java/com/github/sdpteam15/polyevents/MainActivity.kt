package com.github.sdpteam15.polyevents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

import android.view.Menu
import android.widget.SearchView
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.fragments.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.google.android.gms.maps.MapFragment

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<*>
    private val mapFragment:MutableMap<Int, Fragment> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment[R.id.ic_home]=HomeFragment()
        mapFragment[R.id.ic_map]=MapsFragment()
        mapFragment[R.id.ic_list]=ListFragment()
        mapFragment[R.id.ic_profile]=LoginFragment()
        mapFragment[R.id.ic_more]=MoreFragment()

        HelperFunctions.changeFragment(this, mapFragment[R.id.ic_home] as Fragment)

        var bottom_navigation = findViewById<BottomNavigationView>(R.id.navigation_bar)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_map -> HelperFunctions.changeFragment(this, mapFragment[R.id.ic_map] as Fragment)
                R.id.ic_list -> HelperFunctions.changeFragment(this, mapFragment[R.id.ic_list] as Fragment)
                R.id.ic_profile -> HelperFunctions.changeFragment(this, mapFragment[R.id.ic_profile] as Fragment)
                R.id.ic_more -> HelperFunctions.changeFragment(this, mapFragment[R.id.ic_more] as Fragment)
                else -> HelperFunctions.changeFragment(this, mapFragment[R.id.ic_home] as Fragment)
            }
            true
        }

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.countries_array)
        )
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
}

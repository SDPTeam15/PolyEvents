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

class MainActivity : AppCompatActivity() {
    companion object{
        private val mapFragment:MutableMap<Int, Fragment> = HashMap()

        val fragments:Map<Int, Fragment>
            get(){
                if(mapFragment.isEmpty()){
                    mapFragment[R.id.ic_home]=HomeFragment()
                    mapFragment[R.id.ic_map]=MapsFragment()
                    mapFragment[R.id.ic_list]=ListFragment()
                    mapFragment[R.id.ic_login]=LoginFragment()
                    mapFragment[R.id.ic_more]=MoreFragment()
                    mapFragment[R.id.id_fragment_profile]=ProfileFragment()
                }
                return HashMap<Int, Fragment>(mapFragment)
            }
    }
    private lateinit var adapter: ArrayAdapter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        HelperFunctions.changeFragment(this, fragments[R.id.ic_home])

        var bottom_navigation = findViewById<BottomNavigationView>(R.id.navigation_bar)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_map -> HelperFunctions.changeFragment(this, fragments[R.id.ic_map])
                R.id.ic_list -> HelperFunctions.changeFragment(this, fragments[R.id.ic_list])
                R.id.ic_login -> HelperFunctions.changeFragment(this, fragments[R.id.ic_login])
                R.id.ic_more -> HelperFunctions.changeFragment(this, fragments[R.id.ic_more])
                else -> HelperFunctions.changeFragment(this, fragments[R.id.ic_home])
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

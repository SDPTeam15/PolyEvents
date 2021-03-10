package com.github.sdpteam15.polyevents.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.ActivityActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.activity.ActivityItemAdapter
import com.github.sdpteam15.polyevents.activity.Datasource

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataset = Datasource().loadUpcomingActivities()
        val fragmentView = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView = fragmentView.findViewById<RecyclerView>(R.id.recycler_view)

        val openActivity = {activity : Activity ->
            val intent = Intent(inflater.context, ActivityActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, activity.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = ActivityItemAdapter(dataset,openActivity)


        recyclerView.setHasFixedSize(true)
        // Inflate the layout for this fragment
        return fragmentView
        }
    }

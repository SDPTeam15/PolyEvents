package com.github.sdpteam15.polyevents.view.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.NUMBER_UPCOMING_EVENTS
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication.Companion.inTest
import com.github.sdpteam15.polyevents.view.activity.EventActivity
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.github.sdpteam15.polyevents.view.activity.TimeTableActivity
import com.github.sdpteam15.polyevents.view.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID

/**
 * The fragment for the home page.
 */
class VisitorHomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    val events = ObservableList<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        // Check if the user is connected and with a rank which is not participant.
        // If so, display a spinner to change its current role
        // Otherwise, hide the spinner
        val bool =
            UserLogin.currentUserLogin.isConnected() && currentDatabase.currentUser!!.userProfiles.fold(
                false, { a, c ->
                    if (a) {
                        a
                    } else {
                        c.userRole.ordinal < UserRole.PARTICIPANT.ordinal
                    }
                })
        requireActivity().findViewById<Spinner>(R.id.spinner_visitor).visibility =
            if (bool) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_home_visitor, container, false)
        recyclerView = fragmentView.findViewById(R.id.id_recycler_upcomming_events)

        val openEvent = { event: Event ->
            val intent = Intent(inflater.context, EventActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, event.eventId)
            }
            startActivity(intent)
        }

        recyclerView.adapter = EventItemAdapter(events, openEvent)
        recyclerView.setHasFixedSize(false)

        val observableDBAnswer = Observable<Boolean>()
        // Get all events from database
        currentDatabase.eventDatabase.getEvents(events, NUMBER_UPCOMING_EVENTS.toLong())
            .observe(this) {
                if (!it.value) {
                    HelperFunctions.showToast(
                        getString(R.string.failed_to_load_events),
                        fragmentView.context
                    )
                }
            }.then.updateOnce(this, observableDBAnswer)


        HelperFunctions.showProgressDialog(
            requireActivity(),
            listOf(observableDBAnswer),
            requireActivity().supportFragmentManager
        )

        events.observe(this) {
            recyclerView.adapter!!.notifyDataSetChanged()
        }

        if (!inTest)
            HelperFunctions.getLocationPermission(requireActivity())
        MainActivity.instance!!.switchRoles(
            fragmentView!!.findViewById(R.id.spinner_visitor),
            UserRole.PARTICIPANT
        )

        fragmentView.findViewById<Button>(R.id.id_timetable_button).setOnClickListener {
            val intent = Intent(activity, TimeTableActivity::class.java)
            startActivity(intent)
        }

        return fragmentView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) = HelperFunctions.onRequestPermissionsResult(requestCode, permissions, grantResults)
}
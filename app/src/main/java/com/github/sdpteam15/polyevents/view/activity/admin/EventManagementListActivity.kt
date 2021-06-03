package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.DatabaseHelper.deleteEvent
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.EventListAdapter

class EventManagementListActivity : AppCompatActivity() {
    companion object {
        // The constant used to show that we want to create a new event
        const val NEW_EVENT_ID = "-1"

        // The corresponding intent name
        const val EVENT_ID_INTENT = "EVENT_ID"

        // Intent name to indicate that we want the organiser list of events and not all the list
        const val ORGANISER_LIST = "ORGANISER LIST"

        // Intent name to indicate that EventManagementActivity has been launched in "Activity provider" mode
        const val INTENT_MANAGER = "MANAGER"

        // Intent name to indicate that EventManagementActivity has been launched in "Activity provider" mode and we want to edit an already existing event edit request
        const val INTENT_MANAGER_EDIT = "EDIT_MANAGER"
    }

    private lateinit var recyclerView: RecyclerView
    private var obsEventsMap = ObservableMap<String, Pair<String, ObservableList<Event>>>()
    private lateinit var modifyListener: (String) -> Unit
    private lateinit var deleteListener: (String, Event) -> Unit
    private var isOrganiser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // see if we should show the organiser's event list or if an admin launch the activity
        isOrganiser = intent.hasExtra(ORGANISER_LIST)

        // Create the different listeners
        setupListeners()

        // Setup the recycler
        recyclerView = findViewById(R.id.recycler_events_list_admin)
        recyclerView.adapter =
            EventListAdapter(this, this, isOrganiser, obsEventsMap, modifyListener, deleteListener)
        recyclerView.setHasFixedSize(false)

        // Add the lister on the create button
        findViewById<ImageButton>(R.id.btnNewEvent).setOnClickListener {
            val intent = Intent(this, EventManagementActivity::class.java)
            if (isOrganiser) {
                intent.putExtra(INTENT_MANAGER, "MANAGER")
            }

            intent.putExtra(EVENT_ID_INTENT, NEW_EVENT_ID)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // If we go back to (or launch) the activity, load the list of events from the database
        val requestObservable = ObservableList<Event>()
        // Insert the list of events to the observable map that will be displayed on the recycler view
        requestObservable.group(this) { it.zoneId }.then.observe {
            obsEventsMap.clear()
            for (i in it.value.entries) {
                obsEventsMap[i.key!!] = Pair(i.value[0].zoneName!!, i.value)
            }
        }
        getEventsDatabase(requestObservable)
    }


    /**
     * Method that we will get from the database the correct list of events
     * @param requestObservable The observable list in which the events retrieved from the database will be insert
     */
    private fun getEventsDatabase(requestObservable: ObservableList<Event>) {
        if (isOrganiser) {
            // If the current user is an event organiser and not admin, we display only the events he is organising
            currentDatabase.eventDatabase.getEvents({
                it.whereEqualTo(
                    DatabaseConstant.EventConstant.EVENT_ORGANIZER.value,
                    currentDatabase.currentUser!!.uid
                )
            }, null, eventList = requestObservable)
                .observe(this) {
                    redirectOnFailure(it.value)
                }
        } else {
            // Otherwise, we load all the events from the database
            currentDatabase.eventDatabase.getEvents(null, null, eventList = requestObservable)
                .observe(this) {
                    redirectOnFailure(it.value)
                }
        }
    }

    /**
     * If the argument is false, close the activity with an error message
     * @param success If the operation was successful or not
     */
    private fun redirectOnFailure(success: Boolean) {
        if (!success) {
            HelperFunctions.showToast(getString(R.string.fail_retrieve_events), this)
            finish()
        }
    }

    /**
     * Create all the listener needed by the adapter
     */
    private fun setupListeners() {
        // Build a listener that will open EventManagement activity to modify the event
        modifyListener = { s: String ->
            val intent = Intent(this, EventManagementActivity::class.java)
            if (isOrganiser) {
                intent.putExtra(INTENT_MANAGER, "INTENT_MANAGER")
            }
            intent.putExtra(EVENT_ID_INTENT, s)
            startActivity(intent)
        }

        // build an alert dialog so that a confirmation of deletion is displayed before actually delete the message
        deleteListener = { zoneId: String, event: Event ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.message_confirm_delete, event.eventName))
                .setPositiveButton(getString(R.string.event_deletion_yes_button_text)) { _, _ ->
                    eventDeletion(event, zoneId)
                }.setNegativeButton(getString(R.string.event_deletion_no_button_text)) { _, _ -> }
            builder.show()
            Unit
        }
    }

    /**
     * Handle the event deletion
     * @param event The event we want to delete
     * @param zoneId The zoneId where the deleted event happen
     */
    private fun eventDeletion(event: Event, zoneId: String) {
        deleteEvent(event)
        // Remove the event in the current recycler view
        val list = obsEventsMap[zoneId]!!
        list.second.remove(event)
        obsEventsMap[zoneId] = list
    }
}
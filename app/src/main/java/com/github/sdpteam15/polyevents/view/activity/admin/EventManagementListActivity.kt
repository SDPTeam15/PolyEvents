package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.EventListAdapter

class EventManagementListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var obsEventsMap = ObservableMap<String, Pair<String, ObservableList<Event>>>()
    private lateinit var modifyListener: (String) -> Unit
    private lateinit var deleteListener: (String, Event) -> Unit

    companion object {
        const val NEW_EVENT_ID = "-1"
        const val EVENT_ID_INTENT = "EVENT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setupListeners()

        recyclerView = findViewById(R.id.recycler_events_list_admin)
        recyclerView.adapter =
            EventListAdapter(this, this, obsEventsMap, modifyListener, deleteListener)
        recyclerView.setHasFixedSize(false)

        val requestObservable = ObservableList<Event>()
        requestObservable.group(this) { it.zoneId }.then.observe {
            obsEventsMap.clear()
            for (i in it.value.entries) {
                obsEventsMap[i.key!!] = Pair(i.value[0].zoneName!!, i.value)
            }
        }

        currentDatabase.eventDatabase!!.getEvents(null, null, eventList = requestObservable)
            .observe(this) {
                if (!it.value) {
                    HelperFunctions.showToast(getString(R.string.fail_retrieve_events), this)
                    finish()
                }
            }

        findViewById<Button>(R.id.btnNewEvent).setOnClickListener {
            val intent = Intent(this, EventManagementActivity::class.java)
            intent.putExtra(EVENT_ID_INTENT, NEW_EVENT_ID)
            startActivity(intent)
        }
    }

    private fun setupListeners() {
        modifyListener = { s: String ->
            val intent = Intent(this, EventManagementActivity::class.java)
            intent.putExtra(EVENT_ID_INTENT, s)
            startActivity(intent)
        }

        deleteListener = { zoneId: String, event: Event ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure to want to delete " + event.eventName + " (this action is not reversible)")
                .setPositiveButton(getString(R.string.event_deletion_yes_button_text)) { _, _ ->
                    currentDatabase.eventDatabase!!.removeEvent(event.eventId!!).observe(this) {
                        if (it.value) {
                            HelperFunctions.showToast(
                                getString(R.string.success_delete_from_database),
                                this
                            )
                            val list = obsEventsMap[zoneId]!!
                            list.second.remove(event)
                            obsEventsMap[zoneId] = list
                        } else {
                            HelperFunctions.showToast(
                                getString(R.string.fail_delete_from_database),
                                this
                            )
                        }
                    }
                }.setNegativeButton(getString(R.string.event_deletion_no_button_text)) { _, _ -> }
            builder.show()
            Unit
        }
    }
}
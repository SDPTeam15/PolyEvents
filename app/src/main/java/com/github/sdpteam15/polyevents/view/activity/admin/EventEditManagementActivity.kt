package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.EventEditAdminAdapter
import com.github.sdpteam15.polyevents.view.fragments.admin.EventEditDifferenceFragment

class EventEditManagementActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val eventEdits = ObservableList<Event>()
    private val origEvents = ObservableMap<String, Event>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_edit_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_event_edits)
        recyclerView.adapter =
            EventEditAdminAdapter(
                this,
                this,
                eventEdits,
                origEvents,
                acceptEventEditRequest,
                declineEventEdit,
                seeEventEdit
            )


        val eventList = ObservableList<Event>()

        eventList.observeAdd(this) {
            val event = it.value
            origEvents[event.eventId!!] = event
        }

        Database.currentDatabase.eventDatabase!!.getEvents(null, null, eventList)
            .observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to get the list of all events", this)
                    finish()
                } else {
                    getEventEdit()
                }
            }
    }

    private fun getEventEdit() {
        //Wait until we have both requests accepted from the database to show the material requests
        Database.currentDatabase.eventDatabase!!.getEventEdits(
            { collection ->
                collection.orderBy(DatabaseConstant.EventEditConstant.EVENT_EDIT_STATUS.value)
            },
            eventEdits,
        ).observeOnce(this) {
            if (!it.value) {
                HelperFunctions.showToast("Failed to get the list of material requests", this)
                finish()
            }
        }
    }

    private val seeEventEdit = { event: Event, creation: Boolean, eventOrig: Event? ->
        val fragment = EventEditDifferenceFragment(event, creation, eventOrig)
        fragment.show(supportFragmentManager, EventEditDifferenceFragment.TAG)
    }

    private fun acceptEventEditCallback(success: Boolean, event: Event) {
        if (!success) {
            HelperFunctions.showToast("Failed to accept the request", this)
        } else {
            eventEdits.set(
                eventEdits.indexOfFirst { it2 -> it2.eventEditId == event.eventEditId },
                event,
                this
            )
        }
    }

    private val acceptEventEditRequest = { event: Event ->
        if (event.status == Event.EventStatus.PENDING) {
            event.status = Event.EventStatus.ACCEPTED

            Database.currentDatabase.eventDatabase!!.updateEventEdit(
                event
            ).observeOnce(this) {
                acceptEventEditCallback(it.value, event)
            }.then.observeOnce(this) {
                if (it.value) {
                    if (event.eventId == null) {
                        Database.currentDatabase.eventDatabase!!.createEvent(
                            event
                        ).observeOnce(this) {
                            acceptEventEditCallback(it.value, event)
                        }
                    } else {
                        Database.currentDatabase.eventDatabase!!.updateEvent(
                            event
                        ).observeOnce(this) {
                            acceptEventEditCallback(it.value, event)
                        }
                    }
                }else{
                    HelperFunctions.showToast("The activity edit cannot be requested", this)
                }
            }

            Unit
        } else {
            HelperFunctions.showToast("Can not accept this request", this)
        }
    }

    private val declineEventEdit = { event: Event ->
        createRefusalPopup(event)
    }

    private fun createRefusalPopup(event: Event) {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.popup_refuse_request, null)

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
        val confirmButton = view.findViewById<Button>(R.id.id_btn_confirm_refuse_request)
        val message = view.findViewById<TextView>(R.id.id_txt_refusal_explanation)

        //set focus on the popup
        popupWindow.isFocusable = true

        // Set a click listener for popup's button widget
        confirmButton.setOnClickListener {
            event.status = Event.EventStatus.REFUSED
            event.adminMessage = message.text.toString()

            Database.currentDatabase.eventDatabase!!.updateEventEdit(
                event
            ).observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to decline the request", this)
                } else {
                    eventEdits.set(
                        eventEdits.indexOfFirst { it2 -> it2.eventEditId == event.eventEditId },
                        event,
                        this
                    )
                }
            }
            // Dismiss the popup window
            popupWindow.dismiss()
        }

        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }
}
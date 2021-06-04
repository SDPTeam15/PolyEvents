package com.github.sdpteam15.polyevents.view.activity.admin

import android.annotation.SuppressLint
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
import com.github.sdpteam15.polyevents.helper.DatabaseHelper.addToUsersFromDB
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdminAdapter

/**
 * Activity to display item requests to Admins
 */
class ItemRequestManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val requests = ObservableList<MaterialRequest>()
    private val userNames = ObservableMap<String, String>()
    private val itemNames = ObservableMap<String, String>()
    private val zoneNameFromEventId = ObservableMap<String, String>()
    private val items = ObservableList<Triple<Item, Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request_management)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_item_requests)

        requests.observeAdd(this) {
            if (!userNames.containsKey(it.value.userId)) {
                addToUsersFromDB(it.value.userId, userNames, this, this)
            }
            if (it.value.staffInChargeId != null && !userNames.containsKey(it.value.staffInChargeId!!)) {
                addToUsersFromDB(it.value.staffInChargeId!!, userNames, this, this)
            }
        }

        items.group(this) { it.first.itemId!! }.then.map(this, itemNames) {
            it[0].first.itemName!!
        }

        recyclerView.adapter =
            ItemRequestAdminAdapter(
                this,
                this,
                requests,
                userNames,
                itemNames,
                zoneNameFromEventId,
                acceptMaterialRequest,
                declineMaterialRequest
            )

        //Wait until we have both requests accepted from the database to show the material requests
        currentDatabase.materialRequestDatabase.getMaterialRequestList(
            requests.sortAndLimitFrom(this) { it.status })
            .observeOnce(this) { it ->
                if (!it.value) {
                    HelperFunctions.showToast("Failed to get the list of material requests", this)
                } else {
                    currentDatabase.itemDatabase.getItemsList(items)
                        .observeOnce(this) { it2 ->
                            if (!it2.value) {
                                HelperFunctions.showToast("Failed to get the list of items", this)
                            }
                        }
                    val sentEventIds = mutableListOf<String>()
                    for (request in requests) {
                        if (request.eventId !in sentEventIds) {
                            sentEventIds.add(request.eventId)
                            val event = Observable<Event>()
                            val zone = Observable<Zone>()
                            currentDatabase.eventDatabase.getEventFromId(request.eventId, event)
                                .observeOnce(this) {
                                    if (it.value) {
                                        currentDatabase.zoneDatabase.getZoneInformation(
                                            event.value!!.zoneId!!,
                                            zone
                                        ).observeOnce(this) {
                                            if (it.value) {
                                                zoneNameFromEventId[event.value!!.eventId!!] =
                                                    zone.value!!.zoneName!!
                                            }
                                        }
                                    }
                                }
                        }

                    }
                }
            }
    }

    private val acceptMaterialRequest = { request: MaterialRequest ->

        if (canAccept(request)) {
            request.status = MaterialRequest.Status.ACCEPTED
            val obs = ArrayList<Observable<Boolean>>()
            obs.add(
                currentDatabase.materialRequestDatabase.updateMaterialRequest(
                    request.requestId!!,
                    request
                ).observeOnce(this) {
                    if (!it.value) {
                        HelperFunctions.showToast("Failed to accept the request", this)
                    } else {
                        requests.set(
                            requests.indexOfFirst { it2 -> it2.requestId == request.requestId },
                            request,
                            this
                        )
                    }
                }.then
            )

            for (item in request.items) {
                val oldItem = items.first { it.first.itemId == item.key }
                obs.add(
                    currentDatabase.itemDatabase.updateItem(
                        oldItem.first,
                        oldItem.second,
                        oldItem.third - item.value
                    )
                )
            }
            // Display a loading screen while the queries with the database are not over
            HelperFunctions.showProgressDialog(
                this, obs, supportFragmentManager
            )

        } else {
            HelperFunctions.showToast("Can not accept this request", this)
        }
    }

    /**
     * Checks if the material request can be accepted
     */
    private fun canAccept(materialRequest: MaterialRequest): Boolean {
        return materialRequest.status == MaterialRequest.Status.PENDING && materialRequest.items.all {
            items.first { it2 -> it2.first.itemId == it.key }.third >= it.value
        }
    }

    private val declineMaterialRequest = { request: MaterialRequest ->
        createRefusalPopup(request)
    }

    @SuppressLint("InflateParams")
    private fun createRefusalPopup(request: MaterialRequest) {
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
            request.status = MaterialRequest.Status.REFUSED
            request.adminMessage = message.text.toString()

            val updateEnded = Observable<Boolean>()

            currentDatabase.materialRequestDatabase.updateMaterialRequest(
                request.requestId!!,
                request
            ).observeOnce(this) {
                if (!it.value) {
                    HelperFunctions.showToast("Failed to decline the request", this)
                } else {
                    requests.set(
                        requests.indexOfFirst { it2 -> it2.requestId == request.requestId },
                        request,
                        this
                    )
                }
            }.then.updateOnce(this, updateEnded)

            // Display a loading screen while the queries with the database are not over
            HelperFunctions.showProgressDialog(
                this, listOf(
                    updateEnded
                ), supportFragmentManager
            )

            // Dismiss the popup window
            popupWindow.dismiss()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }

}
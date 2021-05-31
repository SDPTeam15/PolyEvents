package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider.currentUser
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.activityprovider.EXTRA_ITEM_REQUEST_ID
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdapter
import java.time.LocalDateTime

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var eventSpinner: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: Button
    var mapSelectedItems = ObservableMap<Item, Int>()
    var obsItemsMap = ObservableMap<String, ObservableMap<Item, Pair<Int, Int>>>()
    private var requestId: String? = null
    private val selectedEvent = Observable<Event>()
    var listEvent = ObservableList<Event>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestId = intent.getStringExtra(EXTRA_ITEM_REQUEST_ID)


        recyclerView = findViewById(R.id.id_recycler_items_request)
        sendButton = findViewById(R.id.id_button_make_request)
        sendButton.setOnClickListener { sendItemsRequest() }

        eventSpinner = findViewById(R.id.id_spinner_event)

        val requestObservable = ObservableList<Triple<Item, Int, Int>>()
        requestObservable
            .group(this) { it.first.itemType }.then
            .map(this, obsItemsMap) {
                it.group(this) { it2 -> it2.first }.then
                    .map(this) { it2 -> Pair(it2[0].second, it2[0].third) }.then
            }

        currentDatabase.eventDatabase!!.getEvents({
            it.whereEqualTo(
                DatabaseConstant.EventConstant.EVENT_ORGANIZER.value,
                currentDatabase.currentUser!!.uid
            )
        }, null, listEvent).observeOnce(this) {
            if (it.value) {
                if (listEvent.isEmpty()) {
                    showToast(getString(R.string.create_event_before_items), this)
                    finish()
                } else {
                    if (selectedEvent.value == null) {
                        selectedEvent.postValue(listEvent[0])
                    }

                    eventSpinner.adapter =
                        ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            listEvent.map (this){ it.eventName }.then
                        )

                    eventSpinner.setSelection(listEvent.indexOfFirst { it.eventId == selectedEvent.value!!.eventId })
                    eventSpinner.onItemSelectedListener = this
                }
            } else {
                showToast(getString(R.string.fail_to_get_event_list), this)
                finish()
            }
        }
        currentDatabase.itemDatabase!!.getAvailableItems(requestObservable).observeOnce(this) {
            if (it.value) {
                if (requestId != null) {
                    val request = Observable<MaterialRequest>()
                    currentDatabase.materialRequestDatabase!!.getMaterialRequestById(
                        request,
                        requestId!!
                    ).observeOnce(this) { it2 ->
                        if (it2.value) {
                            currentDatabase.eventDatabase!!.getEventFromId(
                                request.value!!.eventId,
                                selectedEvent
                            )

                            for (item in request.value!!.items) {
                                val tripleIndex =
                                    requestObservable.indexOfFirst { itemTriple -> itemTriple.first.itemId == item.key }
                                if (tripleIndex != -1)

                                    mapSelectedItems[requestObservable[tripleIndex].first] =
                                        item.value
                            }
                        }
                    }
                }
                recyclerView.adapter =
                    ItemRequestAdapter(
                        this,
                        this,
                        obsItemsMap,
                        mapSelectedItems
                    )
                recyclerView.setHasFixedSize(false)
            }

        }
    }


    /**
     * Send the items request to the admins
     * and display a short message confirming the request was sent.
     * This redirect to the Main activity.
     * If the request is empty, display an error message.
     */
    private fun sendItemsRequest() {
        when {
            mapSelectedItems.isEmpty() -> {
                showToast(getString(R.string.item_request_empty_text), this)
            }
            selectedEvent.value == null -> {
                showToast(getString(R.string.event_not_selected), this)
            }
            else -> {
                if (requestId == null) {

                    currentDatabase.materialRequestDatabase!!.createMaterialRequest(
                        MaterialRequest(
                            null,
                            mapSelectedItems.keys.map { Pair(it.itemId!!, mapSelectedItems[it]!!) }
                                .toMap(),
                            LocalDateTime.now(),
                            currentDatabase.currentUser?.uid ?: "",
                            selectedEvent.value!!.eventId!!,
                            MaterialRequest.Status.PENDING,
                            null,
                            null
                        )
                    ).observeOnce {
                        if(it.value){
                            showToast(getString(R.string.item_request_sent_text), this)
                        }
                    }

                } else {
                    currentDatabase.materialRequestDatabase!!.updateMaterialRequest(
                        requestId!!,
                        MaterialRequest(
                            requestId,
                            mapSelectedItems.keys.map { Pair(it.itemId!!, mapSelectedItems[it]!!) }
                                .toMap(),
                            LocalDateTime.now(),
                            currentDatabase.currentUser?.uid ?: "",
                            selectedEvent.value!!.eventId!!,
                            MaterialRequest.Status.PENDING,
                            null,
                            null
                        )
                    ).observeOnce {
                        if(it.value){
                            showToast(getString(R.string.item_request_updated), this)
                        }
                    }
                }
                // Go back to previous activity
                finish()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedEvent.postValue(listEvent[position])
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}
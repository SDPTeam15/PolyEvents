package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.activity.activityprovider.EXTRA_ITEM_REQUEST_ID
import com.github.sdpteam15.polyevents.view.adapter.ItemRequestAdapter
import java.time.LocalDateTime

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: Button
    var mapSelectedItems = ObservableMap<Item, Int>()
    var obsItemsMap = ObservableMap<String, ObservableMap<Item, Pair<Int, Int>>>()
    private var requestId: String? = null
    var mapZoneNames = ObservableMap<String,String>()
    lateinit var selectedZoneId : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestId = intent.getStringExtra(EXTRA_ITEM_REQUEST_ID)


        recyclerView = findViewById(R.id.id_recycler_items_request)
        sendButton = findViewById(R.id.id_button_make_request)
        sendButton.setOnClickListener { sendItemsRequest() }

        val requestObservable = ObservableList<Triple<Item, Int, Int>>()
        requestObservable
            .group(this) { it.first.itemType }.then
            .map(this, obsItemsMap) {
                it.group(this) { it2 -> it2.first }.then
                    .map(this) { it2 -> Pair(it2[0].second, it2[0].third) }.then
            }
        val tempZones = ObservableList<Zone>()
        currentDatabase.zoneDatabase!!.getAllZones(null,null,tempZones).observe (this){
            if(it.value){
                tempZones.group (this){ it2 -> it2.zoneId!! }.then.map (this, mapZoneNames) { it2->it2[0].zoneName?:"UNKNOWN LOCATION" }
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
        if (mapSelectedItems.isEmpty()) {
            showToast(getString(R.string.item_request_empty_text), this)
        } else {
            if (requestId == null){

                currentDatabase.materialRequestDatabase!!.createMaterialRequest(
                    MaterialRequest(
                        null,
                        mapSelectedItems.keys.map { Pair(it.itemId!!, mapSelectedItems[it]!!) }
                            .toMap(),
                        LocalDateTime.now(),
                        currentDatabase.currentUser?.uid ?: "",
                        selectedZoneId,
                        MaterialRequest.Status.PENDING,
                        null,
                        null
                    )
                )
            showToast(getString(R.string.item_request_sent_text), this)
            }else{
                currentDatabase.materialRequestDatabase!!.updateMaterialRequest(
                    requestId!!,
                    MaterialRequest(
                        requestId,
                        mapSelectedItems.keys.map { Pair(it.itemId!!, mapSelectedItems[it]!!) }
                            .toMap(),
                        LocalDateTime.now(),
                        currentDatabase.currentUser?.uid ?: "",
                        selectedZoneId,
                        MaterialRequest.Status.PENDING,
                        null,
                        null
                    )
                )
                showToast("Your request has been successful updated", this)
            }
            // Go back to previous activity
            finish()
        }
    }
}
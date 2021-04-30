package com.github.sdpteam15.polyevents.veiw.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.veiw.adapter.ItemRequestAdapter
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import java.time.LocalDateTime

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: Button
    var mapSelectedItems = ObservableMap<Item, Int>()
    var obsItemsMap = ObservableMap<String, ObservableMap<Item, Int>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_items_request)
        sendButton = findViewById(R.id.id_button_make_request)
        sendButton.setOnClickListener { sendItemsRequest() }

        val requestObservable = ObservableList<Pair<Item, Int>>()
        requestObservable
            .group(this) { it.first.itemType }.then
            .map(this, obsItemsMap) {
                it.group(this) { it2 -> it2.first }.then
                    .map(this) { it2 -> it2[0].second }.then
            }
        currentDatabase.itemDatabase!!.getAvailableItems(requestObservable).observe(this) {
            if (it.value) {
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

            currentDatabase.materialRequestDatabase!!.createMaterialRequest(
                MaterialRequest(
                    null, mapSelectedItems.keys.map { Pair(it.itemId!!, mapSelectedItems[it]!!) }.toMap(),
                    LocalDateTime.now(),
                    currentDatabase.currentUser?.uid ?: ""
                )
            )
            showToast(getString(R.string.item_request_sent_text), this)

            // Go back to previous activity
            finish()
        }
    }
}
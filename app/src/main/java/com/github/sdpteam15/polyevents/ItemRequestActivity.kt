package com.github.sdpteam15.polyevents

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.adapter.ItemRequestAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.database.observe.ObservableMap
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.MaterialRequest
import java.time.LocalDateTime

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    var mapSelectedItems = ObservableMap<Item, Int>()
    var obsItemsMap = ObservableMap<String, ObservableMap<Item, Int>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_items_request)


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
     * Send the items request to the admins (TODO : actually send it)
     * and display a short message confirming the request was sent.
     * This redirect to the Main activity.
     * If the request is empty, display an error message.
     * @param view : the button clicked
     */
    fun sendItemsRequest(view: View) {
        if (mapSelectedItems.isEmpty()) {
            showToast(getString(R.string.item_request_empty_text), this)
        } else {
            // TODO : send the request through the db interface
            // sendRequest(listSelectedItems)
            currentDatabase.materialRequestDatabase!!.createMaterialRequest(
                MaterialRequest(
                    null, mapSelectedItems,
                    LocalDateTime.now(),
                    currentDatabase.currentUser?.uid ?: ""
                )
            )
            showToast(getString(R.string.item_request_sent_text), this)

            // Go back to previous activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
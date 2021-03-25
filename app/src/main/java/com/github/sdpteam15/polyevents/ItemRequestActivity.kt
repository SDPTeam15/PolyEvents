package com.github.sdpteam15.polyevents

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.item_request.ItemRequestAdapter
import com.github.sdpteam15.polyevents.model.Item

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var mapSelectedItems: MutableMap<Item, Int>
    private val obsItems = ObservableList<Pair<Item,Int>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_items_request)
        mapSelectedItems = mutableMapOf()

        currentDatabase.getAvailableItems(obsItems)

        // Listener that update the map of selected items when the quantity is changed
        val onItemQuantityChangeListener = { item: Item, newQuantity: Int ->
            when {
                mapSelectedItems.containsKey(item) and (newQuantity == 0) -> {
                    mapSelectedItems.remove(item)
                }
                newQuantity > 0 -> {
                    mapSelectedItems[item] = newQuantity
                }
            }
            Unit
        }

        recyclerView.adapter =
            ItemRequestAdapter(obsItems, onItemQuantityChangeListener)

        recyclerView.setHasFixedSize(false)
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

            showToast(getString(R.string.item_request_sent_text), this)

            // Go back to previous activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
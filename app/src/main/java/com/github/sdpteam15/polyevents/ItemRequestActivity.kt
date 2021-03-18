package com.github.sdpteam15.polyevents

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.item_request.ItemRequestAdapter

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var mapSelectedItems: MutableMap<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_items_request)
        mapSelectedItems = mutableMapOf()

        // TODO : Adapt that stub and put it in the db interface
        val availableItems = currentDatabase.getAvailableItems()

        val onItemQuantityChangeListener = { item: String, newQuantity: Int ->
            when {
                mapSelectedItems.containsKey(item) and (newQuantity == 0) -> {
                    mapSelectedItems.remove(item)
                }
                newQuantity > 0 -> {
                    mapSelectedItems[item] = newQuantity
                }
                else -> {
                }
            }
            Unit
        }

        recyclerView.adapter =
            ItemRequestAdapter(availableItems.toList(), onItemQuantityChangeListener)

        recyclerView.setHasFixedSize(false)
    }

    fun sendItemsRequest(view: View) {
        if (mapSelectedItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.item_request_empty_text), Toast.LENGTH_LONG)
                .show()
        } else {
            // TODO : send the request through the db interface
            // sendRequest(listSelectedItems)

            Toast.makeText(this, getString(R.string.item_request_sent_text), Toast.LENGTH_LONG)
                .show()

            // Go back to previous activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
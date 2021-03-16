package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.item_request.ItemRequestAdapter

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var mapSelectedItems: HashMap<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_items_request)
        mapSelectedItems = HashMap()

        // TODO : Remove that stub and put it in the db interface
        val availableItems = listOf(
            Pair("230V plug", 2),
            Pair("Cord rewinder (50m)", 3),
            Pair("Cooking plate", 5),
            Pair("Cord rewinder (100m)", 1),
            Pair("Cord rewinder (10m)", 30),
            Pair("Fridge (large)", 2),
            Pair("Fridge (small)", 10)
        )

        // Listener to keep the selected items in a list
        val onItemCheckChangeListener = { item: Pair<String, Int>, isChecked: Boolean ->
            if (isChecked) {
                val ret = mapSelectedItems.put(item.first, item.second)
            } else {
                val ret = mapSelectedItems.remove(item.first)
            }
        }

        val onItemQuantityChangeListener = {item: String, newQuantity: Int ->
            if(mapSelectedItems.containsKey(item)) {
                val ret = mapSelectedItems.put(item, newQuantity)
            }
        }

        recyclerView.adapter = ItemRequestAdapter(availableItems, onItemCheckChangeListener, onItemQuantityChangeListener)

        recyclerView.setHasFixedSize(false)
    }

    fun sendItemsRequest(view: View) {
        if (mapSelectedItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.item_request_empty_text), Toast.LENGTH_SHORT)
                .show()
        } else {
            // TODO : send the request through the db interface
            // sendRequest(listSelectedItems)

            Toast.makeText(this, getString(R.string.item_request_sent_text), Toast.LENGTH_SHORT)
                .show()

            // Go back to previous activity
            // val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
        }
    }
}
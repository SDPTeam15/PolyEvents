package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

        // Listener to keep the selected items in a list
        val onItemCheckChangeListener =
            { item: Pair<String, Int>, isChecked: Boolean, setQuantity: Int ->
                if (isChecked and (setQuantity > 0)) {
                    mapSelectedItems[item.first] = setQuantity
                } else {
                    mapSelectedItems.remove(item.first)
                }
                // Otherwise the above map operations return an Int
                Unit
        }

        val onItemQuantityChangeListener = {item: String, newQuantity: Int ->
            if(mapSelectedItems.containsKey(item)) {
                mapSelectedItems[item] = newQuantity
            }
        }

        recyclerView.adapter = ItemRequestAdapter(availableItems.toList(), onItemCheckChangeListener, onItemQuantityChangeListener)

        recyclerView.setHasFixedSize(false)
    }

    fun sendItemsRequest(view: View) {
        if (mapSelectedItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.item_request_empty_text), Toast.LENGTH_SHORT)
                .show()
        } else {
            // TODO : send the request through the db interface
            // sendRequest(listSelectedItems)

            Toast.makeText(this, getString(R.string.item_request_sent_text) + mapSelectedItems.size.toString(), Toast.LENGTH_SHORT)
                .show()

            // Go back to previous activity
            // val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
        }
    }
}
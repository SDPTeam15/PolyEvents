package com.github.sdpteam15.polyevents

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.adapter.ItemRequestAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.Item

/**
 * An activity containing items available for request
 */
class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var mapSelectedItems: MutableMap<Item, Int>
    private val obsItems = ObservableList<Pair<Item, Int>>()
    private val obsItemsMap : MutableMap<String,ObservableList<Pair<Item, Int>>> = mutableMapOf()
    private val obsItemTypes = ObservableList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.id_recycler_items_request)
        mapSelectedItems = mutableMapOf()


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
        currentDatabase.itemDatabase!!.getAvailableItems(obsItems).observe {
            if (it.value) {
                currentDatabase.itemDatabase!!.getItemTypes(obsItemTypes).observe { it2 ->
                    if (it2.value){
                        for (item in obsItems){
                            val type = item.first.itemType
                            if (!obsItemsMap.containsKey(type))
                                obsItemsMap[type] = ObservableList()
                            obsItemsMap[type]!!.add(item)
                        }
                        recyclerView.adapter =
                            ItemRequestAdapter(this,obsItemTypes,obsItemsMap, onItemQuantityChangeListener)
                        recyclerView.setHasFixedSize(false)
                    }
                }
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

            showToast(getString(R.string.item_request_sent_text), this)

            // Go back to previous activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
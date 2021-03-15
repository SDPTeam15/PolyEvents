package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.fragments.MoreFragment
import com.github.sdpteam15.polyevents.item_request.ItemRequestAdapter

class ItemRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listSelectedItems: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_request)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById<RecyclerView>(R.id.id_recycler_items_request)
        listSelectedItems = ArrayList<String>()

        // TODO : Remove that stub and put it in the db interface
        val availableItems = listOf(
            "230V plug",
            "Cord rewinder (50m)",
            "Cooking plate",
            "Cord rewinder (100m)",
            "Cord rewinder (10m)",
            "Fridge (large)",
            "Fridge (small)"
        )

/*        val openActivity = { activity: Activity ->
            val intent = Intent(inflater.context, ActivityActivity::class.java).apply {
                putExtra(EXTRA_ACTIVITY, activity.id)
            }
            startActivity(intent)
        }*/

        val onItemCheckChangeListener = {item: String, isChecked: Boolean ->
            if(isChecked) {
                listSelectedItems.add(item)
            } else {
                listSelectedItems.remove(item)
            }
        }

        recyclerView.adapter = ItemRequestAdapter(availableItems, onItemCheckChangeListener)

        recyclerView.setHasFixedSize(false)
    }

    fun sendItemsRequest(view: View) {
        if(listSelectedItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.item_request_empty_text), Toast.LENGTH_SHORT).show()
        } else {
            // TODO : send the request through the db interface
            // sendRequest(listSelectedItems)

            Toast.makeText(this, getString(R.string.item_request_sent_text), Toast.LENGTH_SHORT).show()

            // Go back to previous activity
            // val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
        }
    }
}
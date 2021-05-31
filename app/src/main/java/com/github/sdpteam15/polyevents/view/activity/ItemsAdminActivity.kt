package com.github.sdpteam15.polyevents.view.activity

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.ItemAdapter

/**
 * Activity displaying Items and supports items creation and deletion
 */
class ItemsAdminActivity : AppCompatActivity() {

    private val items = ObservableList<Triple<Item, Int, Int>>()
    private val itemTypes = ObservableList<String>()

    /**
     * Recycler containing all the items
     */
    private lateinit var recyclerView: RecyclerView

    private fun getItemsFromDB() {
        currentDatabase.itemDatabase!!.getItemsList(
            items,
            //we consider objects with 0
            { it.whereNotEqualTo(DatabaseConstant.ItemConstants.ITEM_TOTAL.value, 0) })
            .observe(this) {
                if (!it.value)
                    showToast(getString(R.string.failed_to_get_item), this)
            }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_admin)

        getItemsFromDB()
        currentDatabase.itemDatabase!!.getItemTypes(itemTypes).observe(this) {
            if (!it.value)
                showToast(getString(R.string.query_not_satisfied), this)
        }
        items.observeRemove(this) {
            if (it.sender != currentDatabase) {
                if (it.value.first.itemId != null) {
                    currentDatabase.itemDatabase!!.updateItem(it.value.first, 0, 0)
                }
            }
        }
        items.observeAdd(this) {
            if (it.sender != currentDatabase) {
                currentDatabase.itemDatabase!!.createItem(it.value.first, it.value.second)
                    .observeOnce { it2 ->
                        if (it2.value != "") {
                            items[it.index].first.itemId = it2.value
                        } else {
                            showToast(getString(R.string.fail_to_add_items), this)
                        }
                    }
            }
        }

        itemTypes.observeAdd(this) {
            if (it.sender != currentDatabase) {
                currentDatabase.itemDatabase!!.createItemType(it.value)
            }
        }

        val modifyItem = { item: Triple<Item, Int, Int> ->
            createItemPopup(item)
        }
        val deleteItem = { item: Triple<Item, Int, Int> ->
            //check if there if there is no accepted request with the requested item
            if (item.second != item.third) {
                showToast(getString(R.string.item_already_in_use), this)
            }else{
                items.remove(item)
            }
            Unit
        }

        recyclerView = findViewById(R.id.id_recycler_items_list)
        recyclerView.adapter = ItemAdapter(this, items, modifyItem, deleteItem)

        val btnAdd = findViewById<ImageButton>(R.id.id_add_item_button)
        btnAdd.setOnClickListener { createItemPopup(null) }
    }


    private fun createItemPopup(item: Triple<Item, Int, Int>?) {
        // Initialize a new layout inflater instance
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.popup_add_item, null)

        // Initialize a new instance of popup window
        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn

        // Slide animation for popup window exit transition
        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut


        // Get the widgets reference from custom view
        val itemName = view.findViewById<EditText>(R.id.id_edittext_item_name)
        val confirmButton = view.findViewById<ImageButton>(R.id.id_confirm_add_item_button)
        val itemQuantity = view.findViewById<EditText>(R.id.id_edittext_item_quantity)
        val itemTypeTextView = view.findViewById<AutoCompleteTextView>(R.id.id_edittext_item_type)
        var itemUsed = 0
        if (item != null) {
            itemName.setText(item.first.itemName)
            itemQuantity.setText(item.second.toString())
            itemTypeTextView.setText(item.first.itemType)
            itemUsed = item.second - item.third

        }


        //set adapter to show available item types
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, itemTypes)
        itemTypeTextView.setAdapter(adapter)

        // update adapter when a new itemType is added
        itemTypes.observe(this) { adapter.notifyDataSetChanged() }

        //set focus on the popup
        popupWindow.isFocusable = true

        // Set a click listener for popup's button widget
        confirmButton.setOnClickListener {
            if (itemName.text.toString().trim() != "" &&
                itemQuantity.text.toString().trim() != "" &&
                itemTypeTextView.text.toString().trim() != ""
            ) {
                val newTotal = itemQuantity.text.toString().toInt()
                when {
                    newTotal <= 0 -> {
                        showToast(getString(R.string.quantity_should_be_positive), this)
                    }
                    itemUsed > newTotal -> {
                        showToast(getString(R.string.new_total_less_items), this)

                    }
                    else -> {

                        val itemType = itemTypeTextView.text.toString()

                        // add new item Type if not already present
                        if (itemType !in itemTypes) {
                            itemTypes.add(itemType)
                        }
                        if (item == null) {
                            // add new item
                            items.add(
                                Triple(
                                    Item(null, itemName.text.toString(), itemType),
                                    newTotal,
                                    newTotal
                                ), this
                            )
                        } else {
                            //Modify item
                            currentDatabase.itemDatabase!!.updateItem(
                                Item(
                                    item.first.itemId,
                                    itemName.text.toString(),
                                    itemType
                                ), newTotal,
                                newTotal - itemUsed
                            ).observe { it1 ->
                                if (it1.value) {
                                    getItemsFromDB()
                                }
                            }
                        }
                        // Dismiss the popup window
                        popupWindow.dismiss()
                    }
                }
            } else {
                showToast(getString(R.string.fill_all_fields), this)
            }
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }


}
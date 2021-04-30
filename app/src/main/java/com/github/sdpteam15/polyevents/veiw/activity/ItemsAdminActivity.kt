package com.github.sdpteam15.polyevents.veiw.activity

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.veiw.adapter.ItemAdapter
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.Item

/**
 * Activity displaying Items and supports items creation and deletion
 */
class ItemsAdminActivity : AppCompatActivity() {

    private val items = ObservableList<Pair<Item, Int>>()
    private val itemTypes = ObservableList<String>()

    /**
     * Recycler containing all the items
     */
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_admin)


        currentDatabase.itemDatabase!!.getItemsList(items).observe(this) {
            if (!it.value)
                HelperFunctions.showToast(getString(R.string.failed_to_get_item),this)

        }
        currentDatabase.itemDatabase!!.getItemTypes(itemTypes).observe(this) {
            if (!it.value)
                HelperFunctions.showToast(getString(R.string.query_not_satisfied), this)
        }
        items.observeRemove(this) {
            if (it.sender != currentDatabase.itemDatabase!!) {
                currentDatabase.itemDatabase!!.removeItem(it.value.first.itemId!!)
            }
        }
        items.observeAdd(this) {
            if (it.sender != currentDatabase.itemDatabase!!) {
                currentDatabase.itemDatabase!!.createItem(it.value.first, it.value.second)
                    .observe { it1 ->
                        if (it1.value) {
                            currentDatabase.itemDatabase!!.getItemsList(items)
                        }
                    }
            }
        }

        itemTypes.observeAdd(this) {
            if (it.sender != currentDatabase.itemDatabase!!) {
                currentDatabase.itemDatabase!!.createItemType(it.value)
            }
        }

        val modifyItem = { item: Pair<Item, Int> ->
            createItemPopup(item)
        }

        recyclerView = findViewById(R.id.id_recycler_items_list)
        recyclerView.adapter = ItemAdapter(this, items, modifyItem)

        val btnAdd = findViewById<ImageButton>(R.id.id_add_item_button)
        btnAdd.setOnClickListener { createItemPopup(null) }
    }


    private fun createItemPopup(item: Pair<Item, Int>?) {
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

        if(item != null){
            itemName.setText(item.first.itemName)
            itemQuantity.setText(item.second.toString())
            itemTypeTextView.setText(item.first.itemType)
        }


        //set adapter to show available item types
        val adapter = ArrayAdapter(this, android.R.layout.select_dialog_item, itemTypes)
        itemTypeTextView.setAdapter(adapter)

        // update adapter when a new itemType is added
        itemTypes.observe(this) { adapter.notifyDataSetChanged() }

        //set focus on the popup
        popupWindow.isFocusable = true

        // Set a click listener for popup's button widget
        confirmButton.setOnClickListener{
            val itemType = itemTypeTextView.text.toString()

            // add new item Type if not already present
            if (itemType !in itemTypes) {
                itemTypes.add(itemType)
            }
            if(item == null){
                // add new item
                items.add(
                        Pair(
                                Item(null, itemName.text.toString(), itemType),
                                itemQuantity.text.toString().toInt()
                        ), this
                )
            }else{
                //Modify item
                currentDatabase.itemDatabase!!.updateItem(Item(item.first.itemId, itemName.text.toString(), itemType), itemQuantity.text.toString().toInt()).observe { it1 ->
                    if (it1.value) {
                        currentDatabase.itemDatabase!!.getItemsList(items)
                    }
                }
            }
            // Dismiss the popup window
            popupWindow.dismiss()
        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }


}
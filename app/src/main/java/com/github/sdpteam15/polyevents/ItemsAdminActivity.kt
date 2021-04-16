package com.github.sdpteam15.polyevents

import android.content.Context
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.adapter.ItemAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType

/**
 * Activity displaying Items and supports items creation and deletion
 */
class ItemsAdminActivity : AppCompatActivity() {

    private val items = ObservableList<Pair<Item, Int>>()

    /**
     * Recycler containing all the items
     */
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_admin)


        currentDatabase.itemDatabase!!.getItemsList(items).observe(this) {
            if (!it.value)
                println("query not satisfied")
        }
        items.observeRemove(this) {
            if (it.sender != currentDatabase.itemDatabase!!)
                currentDatabase.itemDatabase!!.removeItem(it.value.first.itemId!!)
        }
        items.observeAdd(this) {
            if (it.sender!= currentDatabase.itemDatabase!!) {
                currentDatabase.itemDatabase!!.createItem(it.value.first, it.value.second).observe { it1 ->
                    if (it1.value) {
                        currentDatabase.itemDatabase!!.getItemsList(items)
                    }
                }
            }
        }

        recyclerView = findViewById(R.id.id_recycler_items_list)
        recyclerView.adapter = ItemAdapter(this, items)

        val btnAdd = findViewById<ImageButton>(R.id.id_add_item_button)
        btnAdd.setOnClickListener { createAddItemPopup() }
    }

    private fun createAddItemPopup() {
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

        //set focus on the popup
        popupWindow.isFocusable = true

        // Set a click listener for popup's button widget
        confirmButton.setOnClickListener {


            items.add(
                Pair(
                    Item(null, itemName.text.toString(), ItemType.OTHER),
                    itemQuantity.text.toString().toInt()
                ), this
            )
            // Dismiss the popup window
            popupWindow.dismiss()

        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }
}
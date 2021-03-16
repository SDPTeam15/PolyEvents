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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.item_request.ItemAdapter

class ItemsAdminActivity : AppCompatActivity() {

    lateinit var items: MutableLiveData<MutableList<String>>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_admin)

        // TODO take existing items from the database

        items = MutableLiveData()

        items.postValue(
            mutableListOf(
                "Scie-tronconneuse",
                "Bout de bois",
                "Feu",
                "Masque team Philippe",
                "Micro Yeti",
                "Led RGB",
                "Bouteille de Pergola"
            )
        )

        val clickListener = { _: String -> }
        recyclerView = findViewById(R.id.id_recycler_items_request)
        recyclerView.adapter = ItemAdapter(items, clickListener)

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

        //set focus on the popup
        popupWindow.isFocusable = true

        // Set a click listener for popup's button widget
        confirmButton.setOnClickListener {
            // Dismiss the popup window
            val newList = items.value
            val newItem = itemName.text.toString()
            newList!!.add(newItem)
            items.postValue(newList)
            popupWindow.dismiss()

        }


        // Finally, show the popup window on app
        TransitionManager.beginDelayedTransition(this.recyclerView)
        popupWindow.showAtLocation(this.recyclerView, Gravity.CENTER, 0, 0)
    }
}
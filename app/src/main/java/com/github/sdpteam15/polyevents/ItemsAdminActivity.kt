package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.widget.ImageButton
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
        btnAdd.setOnClickListener {
            val temp = items.value
            temp?.add("Saucisse")
            items.postValue(temp)
        }
    }
}
package com.github.sdpteam15.polyevents.item_request

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.FakeDatabase

class ItemAdapter(
    // TODO ADD private var data: MutableLiveData<MutableList<String>>,
    // TODO ADD private val onItemClickListener: (String) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    /*TODO ADD
        private val observer = Observer<MutableList<String>> { t ->
            items = t!!
            notifyDataSetChanged()
        }
    */
    init {
        //TODO ADD data.observeForever(observer)
    }

    /**
     * adapted ViewHolder for each event
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_list_item_name)
        private val btnRemove = view.findViewById<ImageButton>(R.id.id_remove_item)

        /**
         * Binds the values of each field of an event to the layout of an event
         */
        fun bind(item: String) {
            btnRemove.setOnClickListener {
                Database.currentDatabase.removeItem(item)
                // TODO ADD data.postValue(items)
                notifyDataSetChanged()
            }
            itemName.text = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_material_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        println("SALE O " + (currentDatabase == FakeDatabase))
        val item = currentDatabase.getItemsList()[position]
        holder.bind(item)
        /*
        holder.itemView.setOnClickListener {

            onItemClickListener(item)
        }*/
    }

    override fun getItemCount(): Int {
        return Database.currentDatabase.getItemsList().size
    }
}


package com.github.sdpteam15.polyevents.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.database.observe.ObservableMap
import com.github.sdpteam15.polyevents.model.MaterialRequest

/**
 * Recycler Adapter for the list of items
 * Shows each item with its available quantity
 * @param context context of parent view used to inflate new views
 * @param lifecycleOwner parent to enable observables to stop observing when the lifecycle is closed
 * @param requests List of all item requests
 * @param userNames Username map to retrieve usernames given their userid
 */
class ItemRequestAdminAdapter(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val requests: ObservableList<MaterialRequest>,
    private val userNames: ObservableMap<String, Observable<String>>,
    private val itemNames: ObservableMap<String, String>,
) : RecyclerView.Adapter<ItemRequestAdminAdapter.ItemViewHolder>() {
    val adapterLayout =  LayoutInflater.from(context)
    init {

        requests.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
        itemNames.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
        userNames.observe (lifecycleOwner){
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding event view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {



        private val organizer = view.findViewById<TextView>(R.id.id_request_organiser)
        private val time = view.findViewById<TextView>(R.id.id_request_time)
        private val itemList = view.findViewById<TextView>(R.id.id_request_item_list)
        private val btnAccept = view.findViewById<ImageButton>(R.id.id_request_accept)
        private val btnRefuse = view.findViewById<ImageButton>(R.id.id_request_refuse)

        /**
         * Binds the values of each view of an event to the layout of an event
         */
        fun bind(request: MaterialRequest) {
            organizer.text = userNames[request.userId]!!.value
            time.text = request.time.toString()
            itemList.text = request.items.map { itemNames[it.key] + ":" +it.value }.joinToString(separator = "\n") { it }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemRequestAdminAdapter.ItemViewHolder {

        val view = adapterLayout.inflate(R.layout.card_material_request, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemRequestAdminAdapter.ItemViewHolder, position: Int) {
        val item = requests[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
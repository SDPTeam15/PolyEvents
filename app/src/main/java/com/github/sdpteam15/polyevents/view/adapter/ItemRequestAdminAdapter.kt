package com.github.sdpteam15.polyevents.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import java.time.format.DateTimeFormatter

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
    private val userNames: ObservableMap<String, String>,
    private val itemNames: ObservableMap<String, String>,
    private val onAcceptListener: (MaterialRequest) -> Unit,
    private val onRefuseListener: (MaterialRequest) -> Unit
) : RecyclerView.Adapter<ItemRequestAdminAdapter.ItemViewHolder>() {
    private val adapterLayout = LayoutInflater.from(context)

    init {

        requests.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
        itemNames.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
        userNames.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    /**
     * adapted ViewHolder for each item
     * Takes the corresponding material request view
     */
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        private val organizer = view.findViewById<TextView>(R.id.id_request_organiser)
        private val time = view.findViewById<TextView>(R.id.id_request_time)
        private val itemList = view.findViewById<TextView>(R.id.id_request_item_list)
        private val btnAccept = view.findViewById<ImageButton>(R.id.id_request_accept)
        private val btnRefuse = view.findViewById<ImageButton>(R.id.id_request_refuse)
        private val status = view.findViewById<TextView>(R.id.id_request_status)

        /**
         * Binds the values of each value of a material request to a view
         */
        @SuppressLint("SetTextI18n")
        fun bind(request: MaterialRequest) {
            organizer.text = userNames[request.userId]
            time.text =
                request.time!!.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + request.time.format(
                    DateTimeFormatter.ISO_LOCAL_TIME
                ).subSequence(0, 5)
            itemList.text = request.items.map { itemNames[it.key] + " : " + it.value }
                .joinToString(separator = "\n") { it }
            status.setTextColor(when(request.status){
                MaterialRequest.Status.ACCEPTED -> Color.GREEN
                MaterialRequest.Status.PENDING -> Color.BLACK
                MaterialRequest.Status.REFUSED -> Color.RED
                else -> Color.BLACK //should never happen
            })
            status.text = request.status.toString()

            btnAccept.visibility = if (request.status == MaterialRequest.Status.PENDING) VISIBLE else INVISIBLE
            btnRefuse.visibility = if (request.status == MaterialRequest.Status.PENDING) VISIBLE else INVISIBLE
            btnRefuse.setOnClickListener { onRefuseListener(request) }
            btnAccept.setOnClickListener { onAcceptListener(request) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view = adapterLayout.inflate(R.layout.card_material_request, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = requests[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}
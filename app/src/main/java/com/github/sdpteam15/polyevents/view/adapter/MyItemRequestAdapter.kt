package com.github.sdpteam15.polyevents.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R

import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
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
class MyItemRequestAdapter(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val requests: ObservableMap<MaterialRequest.Status, ObservableList<MaterialRequest>>,
    private val typeToDisplay: Observable<MaterialRequest.Status>,
    private val userNames: String?,
    private val itemNames: ObservableMap<String, String>,
    private val onModifyListener: (MaterialRequest) -> Unit,
    private val onCancelListener: (MaterialRequest) -> Unit,
    private val onReturnRequest: (MaterialRequest) -> Unit
) : RecyclerView.Adapter<MyItemRequestAdapter.ItemViewHolder>() {
    private val adapterLayout = LayoutInflater.from(context)

    init {

        requests.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }

        itemNames.observe(lifecycleOwner) {
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
        private val btnModify = view.findViewById<ImageButton>(R.id.id_modify_request)
        private val btnCancel = view.findViewById<ImageButton>(R.id.id_delete_request)
        private val btnReturn = view.findViewById<ImageButton>(R.id.id_return_request)
        private val status = view.findViewById<TextView>(R.id.id_request_status)
        private val adminMessage = view.findViewById<TextView>(R.id.id_admin_message)
        private val refusalLayout = view.findViewById<LinearLayout>(R.id.id_reason_of_refusal)

        /**
         * Binds the values of each value of a material request to a view
         */
        @SuppressLint("SetTextI18n")
        fun bind(request: MaterialRequest) {
            organizer.text = userNames
            time.text =
                request.time!!.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + request.time.format(
                    DateTimeFormatter.ISO_LOCAL_TIME
                ).subSequence(0, 5)
            itemList.text = request.items.map { itemNames[it.key] + " : " + it.value }
                .joinToString(separator = "\n") { it }
            status.setTextColor(when(request.status){
                ACCEPTED -> Color.GREEN
                PENDING -> Color.BLACK
                REFUSED -> Color.RED
                DELIVERING -> Color.CYAN
                DELIVERED -> Color.GREEN
                RETURN_REQUESTED -> Color.CYAN
                RETURNING -> Color.CYAN
                RETURNED -> Color.GREEN
                CANCELED -> Color.LTGRAY
            })
            status.text = request.status.toString()

            btnModify.visibility = if (request.status == PENDING) VISIBLE else INVISIBLE
            btnCancel.visibility = if (request.status == PENDING) VISIBLE else INVISIBLE
            btnReturn.visibility = if (request.status == DELIVERED) VISIBLE else INVISIBLE
            btnCancel.setOnClickListener { onCancelListener(request) }
            btnModify.setOnClickListener { onModifyListener(request) }
            btnReturn.setOnClickListener { onReturnRequest(request) }
            refusalLayout.visibility = if(request.adminMessage != null) VISIBLE else GONE
            adminMessage.text = request.adminMessage
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {

        val view = adapterLayout.inflate(R.layout.card_my_material_request, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = requests[typeToDisplay.value]!![position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return requests[typeToDisplay.value]?.size ?: 0
    }
}
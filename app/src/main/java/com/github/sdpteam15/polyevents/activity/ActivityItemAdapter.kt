package com.github.sdpteam15.polyevents.activity

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R

/**
 * Adapts activities to RecyclerView's ItemViewHolders
 * Takes :
 * - The list of activities to adapt
 * - A listener that will be triggered on click of an ItemViewHolder element
 */
class ActivityItemAdapter(
    private val activities: List<Activity>,
    private val listener: (Activity) -> Unit
) : RecyclerView.Adapter<ActivityItemAdapter.ItemViewHolder>() {

    /**
     * adapted ViewHolder for each activity
     * Takes the corresponding activity view
     */
    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val activityName = view.findViewById<TextView>(R.id.id_activity_name_text)
        private val activitySchedule = view.findViewById<TextView>(R.id.id_activity_schedule_text)
        private val activityZone = view.findViewById<TextView>(R.id.id_activity_zone)
        private val activityIcon = view.findViewById<ImageView>(R.id.id_activity_icon)

        /**
         * Binds the values of each field of an activity to the layout of an activity
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(activity: Activity) {
            activityName.text = activity.name
            activitySchedule.text = "at ${activity.getTime()}"
            activityZone.text = activity.zone

            // TODO : set the icon of the activity
            //activityIcon.setImageBitmap(activity.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_activity, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
        holder.itemView.setOnClickListener {
            listener(activity)
        }
    }

    override fun getItemCount(): Int {
        return activities.size
    }
}



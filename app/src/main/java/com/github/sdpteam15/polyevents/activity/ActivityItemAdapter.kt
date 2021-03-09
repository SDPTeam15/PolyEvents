package com.github.sdpteam15.polyevents.activity

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R

class ActivityItemAdapter (
    private val dataset: List<Activity>) : RecyclerView.Adapter<ActivityItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val activityName = view.findViewById<TextView>(R.id.id_activity_name_text)
        val activitySchedule = view.findViewById<TextView>(R.id.id_activity_schedule_text)
        val activityZone = view.findViewById<TextView>(R.id.id_activity_zone)
        val activityIcon = view.findViewById<ImageView>(R.id.id_activity_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        println("salut create")
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_activity, parent, false)
        println(adapterLayout)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        println("bind")
        val activity = dataset[position]
        holder.activityName.text = activity.name
        holder.activitySchedule.text = "at ${activity.getTime()}"
        holder.activityZone.text = activity.zone
        // TODO : set the icon of the activity
        //activityIcon.setImageBitmap(activity.icon)

    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
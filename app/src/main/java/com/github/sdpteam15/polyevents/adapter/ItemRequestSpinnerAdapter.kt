package com.github.sdpteam15.polyevents.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.Item

class ItemRequestSpinnerAdapter(private val context : Context, private val items : MutableList<Pair<Item, Int>>): BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return if(convertView == null){
            val newView = LayoutInflater.from(parent!!.context).inflate(R.layout.card_material_item,parent)
            newView.findViewById<TextView>(R.id.id_item_name).text = items[position].first.itemName
            newView
        }else{
            convertView.findViewById<TextView>(R.id.id_item_name).text = items[position].first.itemName
            convertView
        }
    }
}
package com.github.sdpteam15.polyevents.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.observable.ObservableList

/**
 * Adapts events to RecyclerView's CommentItemAdapter
 * @param events The list of comments to adapt
 */
class CommentItemAdapter(
    private val comments: ObservableList<Rating>
) : RecyclerView.Adapter<CommentItemAdapter.ItemViewHolder>() {

    /**
     * adapted ViewHolder for each comment
     * Takes the corresponding comment view
     */
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar_comment)
        private val comment = view.findViewById<TextView>(R.id.id_comment)

        /**
         * Binds the values of each field of a comment to the layout of a comment
         */
        fun bind(rating: Rating) {
            ratingBar.rating = rating.rate!!.toFloat()
            comment.text = rating.feedback
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_comment, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = comments[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return comments.size
    }
}
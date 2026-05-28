package com.example.crunchyroll_pemvis_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewAdapter(private var reviews: List<Map<String, Any>>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtUsername: TextView = view.findViewById(R.id.txt_review_username)
        val txtRating: TextView = view.findViewById(R.id.txt_review_rating)
        val txtComment: TextView = view.findViewById(R.id.txt_review_comment)
        val txtDate: TextView = view.findViewById(R.id.txt_review_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.txtUsername.text = review["username"] as? String ?: "Pengguna"
        
        val ratingVal = (review["ratingStars"] as? Number)?.toDouble() ?: 5.0
        holder.txtRating.text = "⭐ %.1f".format(ratingVal)
        holder.txtComment.text = review["comment"] as? String ?: ""

        val timestamp = review["timestamp"] as? com.google.firebase.Timestamp
        if (timestamp != null) {
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
            holder.txtDate.text = sdf.format(timestamp.toDate())
        } else {
            holder.txtDate.text = ""
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun updateData(newReviews: List<Map<String, Any>>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}

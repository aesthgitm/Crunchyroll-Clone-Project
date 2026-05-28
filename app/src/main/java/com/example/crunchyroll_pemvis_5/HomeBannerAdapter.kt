package com.example.crunchyroll_pemvis_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeBannerAdapter(
    private val bannerList: List<AnimeModel>,
    private val onPlayClick: (AnimeModel) -> Unit,
    private val onItemClick: (AnimeModel) -> Unit
) : RecyclerView.Adapter<HomeBannerAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bannerImage: ImageView = view.findViewById(R.id.banner_image)
        val bannerTitle: TextView = view.findViewById(R.id.banner_title)
        val bannerSubtitle: TextView = view.findViewById(R.id.banner_subtitle)
        val bannerAgeRating: TextView = view.findViewById(R.id.banner_age_rating)
        val bannerDubSub: TextView = view.findViewById(R.id.banner_dub_sub)
        val bannerDescription: TextView = view.findViewById(R.id.banner_description)
        val btnPlay: View = view.findViewById(R.id.btn_play_banner)
        val btnWatchlist: View = view.findViewById(R.id.btn_watchlist_banner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_banner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = bannerList[position]
        holder.bannerTitle.text = item.title
        holder.bannerSubtitle.text = item.subtitle
        holder.bannerAgeRating.text = item.ageRating
        holder.bannerDubSub.text = item.dubSubText
        holder.bannerDescription.text = item.description

        if (item.imageResId != null) {
            holder.bannerImage.setImageResource(item.imageResId)
        } else {
            holder.bannerImage.setImageResource(android.R.color.transparent)
            holder.bannerImage.setBackgroundColor(item.placeholderColor)
        }

        holder.btnPlay.setOnClickListener {
            onPlayClick(item)
        }

        holder.btnWatchlist.setOnClickListener {
            // Can be extended, for now behaves like item click/watchlist addition
            onItemClick(item)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = bannerList.size
}

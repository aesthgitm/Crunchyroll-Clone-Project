package com.example.crunchyroll_pemvis_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load // IMPORT COIL

class AnimePosterAdapter(
    private var animeList: List<AnimeModel>,
    private val onItemClick: (AnimeModel) -> Unit
) : RecyclerView.Adapter<AnimePosterAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val posterImageContainer: View = view.findViewById(R.id.poster_image_container)
        val posterImage: ImageView = view.findViewById(R.id.poster_image)
        val posterTitle: TextView = view.findViewById(R.id.poster_title)
        val posterSubtitle: TextView = view.findViewById(R.id.poster_subtitle)
        val btnMore: ImageView = view.findViewById(R.id.btn_poster_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime_poster, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = animeList[position]
        holder.posterTitle.text = item.title
        holder.posterSubtitle.text = item.dubSubText
        
        // SINKRONISASI IMAGE INTERNET: Memasang URL Gambar ke adapter list horizontal
        if (item.imageUrl.isNotEmpty()) {
            holder.posterImage.load(item.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_circle_bookmark)
                error(R.drawable.bg_circle_bookmark)
            }
            holder.posterImageContainer.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        } else {
            holder.posterImage.setImageResource(android.R.color.transparent)
            holder.posterImageContainer.setBackgroundColor(item.placeholderColor)
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        
        holder.btnMore.setOnClickListener { v ->
            val context = v.context
            val popup = androidx.appcompat.widget.PopupMenu(context, v)
            popup.menu.add("Tambahkan ke Daftar Tonton")
            popup.menu.add("Tonton Sekarang")
            popup.menu.add("Bagikan")
            popup.menu.add("Tandai sebagai Sudah Ditonton")
            popup.setOnMenuItemClickListener { menuItem ->
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                when (menuItem.title) {
                    "Tambahkan ke Daftar Tonton" -> {
                        val isBookmarked = MockData.watchlist.any { it.id == item.id }
                        if (isBookmarked) {
                            Toast.makeText(context, "${item.title} sudah di Daftar Tonton", Toast.LENGTH_SHORT).show()
                        } else {
                            MockData.watchlist.add(item)
                            if (userId.isNotEmpty()) {
                                FirestoreHelper().addToWatchlist(userId, item.id) { }
                            }
                            Toast.makeText(context, "${item.title} ditambahkan ke Daftar Tonton", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "Tonton Sekarang" -> {
                        MockData.watchHistory.removeIf { it.id == item.id }
                        MockData.watchHistory.add(0, item)
                        if (userId.isNotEmpty()) {
                            FirestoreHelper().addToWatchHistory(userId, item.id) { }
                        }
                        Toast.makeText(context, "Memutar: ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                    "Bagikan" -> {
                        Toast.makeText(context, "Membagikan ${item.title}...", Toast.LENGTH_SHORT).show()
                    }
                    "Tandai sebagai Sudah Ditonton" -> {
                        MockData.watchHistory.removeIf { it.id == item.id }
                        MockData.watchHistory.add(0, item)
                        if (userId.isNotEmpty()) {
                            FirestoreHelper().addToWatchHistory(userId, item.id) { }
                        }
                        Toast.makeText(context, "Ditandai sebagai sudah ditonton: ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount() = animeList.size

    fun updateList(newList: List<AnimeModel>) {
        animeList = newList
        notifyDataSetChanged()
    }
}
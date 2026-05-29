package com.example.crunchyroll_pemvis_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import coil.load // IMPORT COIL

class AnimeListAdapter(
    private var animeList: List<AnimeModel>,
    private val onItemClick: (AnimeModel) -> Unit
) : RecyclerView.Adapter<AnimeListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gridImage: ImageView = view.findViewById(R.id.grid_image)
        val gridCard: View = view.findViewById(R.id.grid_card)
        val gridTitle: TextView = view.findViewById(R.id.grid_title)
        val gridSubtitle: TextView = view.findViewById(R.id.grid_subtitle)
        val btnGridMore: ImageView = view.findViewById(R.id.btn_grid_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime_grid, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = animeList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = animeList[position]
        holder.gridTitle.text = item.title
        holder.gridSubtitle.text = item.dubSubText

        // SINKRONISASI IMAGE INTERNET: Memuat tautan URL gambar asli spreadsheet lewat internet
        if (item.imageUrl.isNotEmpty()) {
            holder.gridImage.load(item.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_circle_bookmark) // Gambar sementara loading
                error(R.drawable.bg_circle_bookmark) // Gambar cadangan jika internet putus
            }
            holder.gridCard.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        } else {
            holder.gridImage.setImageResource(android.R.color.transparent)
            holder.gridCard.setBackgroundColor(item.placeholderColor)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        holder.btnGridMore.setOnClickListener { v ->
            val context = v.context
            val popup = PopupMenu(context, v)
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

    fun updateList(newList: List<AnimeModel>) {
        animeList = newList
        notifyDataSetChanged()
    }
}
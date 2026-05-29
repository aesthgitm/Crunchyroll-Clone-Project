package com.example.crunchyroll_pemvis_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load // IMPORT COIL

class EpisodeAdapter(
    private val episodeList: List<EpisodeModel>,
    private val onEpisodeClick: (EpisodeModel) -> Unit,
    private val onDownloadClick: (EpisodeModel) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Hubungkan ke ID ImageView baru yang kita buat di XML tadi
        val imgEpisodeThumbnail: ImageView = view.findViewById(R.id.img_episode_thumbnail)
        val txtProgress: TextView = view.findViewById(R.id.txt_progress)
        val txtEpisodeTitle: TextView = view.findViewById(R.id.txt_episode_title)
        val btnDownload: View = view.findViewById(R.id.btn_episode_download)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = episodeList[position]
        holder.txtEpisodeTitle.text = "${item.number}. ${item.name}"
        holder.txtProgress.text = item.durationRemaining
        
        // Pemuatan Gambar Menggunakan Coil dari Internet URL secara dinamis
        if (item.imageUrl.isNotEmpty()) {
            holder.imgEpisodeThumbnail.load(item.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.bg_circle_bookmark)
                error(R.drawable.bg_circle_bookmark)
            }
        } else {
            holder.imgEpisodeThumbnail.setImageResource(android.R.color.transparent)
            holder.imgEpisodeThumbnail.setBackgroundColor(item.thumbnailColor)
        }

        holder.itemView.setOnClickListener { onEpisodeClick(item) }
        holder.btnDownload.setOnClickListener { onDownloadClick(item) }
    }

    override fun getItemCount() = episodeList.size
}
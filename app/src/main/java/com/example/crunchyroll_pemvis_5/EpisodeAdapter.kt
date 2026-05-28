package com.example.crunchyroll_pemvis_5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EpisodeAdapter(
    private val episodeList: List<EpisodeModel>,
    private val onEpisodeClick: (EpisodeModel) -> Unit,
    private val onDownloadClick: (EpisodeModel) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailContainer: View = view.findViewById(R.id.thumbnail_container)
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
        
        // Use color placeholder for episode thumbnail
        holder.thumbnailContainer.setBackgroundColor(item.thumbnailColor)

        holder.itemView.setOnClickListener {
            onEpisodeClick(item)
        }

        holder.btnDownload.setOnClickListener {
            onDownloadClick(item)
        }
    }

    override fun getItemCount() = episodeList.size
}

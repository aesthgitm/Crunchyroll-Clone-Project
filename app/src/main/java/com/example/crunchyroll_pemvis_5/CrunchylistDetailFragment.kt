package com.example.crunchyroll_pemvis_5

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CrunchylistDetailFragment : Fragment() {
    private var crunchylistId: String = ""
    private var crunchylist: CrunchylistModel? = null
    private lateinit var txtTitle: TextView
    private lateinit var txtCount: TextView
    private lateinit var rvItems: RecyclerView

    companion object {
        private const val ARG_CRUNCHYLIST_ID = "arg_crunchylist_id"
        fun newInstance(crunchylistId: String): CrunchylistDetailFragment {
            val fragment = CrunchylistDetailFragment()
            fragment.arguments = Bundle().apply { putString(ARG_CRUNCHYLIST_ID, crunchylistId) }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crunchylistId = arguments?.getString(ARG_CRUNCHYLIST_ID).orEmpty()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_crunchylist_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtTitle = view.findViewById(R.id.txt_crunchylist_title)
        txtCount = view.findViewById(R.id.txt_crunchylist_count)
        rvItems = view.findViewById(R.id.rv_crunchylist_items)

        view.findViewById<View>(R.id.btn_back_crunchylist).setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        view.findViewById<View>(R.id.btn_crunchylist_menu).setOnClickListener { anchor ->
            showListMenu(anchor)
        }
        view.findViewById<View>(R.id.btn_add_anime_to_crunchylist).setOnClickListener {
            showAddAnimeDialog(view)
        }

        rvItems.layoutManager = LinearLayoutManager(context)
        bindData(view)
    }

    private fun bindData(rootView: View) {
        crunchylist = MockData.createdCrunchylists.find { it.id == crunchylistId }
        val activeList = crunchylist ?: return
        txtTitle.text = activeList.name
        txtCount.text = "${activeList.animeIds.size}/100 Item"

        val animeItems = activeList.animeIds.mapNotNull { id -> MockData.allAnime.find { it.id == id } }
        rvItems.adapter = CrunchylistAnimeAdapter(animeItems,
            onItemClick = { anime ->
                (activity as? MainActivity)?.openDetailFragment(anime)
            },
            onRemoveClick = { anime ->
                removeAnimeFromList(rootView, anime.id)
            }
        )
    }

    private fun showListMenu(anchor: View) {
        val list = crunchylist ?: return
        val popup = PopupMenu(requireContext(), anchor)
        popup.menu.add("Ganti Nama Crunchylist")
        popup.menu.add("Hapus Crunchylist")
        popup.setOnMenuItemClickListener {
            when (it.title.toString()) {
                "Ganti Nama Crunchylist" -> showRenameDialog(anchor)
                "Hapus Crunchylist" -> deleteCurrentList(anchor)
            }
            true
        }
        popup.show()
    }

    private fun showRenameDialog(rootView: View) {
        val list = crunchylist ?: return
        val input = EditText(requireContext())
        input.setText(list.name)
        AlertDialog.Builder(requireContext())
            .setTitle("Ganti Nama Crunchylist")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isEmpty()) return@setPositiveButton
                list.name = newName
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (userId.isNotEmpty()) {
                    FirestoreHelper().renameCrunchylist(list.id, newName) { }
                }
                AppNotifier.show(rootView, "Nama crunchylist diperbarui")
                bindData(rootView)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteCurrentList(rootView: View) {
        val list = crunchylist ?: return
        MockData.createdCrunchylists.removeAll { it.id == list.id }
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            FirestoreHelper().deleteCrunchylist(list.id) { }
        }
        AppNotifier.show(rootView, "Crunchylist dihapus")
        activity?.supportFragmentManager?.popBackStack()
    }

    private fun showAddAnimeDialog(rootView: View) {
        val list = crunchylist ?: return
        val candidates = MockData.allAnime.filterNot { list.animeIds.contains(it.id) }
        if (candidates.isEmpty()) {
            AppNotifier.show(rootView, "Semua anime sudah ada di list")
            return
        }
        val titles = candidates.map { it.title }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Tambah Anime")
            .setItems(titles) { _, which ->
                val chosen = candidates[which]
                list.animeIds.add(chosen.id)
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (userId.isNotEmpty()) {
                    FirestoreHelper().addAnimeToCrunchylist(list.id, chosen.id) { }
                }
                AppNotifier.show(rootView, "${chosen.title} ditambahkan")
                bindData(rootView)
            }
            .show()
    }

    private fun removeAnimeFromList(rootView: View, animeId: String) {
        val list = crunchylist ?: return
        list.animeIds.remove(animeId)
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            FirestoreHelper().removeAnimeFromCrunchylist(list.id, animeId) { }
        }
        AppNotifier.show(rootView, "Anime dihapus dari list")
        bindData(rootView)
    }

    class CrunchylistAnimeAdapter(
        private val items: List<AnimeModel>,
        private val onItemClick: (AnimeModel) -> Unit,
        private val onRemoveClick: (AnimeModel) -> Unit
    ) : RecyclerView.Adapter<CrunchylistAnimeAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imgPoster: ImageView = view.findViewById(R.id.img_crunchylist_anime)
            val txtTitle: TextView = view.findViewById(R.id.txt_crunchylist_anime_title)
            val txtSubtitle: TextView = view.findViewById(R.id.txt_crunchylist_anime_subtitle)
            val btnMore: View = view.findViewById(R.id.btn_crunchylist_item_more)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crunchylist_anime, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val anime = items[position]
            holder.txtTitle.text = anime.title
            holder.txtSubtitle.text = anime.dubSubText
            if (anime.imageResId != null && anime.imageResId != 0) {
                holder.imgPoster.setImageResource(anime.imageResId)
            } else {
                holder.imgPoster.setImageResource(android.R.color.transparent)
                holder.imgPoster.setBackgroundColor(anime.placeholderColor)
            }
            holder.itemView.setOnClickListener { onItemClick(anime) }
            holder.btnMore.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.menu.add("Hapus dari Crunchylist")
                popup.setOnMenuItemClickListener {
                    onRemoveClick(anime)
                    true
                }
                popup.show()
            }
        }
    }
}

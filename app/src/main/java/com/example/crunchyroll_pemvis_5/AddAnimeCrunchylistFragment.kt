package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load

class AddAnimeCrunchylistFragment : Fragment() {

    private lateinit var edtSearch: EditText
    private lateinit var chipGroupGenres: com.google.android.material.chip.ChipGroup
    private lateinit var rvAnime: RecyclerView
    private lateinit var txtNoResults: TextView

    private var currentListName: String = ""
    private var activeGenreFilter: String = "Semua"
    private var currentSearchQuery: String = ""

    companion object {
        private const val ARG_LIST_NAME = "arg_list_name"

        fun newInstance(listName: String): AddAnimeCrunchylistFragment {
            val fragment = AddAnimeCrunchylistFragment()
            val args = Bundle()
            args.putString(ARG_LIST_NAME, listName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentListName = arguments?.getString(ARG_LIST_NAME).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_anime_crunchylist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edtSearch = view.findViewById(R.id.edt_search_anime_list)
        chipGroupGenres = view.findViewById(R.id.chip_group_genres)
        rvAnime = view.findViewById(R.id.rv_available_anime)
        txtNoResults = view.findViewById(R.id.txt_no_results)

        rvAnime.layoutManager = LinearLayoutManager(context)

        view.findViewById<View>(R.id.btn_back_add_anime).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupGenreChips()
        performFiltering()

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString().trim()
                performFiltering()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupGenreChips() {
        val genres = listOf("Semua", "Aksi", "Drama", "Romantis")
        chipGroupGenres.removeAllViews()

        genres.forEach { genreName ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = genreName
                isCheckable = true
                isChecked = genreName == activeGenreFilter
                setTextColor(android.graphics.Color.WHITE)

                chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor(if (isChecked) "#FF6400" else "#23252B")
                )

                isCloseIconVisible = false

                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        activeGenreFilter = genreName
                        performFiltering()
                        setupGenreChips()
                    }
                }
            }
            chipGroupGenres.addView(chip)
        }
    }

    private fun performFiltering() {
        val currentList = MockData.createdCrunchylists.find { it.name == currentListName } ?: return

        var results = MockData.allAnime.filter { anime ->
            anime.title.contains(currentSearchQuery, ignoreCase = true)
        }

        if (activeGenreFilter != "Semua") {
            val matchingGenreAnimes = MockData.animeByGenre[activeGenreFilter]?.map { it.id }.orEmpty()
            results = results.filter { it.id in matchingGenreAnimes }
        }

        if (results.isEmpty()) {
            txtNoResults.visibility = View.VISIBLE
            rvAnime.visibility = View.GONE
        } else {
            txtNoResults.visibility = View.GONE
            rvAnime.visibility = View.VISIBLE
            rvAnime.adapter = InnerAddAnimeAdapter(results, currentList)
        }
    }

    private inner class InnerAddAnimeAdapter(
        private val list: List<AnimeModel>,
        private val crunchylist: CrunchylistModel
    ) : RecyclerView.Adapter<InnerAddAnimeAdapter.ViewHolder>() {

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val imgPoster: ImageView = v.findViewById(R.id.img_item_poster)
            val txtTitle: TextView = v.findViewById(R.id.txt_item_title)
            val txtMeta: TextView = v.findViewById(R.id.txt_item_meta)
            val btnToggle: View = v.findViewById(R.id.btn_toggle_add)
            val imgIcon: ImageView = v.findViewById(R.id.img_toggle_icon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_anime_to_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val anime = list[position]
            holder.txtTitle.text = anime.title
            holder.txtMeta.text = "${anime.subtitle} • ${anime.ageRating}"
            holder.imgPoster.load(anime.imageUrl)

            val isAdded = crunchylist.animeIds.contains(anime.id)
            val cardView = holder.btnToggle as androidx.cardview.widget.CardView

            if (isAdded) {
                cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#FF6400"))
                holder.imgIcon.setImageResource(android.R.drawable.star_on)
                holder.imgIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#111315"))
            } else {
                cardView.setCardBackgroundColor(android.graphics.Color.parseColor("#23252B"))
                holder.imgIcon.setImageResource(android.R.drawable.ic_menu_add)
                holder.imgIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            }

            holder.btnToggle.setOnClickListener {
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (isAdded) {
                    crunchylist.animeIds.remove(anime.id)
                    if (userId.isNotEmpty()) {
                        FirestoreHelper().removeAnimeFromCrunchylist(crunchylist.id, anime.id) { }
                    }
                    // FIX: Mengganti 'context' menjadi 'requireContext()' agar aman dari eror type mismatch
                    Toast.makeText(requireContext(), "Dihapus dari ${crunchylist.name}", Toast.LENGTH_SHORT).show()
                } else {
                    crunchylist.animeIds.add(anime.id)
                    if (userId.isNotEmpty()) {
                        FirestoreHelper().addAnimeToCrunchylist(crunchylist.id, anime.id) { }
                    }
                    // FIX: Mengganti 'context' menjadi 'requireContext()' agar aman dari eror type mismatch
                    Toast.makeText(requireContext(), "Ditambahkan ke ${crunchylist.name}", Toast.LENGTH_SHORT).show()
                }
                notifyItemChanged(position)
            }
        }

        override fun getItemCount(): Int = list.size
    }
}
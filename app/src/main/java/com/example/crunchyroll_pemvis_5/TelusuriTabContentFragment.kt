package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import coil.load // IMPORT COIL UNTUK MEMUAT GAMBAR INTERNET
import com.google.android.material.bottomsheet.BottomSheetDialog

class TelusuriTabContentFragment : Fragment() {

    private var tabIndex = 0

    companion object {
        private const val ARG_TAB_INDEX = "tab_index"

        fun newInstance(tabIndex: Int): TelusuriTabContentFragment {
            val fragment = TelusuriTabContentFragment()
            val args = Bundle()
            args.putInt(ARG_TAB_INDEX, tabIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tabIndex = arguments?.getInt(ARG_TAB_INDEX) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutRes = when (tabIndex) {
            0 -> R.layout.fragment_telusuri_semua
            1 -> R.layout.fragment_telusuri_simulcast
            2 -> R.layout.fragment_telusuri_genre
            3 -> R.layout.fragment_telusuri_musik
            else -> R.layout.fragment_telusuri_semua
        }
        return inflater.inflate(layoutRes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (tabIndex) {
            0 -> setupSemuaAnime(view)
            1 -> setupSimulcast(view)
            2 -> setupGenreAnime(view)
            3 -> setupMusik(view)
        }
    }

    private fun showAnimeOptions(anchorView: View, title: String) {
        val context = context ?: return
        val popup = androidx.appcompat.widget.PopupMenu(context, anchorView)

        popup.menu.add("Tambahkan ke Daftar Tonton")
        popup.menu.add("Tonton Sekarang")
        popup.menu.add("Bagikan")
        popup.menu.add("Tandai sebagai Sudah Ditonton")

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                "Tambahkan ke Daftar Tonton" -> {
                    Toast.makeText(context, "Ditambahkan ke Daftar Tonton: $title", Toast.LENGTH_SHORT).show()
                    true
                }
                "Tonton Sekarang" -> {
                    Toast.makeText(context, "Memutar: $title", Toast.LENGTH_SHORT).show()
                    true
                }
                "Bagikan" -> {
                    Toast.makeText(context, "Membagikan: $title", Toast.LENGTH_SHORT).show()
                    true
                }
                "Tandai sebagai Sudah Ditonton" -> {
                    Toast.makeText(context, "Ditandai sebagai sudah ditonton: $title", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showAnimeOptions(anchorView: View, animeId: String, title: String) {
        val context = context ?: return
        val popup = androidx.appcompat.widget.PopupMenu(context, anchorView)

        popup.menu.add("Tambahkan ke Daftar Tonton")
        popup.menu.add("Tonton Sekarang")
        popup.menu.add("Bagikan")
        popup.menu.add("Tandai sebagai Sudah Ditonton")

        popup.setOnMenuItemClickListener { menuItem ->
            val anime = MockData.allAnime.find { it.id == animeId }
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            when (menuItem.title) {
                "Tambahkan ke Daftar Tonton" -> {
                    if (anime != null) {
                        val isBookmarked = MockData.watchlist.any { it.id == anime.id }
                        if (isBookmarked) {
                            Toast.makeText(context, "${anime.title} sudah di Daftar Tonton", Toast.LENGTH_SHORT).show()
                        } else {
                            MockData.watchlist.add(anime)
                            if (userId.isNotEmpty()) {
                                FirestoreHelper().addToWatchlist(userId, anime.id) { }
                            }
                            Toast.makeText(context, "Ditambahkan ke Daftar Tonton: ${anime.title}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Ditambahkan ke Daftar Tonton: $title", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                "Tonton Sekarang" -> {
                    if (anime != null) {
                        MockData.watchHistory.removeIf { it.id == anime.id }
                        MockData.watchHistory.add(0, anime)
                        if (userId.isNotEmpty()) {
                            FirestoreHelper().addToWatchHistory(userId, anime.id) { }
                        }
                        Toast.makeText(context, "Memutar: ${anime.title}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Memutar: $title", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                "Bagikan" -> {
                    Toast.makeText(context, "Membagikan: $title", Toast.LENGTH_SHORT).show()
                    true
                }
                "Tandai sebagai Sudah Ditonton" -> {
                    if (anime != null) {
                        MockData.watchHistory.removeIf { it.id == anime.id }
                        MockData.watchHistory.add(0, anime)
                        if (userId.isNotEmpty()) {
                            FirestoreHelper().addToWatchHistory(userId, anime.id) { }
                        }
                        Toast.makeText(context, "Ditandai sebagai sudah ditonton: ${anime.title}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ditandai as sudah ditonton: $title", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupSemuaAnime(view: View) {
        view.findViewById<ImageView>(R.id.btn_sort)?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SortFragment())
                .addToBackStack(null)
                .commit()
        }
        view.findViewById<ImageView>(R.id.btn_filter)?.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FilterFragment())
                .addToBackStack(null)
                .commit()
        }

        val rvAllAnime = view.findViewById<RecyclerView>(R.id.rv_all_anime)
        rvAllAnime?.layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2)

        var displayList = MockData.allAnime.toMutableList()

        if (MockData.selectedGenreFilter != "Semua") {
            displayList = when (MockData.selectedGenreFilter) {
                "Movie" -> displayList.filter { it.subtitle.contains("Movie", ignoreCase = true) }.toMutableList()
                "Seri" -> displayList.filter { !it.subtitle.contains("Movie", ignoreCase = true) }.toMutableList()
                "Takarir" -> displayList.filter { it.dubSubText.contains("Takarir", ignoreCase = true) || it.dubSubText.contains("Sub", ignoreCase = true) }.toMutableList()
                "Sulih Suara" -> displayList.filter { it.dubSubText.contains("Sulih Suara", ignoreCase = true) || it.dubSubText.contains("Dub", ignoreCase = true) }.toMutableList()
                else -> displayList.filter { 
                    it.subtitle.contains(MockData.selectedGenreFilter, ignoreCase = true) ||
                    it.description.contains(MockData.selectedGenreFilter, ignoreCase = true)
                }.toMutableList()
            }
        }

        when (MockData.selectedSortOption) {
            "A-Z" -> displayList.sortBy { it.title }
            "Z-A" -> displayList.sortByDescending { it.title }
            "Populer" -> displayList.sortByDescending { it.ratingReviews }
            "Rating" -> displayList.sortByDescending { it.ratingStars }
        }

        rvAllAnime?.adapter = AnimeListAdapter(displayList) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }
    }

    override fun onResume() {
        super.onResume()
        if (view != null) {
            when (tabIndex) {
                0 -> setupSemuaAnime(requireView())
                1 -> setupSimulcast(requireView())
            }
        }
    }

    private fun setupSimulcast(view: View) {
        val txtSelectedSeason = view.findViewById<TextView>(R.id.txt_selected_season)
        txtSelectedSeason?.text = MockData.selectedSimulcastSeason

        view.findViewById<LinearLayout>(R.id.btn_season_dropdown)?.setOnClickListener { btn ->
            val context = context ?: return@setOnClickListener
            val popup = androidx.appcompat.widget.PopupMenu(context, btn)
            val seasons = listOf("Musim Dingin 2026", "Musim Gugur 2025", "Musim Panas 2025", "Musim Semi 2025")
            for (season in seasons) {
                popup.menu.add(season)
            }
            popup.setOnMenuItemClickListener { menuItem ->
                val selected = menuItem.title.toString()
                MockData.selectedSimulcastSeason = selected
                txtSelectedSeason?.text = selected
                Toast.makeText(context, "Menampilkan anime untuk $selected", Toast.LENGTH_SHORT).show()
                true
            }
            popup.show()
        }

        val itemsMap = mapOf(
            R.id.item_gachiakuta to ("dsr" to "Gachiakuta"),
            R.id.item_shield_hero to ("aot" to "The Rising of the Shield Hero"),
            R.id.item_dress_up to ("shy" to "My Dress-Up Darling"),
            R.id.item_dan_da_dan to ("tye" to "DAN DA DAN")
        )

        for ((id, pair) in itemsMap) {
            val (animeId, title) = pair
            val anime = MockData.allAnime.find { it.id == animeId }
            view.findViewById<LinearLayout>(id)?.setOnClickListener {
                if (anime != null) {
                    (activity as? MainActivity)?.openDetailFragment(anime)
                } else {
                    Toast.makeText(context, "Membuka halaman detail anime: $title", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val dotIds = listOf(
            R.id.more_dots_s1 to "dsr",
            R.id.more_dots_s2 to "aot",
            R.id.more_dots_s3 to "shy",
            R.id.more_dots_s4 to "tye"
        )
        for ((dotId, animeId) in dotIds) {
            val title = itemsMap.values.find { it.first == animeId }?.second ?: "Anime"
            view.findViewById<ImageView>(dotId)?.setOnClickListener { btn ->
                showAnimeOptions(btn, animeId, title)
            }
        }
    }

    // ========================================================================
    // FIX DI SINI: MENGATUR 3 GENRE DAN MEMUAT POSTER SEBAGAI BACKGROUND CARD
    // ========================================================================
    private fun setupGenreAnime(view: View) {
        // 1. Definisikan peta klik hanya untuk 3 genre utama (Aksi, Drama, Romantis)
        val genresMap = mapOf(
            R.id.genre_aksi to Pair("Aksi", "🔥"),
            R.id.genre_drama to Pair("Drama", "🎭"),
            R.id.genre_romansa to Pair("Romantis", "💖") // Key "Romantis" digunakan agar sinkron dengan filter MockData
        )

        for ((id, info) in genresMap) {
            view.findViewById<CardView>(id)?.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, GenreDetailFragment.newInstance(info.first, info.second))
                    .addToBackStack(null)
                    .commit()
            }
        }

        // 2. Muat gambar anime pertama secara otomatis dari pangkalan data genre menggunakan Coil
        val imgAksi = view.findViewById<ImageView>(R.id.img_genre_aksi)
        val aksiAnime = MockData.animeByGenre["Aksi"]?.firstOrNull()
        if (imgAksi != null && aksiAnime != null && aksiAnime.imageUrl.isNotEmpty()) {
            imgAksi.load(aksiAnime.imageUrl) {
                crossfade(true)
            }
        }

        val imgDrama = view.findViewById<ImageView>(R.id.img_genre_drama)
        val dramaAnime = MockData.animeByGenre["Drama"]?.firstOrNull()
        if (imgDrama != null && dramaAnime != null && dramaAnime.imageUrl.isNotEmpty()) {
            imgDrama.load(dramaAnime.imageUrl) {
                crossfade(true)
            }
        }

        val imgRomansa = view.findViewById<ImageView>(R.id.img_genre_romansa)
        val romansaAnime = MockData.animeByGenre["Romantis"]?.firstOrNull()
        if (imgRomansa != null && romansaAnime != null && romansaAnime.imageUrl.isNotEmpty()) {
            imgRomansa.load(romansaAnime.imageUrl) {
                crossfade(true)
            }
        }
    }

    private fun setupMusik(view: View) {
        view.findViewById<LinearLayout>(R.id.btn_play_music)?.setOnClickListener {
            Toast.makeText(context, "Memutar video musik BLUE ENCOUNT...", Toast.LENGTH_SHORT).show()
        }

        val artists = listOf(R.id.artist_1, R.id.artist_2, R.id.artist_3, R.id.artist_4, R.id.artist_5)
        for ((index, id) in artists.withIndex()) {
            view.findViewById<LinearLayout>(id)?.setOnClickListener {
                Toast.makeText(context, "Membak detail artis ke-${index + 1}...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
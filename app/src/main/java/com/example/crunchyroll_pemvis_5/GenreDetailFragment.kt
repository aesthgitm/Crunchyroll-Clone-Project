package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GenreDetailFragment : Fragment() {

    private var genreTitle: String = ""

    companion object {
        private const val ARG_GENRE_TITLE = "genre_title"

        fun newInstance(title: String, emoji: String): GenreDetailFragment {
            val fragment = GenreDetailFragment()
            val args = Bundle()
            args.putString(ARG_GENRE_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genreTitle = arguments?.getString(ARG_GENRE_TITLE).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_genre_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Mengatur teks judul genre utama di tengah atas
        val txtGenreTitle = view.findViewById<TextView>(R.id.txt_genre_title)
        if (genreTitle == "Romantis") {
            txtGenreTitle?.text = "Romansa"
        } else {
            txtGenreTitle?.text = "$genreTitle"
        }

        // Action klik tombol kembali
        view.findViewById<ImageView>(R.id.btn_genre_back)?.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        // ========================================================================
        // LOGIKA UTAMA: MENAMPILKAN ANIME YANG SESUAI GENRE DALAM BENTUK GRID 2 KOLOM
        // ========================================================================
        val rvGenreAnimeList = view.findViewById<RecyclerView>(R.id.rv_genre_anime_list)
        
        // Menggunakan GridLayoutManager (2 Kolom) agar sejajar rapi ke bawah
        rvGenreAnimeList?.layoutManager = GridLayoutManager(context, 2)
        
        // Mengambil daftar anime yang hanya sesuai dengan genre yang diklik dari MockData
        val filteredAnime = MockData.animeByGenre[genreTitle].orEmpty()
        
        // Memasang adapter AnimeListAdapter bawaan kamu untuk menampilkan card grid item
        rvGenreAnimeList?.adapter = AnimeListAdapter(filteredAnime) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }
    }
}
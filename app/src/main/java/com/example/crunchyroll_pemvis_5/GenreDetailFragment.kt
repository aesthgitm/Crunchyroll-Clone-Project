package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GenreDetailFragment : Fragment() {

    private var genreName: String = "Aksi"
    private var genreEmoji: String = "🔥"

    companion object {
        private const val ARG_GENRE_NAME = "genre_name"
        private const val ARG_GENRE_EMOJI = "genre_emoji"

        fun newInstance(genreName: String, genreEmoji: String): GenreDetailFragment {
            val fragment = GenreDetailFragment()
            val args = Bundle()
            args.putString(ARG_GENRE_NAME, genreName)
            args.putString(ARG_GENRE_EMOJI, genreEmoji)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            genreName = it.getString(ARG_GENRE_NAME, "Aksi")
            genreEmoji = it.getString(ARG_GENRE_EMOJI, "🔥")
        }
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

        // Set Toolbar & Title Details
        view.findViewById<TextView>(R.id.txt_genre_title).text = genreName

        // Back Button Action
        view.findViewById<ImageView>(R.id.btn_genre_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Cast & Search Actions
        view.findViewById<ImageView>(R.id.btn_genre_cast).setOnClickListener {
            Toast.makeText(context, "Menghubungkan ke perangkat Cast...", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<ImageView>(R.id.btn_genre_search).setOnClickListener {
            (activity as? MainActivity)?.openSearchFragment()
        }

        // Setup horizontal RecyclerViews for Categories
        val rvPopular = view.findViewById<RecyclerView>(R.id.rv_genre_popular)
        val rvNew = view.findViewById<RecyclerView>(R.id.rv_genre_new)
        val rvRecommend = view.findViewById<RecyclerView>(R.id.rv_genre_recommend)

        rvPopular.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvNew.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Populate items from MockData
        val popularList = listOf(
            MockData.bannerAnime[0], // Dr. Stone
            MockData.trendingAnime[0], // To Your Eternity
            MockData.trendingAnime[1]  // Daemons of the Shadow Realm
        )

        val newList = listOf(
            MockData.trendingAnime[2], // The strongest job is a...
            MockData.exclusiveAnime[0], // SHY
            MockData.trendingAnime[0]   // To Your Eternity
        )

        val recommendList = MockData.exclusiveAnime

        // Set Adapters with click and PopupMenu events
        rvPopular.adapter = AnimePosterAdapter(popularList) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }
        
        rvNew.adapter = AnimePosterAdapter(newList) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }

        rvRecommend.adapter = AnimePosterAdapter(recommendList) { anime ->
            (activity as? MainActivity)?.openDetailFragment(anime)
        }

        // Handle see all clicks
        val seeAllClickListener = View.OnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.navigation_telusuri)
        }
        view.findViewById<TextView>(R.id.btn_see_all_populer).setOnClickListener(seeAllClickListener)
        view.findViewById<TextView>(R.id.btn_see_all_baru).setOnClickListener(seeAllClickListener)
        view.findViewById<TextView>(R.id.btn_see_all_rekomendasi).setOnClickListener(seeAllClickListener)
    }
}

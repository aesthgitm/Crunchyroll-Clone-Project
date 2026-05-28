package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DetailFragment : Fragment() {

    private var anime: AnimeModel? = null

    companion object {
        private const val ARG_ANIME = "arg_anime"

        fun newInstance(anime: AnimeModel): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_ANIME, anime)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        anime = arguments?.getSerializable(ARG_ANIME) as? AnimeModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val currentAnime = anime ?: return view

        // Element Binding
        val imgBanner = view.findViewById<ImageView>(R.id.detail_banner_image)
        val txtTitle = view.findViewById<TextView>(R.id.detail_title)
        val txtSubtitle = view.findViewById<TextView>(R.id.detail_subtitle)
        val txtAge = view.findViewById<TextView>(R.id.badge_age)
        val txtDub = view.findViewById<TextView>(R.id.badge_dub)
        val txtRating = view.findViewById<TextView>(R.id.rating_text)
        val txtDescription = view.findViewById<TextView>(R.id.detail_description)
        val rvEpisodes = view.findViewById<RecyclerView>(R.id.rv_episodes)
        
        val btnBack = view.findViewById<ImageView>(R.id.btn_detail_back)
        val btnResume = view.findViewById<View>(R.id.btn_resume_episode)
        val btnBookmarkSticky = view.findViewById<ImageView>(R.id.btn_detail_bookmark_sticky)
        val btnOrderLatest = view.findViewById<TextView>(R.id.btn_order_latest)
        val btnOrderOldest = view.findViewById<TextView>(R.id.btn_order_oldest)
        view.findViewById<View>(R.id.btn_detail_cast)?.visibility = View.GONE
        view.findViewById<View>(R.id.btn_detail_more)?.visibility = View.GONE

        // Assign details values
        txtTitle.text = currentAnime.title
        txtSubtitle.text = "— ${currentAnime.subtitle} —"
        txtAge.text = currentAnime.ageRating
        txtDub.text = currentAnime.dubSubText
        txtRating.text = "Rata-rata: ${currentAnime.ratingStars} (${currentAnime.ratingReviews})"
        txtDescription.text = currentAnime.description

        // Assign background image or placeholder color to banner image
        if (currentAnime.imageResId != null && currentAnime.imageResId != 0) {
            imgBanner.setImageResource(currentAnime.imageResId)
        } else {
            imgBanner.setImageResource(android.R.color.transparent)
            imgBanner.setBackgroundColor(currentAnime.placeholderColor)
        }

        // Helper to update bookmark state in UI
        fun updateBookmarkUI() {
            val isBookmarked = MockData.watchlist.any { it.id == currentAnime.id }
            btnBookmarkSticky.setImageResource(
                if (isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark
            )
            
            // If there's an add to list text/icon, we can update it too (or just rely on toast)
        }
        updateBookmarkUI()

        // Helper to add anime to downloads
        fun addAnimeToDownloads() {
            if (!MockData.downloads.any { it.id == currentAnime.id }) {
                MockData.downloads.add(currentAnime)
                val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (userId.isNotEmpty()) {
                    FirestoreHelper().addToDownloads(userId, currentAnime.id) { }
                }
                AppNotifier.show(view, "${currentAnime.title} berhasil diunduh!")
            } else {
                AppNotifier.show(view, "${currentAnime.title} sudah diunduh sebelumnya")
            }
        }

        var isLatestOrder = true
        fun bindEpisodeAdapter() {
            val displayEpisodes = if (isLatestOrder) currentAnime.episodes.reversed() else currentAnime.episodes
            rvEpisodes.adapter = EpisodeAdapter(
                displayEpisodes,
                onEpisodeClick = { episode ->
                    MockData.watchHistory.removeIf { it.id == currentAnime.id }
                    MockData.watchHistory.add(0, currentAnime)

                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    if (userId.isNotEmpty()) {
                        FirestoreHelper().addToWatchHistory(userId, currentAnime.id) { }
                    }

                    val index = currentAnime.episodes.indexOf(episode)
                    (activity as? MainActivity)?.openPlayerFragment(currentAnime, if (index >= 0) index else 0)
                },
                onDownloadClick = {
                    addAnimeToDownloads()
                }
            )
            btnOrderLatest.setTextColor(resources.getColor(if (isLatestOrder) R.color.color_primary else R.color.text_secondary, null))
            btnOrderOldest.setTextColor(resources.getColor(if (isLatestOrder) R.color.text_secondary else R.color.color_primary, null))
        }

        rvEpisodes.layoutManager = LinearLayoutManager(context)
        bindEpisodeAdapter()
        btnOrderLatest.setOnClickListener {
            isLatestOrder = true
            bindEpisodeAdapter()
        }
        btnOrderOldest.setOnClickListener {
            isLatestOrder = false
            bindEpisodeAdapter()
        }

        // Download all buttons
        view.findViewById<View>(R.id.btn_download_all_text)?.setOnClickListener {
            addAnimeToDownloads()
        }
        view.findViewById<View>(R.id.btn_download_all_icon)?.setOnClickListener {
            addAnimeToDownloads()
        }

        // Floating top bar actions
        btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Pinned sticky actions
        btnResume.setOnClickListener {
            // Add to history when clicking resume/play
            MockData.watchHistory.removeIf { it.id == currentAnime.id }
            MockData.watchHistory.add(0, currentAnime)

            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (userId.isNotEmpty()) {
                FirestoreHelper().addToWatchHistory(userId, currentAnime.id) { }
            }

            (activity as? MainActivity)?.openPlayerFragment(currentAnime, 0)
        }

        btnBookmarkSticky.setOnClickListener {
            val isBookmarked = MockData.watchlist.any { it.id == currentAnime.id }
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            
            if (isBookmarked) {
                MockData.watchlist.removeIf { it.id == currentAnime.id }
                if (userId.isNotEmpty()) {
                    FirestoreHelper().removeFromWatchlist(userId, currentAnime.id) { }
                }
                AppNotifier.show(view, "${currentAnime.title} dihapus dari Daftar Saya")
            } else {
                MockData.watchlist.add(currentAnime)
                if (userId.isNotEmpty()) {
                    FirestoreHelper().addToWatchlist(userId, currentAnime.id) { }
                }
                AppNotifier.show(view, "${currentAnime.title} disimpan ke Daftar Saya")
            }
            updateBookmarkUI()
        }

        view.findViewById<View>(R.id.btn_add_to_list).setOnClickListener {
            if (MockData.createdCrunchylists.isEmpty()) {
                AppNotifier.show(view, "Buat Crunchylist dulu sebelum menambahkan anime")
                return@setOnClickListener
            }
            val popup = PopupMenu(requireContext(), it)
            MockData.createdCrunchylists.forEach { list ->
                popup.menu.add(list.name)
            }
            popup.setOnMenuItemClickListener { item ->
                val selectedList = MockData.createdCrunchylists.find { it.name == item.title.toString() }
                if (selectedList != null) {
                    if (!selectedList.animeIds.contains(currentAnime.id)) {
                        selectedList.animeIds.add(currentAnime.id)
                        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        if (userId.isNotEmpty()) {
                            FirestoreHelper().addAnimeToCrunchylist(selectedList.id, currentAnime.id) { }
                        }
                        AppNotifier.show(view, "${currentAnime.title} ditambahkan ke ${selectedList.name}")
                    } else {
                        AppNotifier.show(view, "${currentAnime.title} sudah ada di ${selectedList.name}")
                    }
                }
                true
            }
            popup.show()
        }

        view.findViewById<View>(R.id.btn_share).setOnClickListener {
            AppNotifier.show(view, "Membagikan ${currentAnime.title}...")
        }

        // Setup Reviews
        val rvReviews = view.findViewById<RecyclerView>(R.id.rv_reviews)
        val ratingBar = view.findViewById<android.widget.RatingBar>(R.id.rating_bar_review)
        val edtComment = view.findViewById<android.widget.EditText>(R.id.edt_review_comment)
        val btnSubmitReview = view.findViewById<android.widget.Button>(R.id.btn_submit_review)

        rvReviews.layoutManager = LinearLayoutManager(context)
        val reviewAdapter = ReviewAdapter(emptyList())
        rvReviews.adapter = reviewAdapter

        fun loadReviews() {
            FirestoreHelper().getReviews(currentAnime.id, { reviewsList ->
                val sortedReviews = reviewsList.sortedByDescending {
                    (it["timestamp"] as? com.google.firebase.Timestamp)?.seconds ?: 0L
                }
                activity?.runOnUiThread {
                    reviewAdapter.updateData(sortedReviews)
                }
            }, {
                // Gagal memuat ulasan
            })
        }
        loadReviews()

        btnSubmitReview.setOnClickListener {
            val comment = edtComment.text.toString().trim()
            val rating = ratingBar.rating.toDouble()
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val username = MockData.profileName.ifEmpty { "Pengguna Crunchyroll" }

            if (comment.isEmpty()) {
                AppNotifier.show(view, "Silakan isi ulasan terlebih dahulu")
                return@setOnClickListener
            }

            if (userId.isEmpty()) {
                AppNotifier.show(view, "Silakan masuk/login untuk memberikan ulasan")
                return@setOnClickListener
            }

            btnSubmitReview.isEnabled = false
            FirestoreHelper().addReview(currentAnime.id, userId, username, rating, comment) { success ->
                activity?.runOnUiThread {
                    btnSubmitReview.isEnabled = true
                    if (success) {
                        AppNotifier.show(view, "Ulasan berhasil dikirim!")
                        edtComment.setText("")
                        ratingBar.rating = 5.0f
                        loadReviews()
                    } else {
                        AppNotifier.show(view, "Gagal mengirim ulasan")
                    }
                }
            }
        }

        return view
    }
}

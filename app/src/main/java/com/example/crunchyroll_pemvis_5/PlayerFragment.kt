package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import coil.load // MEMUAT GAMBAR THUMBNAIL DINAMIS
import com.google.firebase.auth.FirebaseAuth
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

class PlayerFragment : Fragment() {

    private var animeId: String = ""
    private var episodeIndex: Int = 0
    private var anime: AnimeModel? = null

    private lateinit var youtubePlayerView: YouTubePlayerView
    private var activeYouTubePlayer: YouTubePlayer? = null

    private lateinit var txtAnimeTitle: TextView
    private lateinit var txtEpisodeTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var containerNextEpisode: View
    private lateinit var txtNextEpTitle: TextView
    private lateinit var imgNextEpThumbnail: ImageView
    private lateinit var rootViewRef: View

    companion object {
        private const val ARG_ANIME_ID = "arg_anime_id"
        private const val ARG_EP_INDEX = "arg_ep_index"

        // FIX: Sekarang menerima String (ID Anime) agar cocok dengan MainActivity kamu
        fun newInstance(animeId: String, episodeIndex: Int): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putString(ARG_ANIME_ID, animeId)
            args.putInt(ARG_EP_INDEX, episodeIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FIX: Menggunakan getString untuk menghindari warning getSerializable deprecated
        animeId = arguments?.getString(ARG_ANIME_ID).orEmpty()
        episodeIndex = arguments?.getInt(ARG_EP_INDEX) ?: 0
        
        // Mengambil data anime dari pangkalan MockData berdasarkan ID yang dikirim
        anime = MockData.allAnime.find { it.id == animeId }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        rootViewRef = view

        val currentAnime = anime ?: return view

        youtubePlayerView = view.findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youtubePlayerView)

        txtAnimeTitle = view.findViewById(R.id.txt_player_anime_title)
        txtEpisodeTitle = view.findViewById(R.id.txt_player_episode_title)
        txtDescription = view.findViewById(R.id.txt_player_description)
        containerNextEpisode = view.findViewById(R.id.container_next_episode)
        txtNextEpTitle = view.findViewById(R.id.txt_next_ep_title)
        imgNextEpThumbnail = view.findViewById(R.id.img_next_ep_thumbnail)

        txtAnimeTitle.text = currentAnime.title
        txtDescription.text = currentAnime.description
        view.findViewById<TextView>(R.id.txt_player_meta_badge).text = "${currentAnime.ageRating} • ${currentAnime.dubSubText}"

        if (currentAnime.episodes.isEmpty()) {
            FirestoreHelper().getEpisodesForAnime(currentAnime.id, { fetchedEpisodes: List<EpisodeModel> ->
                currentAnime.episodes = fetchedEpisodes
                setupEpisodeDataAndPlayer(currentAnime, view)
            }, { _: Exception ->
                Toast.makeText(context, "Gagal memuat episode dinamis", Toast.LENGTH_SHORT).show()
            })
        } else {
            setupEpisodeDataAndPlayer(currentAnime, view)
        }

        view.findViewById<View>(R.id.btn_player_back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        setupLikeDislikeSystem(view)
        setupStaticButtonsToast(view, currentAnime)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            FirestoreHelper().addToWatchHistory(userId, currentAnime.id) { }
        }

        return view
    }

    private fun setupEpisodeDataAndPlayer(currentAnime: AnimeModel, view: View) {
        val episodeList = currentAnime.episodes
        if (episodeList.isEmpty()) return

        val activeEpisode = episodeList.getOrNull(episodeIndex) ?: episodeList[0]
        txtEpisodeTitle.text = "E${activeEpisode.number} - ${activeEpisode.name}"

        initializeYouTubePlayer(activeEpisode.youtubeVideoId)

        val nextEpIndex = episodeIndex + 1
        val nextEpisode = episodeList.getOrNull(nextEpIndex)
        if (nextEpisode != null) {
            containerNextEpisode.visibility = View.VISIBLE
            txtNextEpTitle.text = "${nextEpisode.number}. ${nextEpisode.name}"
            view.findViewById<TextView>(R.id.txt_next_ep_duration).text = nextEpisode.durationRemaining

            // Memuat gambar thumbnail secara dinamis menggunakan Coil
            if (nextEpisode.imageUrl.isNotEmpty()) {
                imgNextEpThumbnail.load(nextEpisode.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_banner)
                }
            }

            val loadNext = View.OnClickListener { navigateToEpisode(nextEpIndex) }
            containerNextEpisode.setOnClickListener(loadNext)
        } else {
            containerNextEpisode.visibility = View.GONE
        }
    }

    private fun initializeYouTubePlayer(youtubeVideoId: String) {
        val finalVideoId = if (youtubeVideoId.isBlank()) "dQw4w9WgXcQ" else youtubeVideoId

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                activeYouTubePlayer = youTubePlayer
                youTubePlayer.loadVideo(finalVideoId, 0f)
            }
        })
    }

    private fun navigateToEpisode(index: Int) {
        val act = activity as? MainActivity
        val curAnimeId = animeId
        if (act != null && curAnimeId.isNotEmpty()) {
            act.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newInstance(curAnimeId, index))
                .commit()
        }
    }

    private fun setupLikeDislikeSystem(view: View) {
        val txtLikeCount = view.findViewById<TextView>(R.id.txt_player_like_count)
        val txtDislikeCount = view.findViewById<TextView>(R.id.txt_player_dislike_count)
        val imgLike = view.findViewById<ImageView>(R.id.img_player_like)
        val imgDislike = view.findViewById<ImageView>(R.id.img_player_dislike)

        var liked = false
        var disliked = false
        var likes = 28
        var dislikes = 2

        view.findViewById<View>(R.id.btn_player_like).setOnClickListener {
            if (liked) {
                liked = false; likes--; imgLike.tintWithColor(0xFFFFFFFF.toInt())
            } else {
                liked = true; likes++; imgLike.tintWithColor(0xFFFF6D00.toInt())
                if (disliked) { disliked = false; dislikes--; imgDislike.tintWithColor(0xFFFFFFFF.toInt()) }
            }
            txtLikeCount.text = likes.toString()
            txtDislikeCount.text = dislikes.toString()
        }

        view.findViewById<View>(R.id.btn_player_dislike).setOnClickListener {
            if (disliked) {
                disliked = false; dislikes--; imgDislike.tintWithColor(0xFFFFFFFF.toInt())
            } else {
                disliked = true; dislikes++; imgDislike.tintWithColor(0xFFFF6D00.toInt())
                if (liked) { liked = false; likes--; imgLike.tintWithColor(0xFFFFFFFF.toInt()) }
            }
            txtLikeCount.text = likes.toString()
            txtDislikeCount.text = dislikes.toString()
        }
    }

    private fun setupStaticButtonsToast(view: View, currentAnime: AnimeModel) {
        view.findViewById<View>(R.id.btn_player_download).setOnClickListener { addAnimeToDownloads(currentAnime) }
        view.findViewById<View>(R.id.btn_next_ep_download).setOnClickListener { addAnimeToDownloads(currentAnime) }
        view.findViewById<View>(R.id.btn_player_more_options).setOnClickListener { AppNotifier.show(view, "Fitur opsi lainnya segera hadir") }
        view.findViewById<View>(R.id.btn_player_read_more).setOnClickListener { AppNotifier.show(view, currentAnime.description) }
        view.findViewById<View>(R.id.btn_player_all_episodes).setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    private fun addAnimeToDownloads(currentAnime: AnimeModel) {
        if (!MockData.downloads.any { it.id == currentAnime.id }) {
            MockData.downloads.add(currentAnime)
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (userId.isNotEmpty()) {
                FirestoreHelper().addToDownloads(userId, currentAnime.id) { }
            }
            AppNotifier.show(rootViewRef, "${currentAnime.title} berhasil diunduh!")
        } else {
            AppNotifier.show(rootViewRef, "${currentAnime.title} sudah diunduh sebelumnya")
        }
    }

    private fun ImageView.tintWithColor(color: Int) {
        this.imageTintList = android.content.res.ColorStateList.valueOf(color)
    }
}
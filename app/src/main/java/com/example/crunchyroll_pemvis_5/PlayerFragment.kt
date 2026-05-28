package com.example.crunchyroll_pemvis_5

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class PlayerFragment : Fragment() {

    private var anime: AnimeModel? = null
    private var episodeIndex: Int = 0

    private lateinit var videoView: VideoView
    private lateinit var controlsOverlay: View
    private lateinit var controlsLayout: View
    private lateinit var imgPlayPause: ImageView
    private lateinit var seekProgress: SeekBar
    private lateinit var txtCurrentTime: TextView
    private lateinit var txtTotalTime: TextView

    private lateinit var txtAnimeTitle: TextView
    private lateinit var txtEpisodeTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var containerNextEpisode: View
    private lateinit var txtNextEpTitle: TextView
    private lateinit var rootViewRef: View

    // Mock mode parameters if video fails to load or offline
    private var isMockMode = false
    private var mockCurrentPosition = 0
    private val mockTotalDuration = 1435 // 23:55 in seconds

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressRunnable = object : Runnable {
        override fun run() {
            if (isMockMode) {
                if (videoView.isPlaying) {
                    mockCurrentPosition++
                    if (mockCurrentPosition >= mockTotalDuration) {
                        mockCurrentPosition = 0
                    }
                }
                updateProgressUI(mockCurrentPosition * 1000, mockTotalDuration * 1000)
            } else {
                if (videoView.isPlaying) {
                    val current = videoView.currentPosition
                    val duration = videoView.duration
                    updateProgressUI(current, if (duration > 0) duration else mockTotalDuration * 1000)
                }
            }
            handler.postDelayed(this, 1000)
        }
    }

    private val hideControlsRunnable = Runnable {
        hideControls()
    }

    companion object {
        private const val ARG_ANIME = "arg_anime"
        private const val ARG_EP_INDEX = "arg_ep_index"

        fun newInstance(anime: AnimeModel, episodeIndex: Int): PlayerFragment {
            val fragment = PlayerFragment()
            val args = Bundle()
            args.putSerializable(ARG_ANIME, anime)
            args.putInt(ARG_EP_INDEX, episodeIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        anime = arguments?.getSerializable(ARG_ANIME) as? AnimeModel
        episodeIndex = arguments?.getInt(ARG_EP_INDEX) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player, container, false)
        rootViewRef = view

        val currentAnime = anime ?: return view
        val episodeList = currentAnime.episodes
        if (episodeList.isEmpty()) return view
        val activeEpisode = episodeList.getOrNull(episodeIndex) ?: episodeList[0]

        // UI Bindings
        videoView = view.findViewById(R.id.video_view)
        controlsOverlay = view.findViewById(R.id.player_controls_overlay)
        controlsLayout = view.findViewById(R.id.player_controls_layout)
        imgPlayPause = view.findViewById(R.id.img_player_play_pause)
        seekProgress = view.findViewById(R.id.seekbar_player_progress)
        txtCurrentTime = view.findViewById(R.id.txt_player_current_time)
        txtTotalTime = view.findViewById(R.id.txt_player_total_time)

        txtAnimeTitle = view.findViewById(R.id.txt_player_anime_title)
        txtEpisodeTitle = view.findViewById(R.id.txt_player_episode_title)
        txtDescription = view.findViewById(R.id.txt_player_description)
        containerNextEpisode = view.findViewById(R.id.container_next_episode)
        txtNextEpTitle = view.findViewById(R.id.txt_next_ep_title)

        // Set static/dynamic info
        txtAnimeTitle.text = currentAnime.title
        txtEpisodeTitle.text = "E${activeEpisode.number} - ${activeEpisode.name}"
        txtDescription.text = currentAnime.description
        view.findViewById<TextView>(R.id.txt_player_meta_badge).text = "${currentAnime.ageRating} • ${currentAnime.dubSubText}"

        // Back action
        view.findViewById<View>(R.id.btn_player_back).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Like / Dislike dynamics
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
                liked = false
                likes--
                imgLike.tintWithColor(0xFFFFFFFF.toInt())
            } else {
                liked = true
                likes++
                imgLike.tintWithColor(0xFFFF6D00.toInt()) // Accent Color Orange
                if (disliked) {
                    disliked = false
                    dislikes--
                    imgDislike.tintWithColor(0xFFFFFFFF.toInt())
                }
            }
            txtLikeCount.text = likes.toString()
            txtDislikeCount.text = dislikes.toString()
        }

        view.findViewById<View>(R.id.btn_player_dislike).setOnClickListener {
            if (disliked) {
                disliked = false
                dislikes--
                imgDislike.tintWithColor(0xFFFFFFFF.toInt())
            } else {
                disliked = true
                dislikes++
                imgDislike.tintWithColor(0xFFFF6D00.toInt())
                if (liked) {
                    liked = false
                    likes--
                    imgLike.tintWithColor(0xFFFFFFFF.toInt())
                }
            }
            txtLikeCount.text = likes.toString()
            txtDislikeCount.text = dislikes.toString()
        }

        // Next Episode setup
        val nextEpIndex = episodeIndex + 1
        val nextEpisode = episodeList.getOrNull(nextEpIndex)
        if (nextEpisode != null) {
            containerNextEpisode.visibility = View.VISIBLE
            txtNextEpTitle.text = "${nextEpisode.number}. ${nextEpisode.name}"
            
            val loadNext = View.OnClickListener {
                navigateToEpisode(nextEpIndex)
            }
            containerNextEpisode.setOnClickListener(loadNext)
            view.findViewById<View>(R.id.btn_player_toolbar_next_ep).setOnClickListener(loadNext)
            
            view.findViewById<View>(R.id.btn_next_ep_download).setOnClickListener {
                addAnimeToDownloads(currentAnime)
            }
        } else {
            containerNextEpisode.visibility = View.GONE
            view.findViewById<View>(R.id.btn_player_toolbar_next_ep).visibility = View.GONE
        }

        // Other static buttons toast
        view.findViewById<View>(R.id.btn_player_download).setOnClickListener {
            addAnimeToDownloads(currentAnime)
        }
        view.findViewById<View>(R.id.btn_player_more_options).setOnClickListener {
            AppNotifier.show(view, "Fitur opsi lainnya segera hadir")
        }
        view.findViewById<View>(R.id.btn_player_settings).setOnClickListener {
            AppNotifier.show(view, "Kualitas: Auto • Subtitle: ${MockData.subtitleLanguage}")
        }
        view.findViewById<View>(R.id.btn_player_cast).setOnClickListener {
            AppNotifier.show(view, "Mencari perangkat Chromecast...")
        }
        view.findViewById<View>(R.id.btn_player_fullscreen).setOnClickListener {
            AppNotifier.show(view, "Layar penuh diaktifkan")
        }
        view.findViewById<View>(R.id.btn_player_all_episodes).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Setup Video View Playback
        setupVideoPlayback()

        // Interaction touch anywhere on Video container to show controls
        view.findViewById<View>(R.id.player_video_container).setOnClickListener {
            if (controlsLayout.visibility == View.VISIBLE) {
                hideControls()
            } else {
                showControls()
            }
        }

        // Seekbar changes
        seekProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = if (isMockMode) mockTotalDuration * 1000 else videoView.duration
                    val targetPos = (progress.toFloat() / 1000f * duration).toInt()
                    if (isMockMode) {
                        mockCurrentPosition = targetPos / 1000
                        updateProgressUI(targetPos, duration)
                    } else {
                        videoView.seekTo(targetPos)
                    }
                    resetHideTimer()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(hideControlsRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                resetHideTimer()
            }
        })

        // Rewind & Forward
        view.findViewById<View>(R.id.btn_player_rewind_10).setOnClickListener {
            seekRelative(-10)
            resetHideTimer()
        }
        view.findViewById<View>(R.id.btn_player_forward_10).setOnClickListener {
            seekRelative(10)
            resetHideTimer()
        }

        // Big Play/Pause Toggle
        view.findViewById<View>(R.id.btn_player_play_pause).setOnClickListener {
            togglePlayPause()
            resetHideTimer()
        }

        // Firestore Integration: Add to watch history on load
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            FirestoreHelper().addToWatchHistory(userId, currentAnime.id) { }
        }

        return view
    }

    private fun setupVideoPlayback() {
        // Sample video streaming link
        val sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val episodeVideoUrl = anime?.episodes?.getOrNull(episodeIndex)?.videoUrl
        val selectedUrl = if (episodeVideoUrl.isNullOrBlank()) sampleVideoUrl else episodeVideoUrl
        videoView.setVideoURI(Uri.parse(selectedUrl))

        videoView.setOnPreparedListener { mp ->
            isMockMode = false
            videoView.start()
            imgPlayPause.setImageResource(R.drawable.ic_close) // We can use close or make a play pause icon toggle
            imgPlayPause.tintWithColor(0xFFFFFFFF.toInt())
            showControls()
            resetHideTimer()
        }

        videoView.setOnErrorListener { _, _, _ ->
            // Fallback to Mock simulation mode if offline or link broken
            isMockMode = true
            mockCurrentPosition = 0
            videoView.stopPlayback()
            imgPlayPause.setImageResource(R.drawable.ic_play)
            AppNotifier.show(rootViewRef, "Mode simulasi: memutar secara lokal")
            
            // Toggle start play in mock mode
            videoView.start() // will trigger standard updates
            true
        }
    }

    private fun togglePlayPause() {
        if (isMockMode) {
            // Mock mode playing state toggled
            if (videoView.isPlaying) {
                videoView.pause()
                imgPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                videoView.start()
                imgPlayPause.setImageResource(R.drawable.ic_close)
            }
        } else {
            if (videoView.isPlaying) {
                videoView.pause()
                imgPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                videoView.start()
                imgPlayPause.setImageResource(R.drawable.ic_close)
            }
        }
    }

    private fun seekRelative(seconds: Int) {
        val duration = if (isMockMode) mockTotalDuration * 1000 else videoView.duration
        val current = if (isMockMode) mockCurrentPosition * 1000 else videoView.currentPosition
        var target = current + (seconds * 1000)
        if (target < 0) target = 0
        if (target > duration) target = duration

        if (isMockMode) {
            mockCurrentPosition = target / 1000
            updateProgressUI(target, duration)
        } else {
            videoView.seekTo(target)
        }
    }

    private fun updateProgressUI(currentMs: Int, durationMs: Int) {
        val currentSec = currentMs / 1000
        val durationSec = durationMs / 1000

        txtCurrentTime.text = formatTime(currentSec)
        txtTotalTime.text = formatTime(durationSec)

        val progress = if (durationSec > 0) (currentSec.toFloat() / durationSec.toFloat() * 1000).toInt() else 0
        seekProgress.progress = progress
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }

    private fun showControls() {
        controlsOverlay.visibility = View.VISIBLE
        controlsLayout.visibility = View.VISIBLE
        resetHideTimer()
    }

    private fun hideControls() {
        controlsOverlay.visibility = View.GONE
        controlsLayout.visibility = View.GONE
    }

    private fun resetHideTimer() {
        handler.removeCallbacks(hideControlsRunnable)
        handler.postDelayed(hideControlsRunnable, 4000)
    }

    private fun navigateToEpisode(index: Int) {
        val act = activity as? MainActivity
        val curAnime = anime
        if (act != null && curAnime != null) {
            // Swap this player fragment with a new one for the next episode
            act.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newInstance(curAnime, index))
                .commit()
        }
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

    override fun onResume() {
        super.onResume()
        handler.post(updateProgressRunnable)
    }

    override fun onPause() {
        super.onPause()
        videoView.pause()
        handler.removeCallbacks(updateProgressRunnable)
        handler.removeCallbacks(hideControlsRunnable)
    }

    // Helper extension to tint ImageViews dynamically
    private fun ImageView.tintWithColor(color: Int) {
        this.imageTintList = android.content.res.ColorStateList.valueOf(color)
    }
}

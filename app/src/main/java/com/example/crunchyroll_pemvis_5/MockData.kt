package com.example.crunchyroll_pemvis_5

import android.graphics.Color

object MockData {
    // ========================================================================
    // CONTAINER STATE UTAMA (Preferensi Pengguna & Integrasi Halaman)
    // ========================================================================
    val createdCrunchylists = mutableListOf<CrunchylistModel>()
    var activeSubscriptionPlan: String = "Gratis"

    // Profile preferences state
    var profileName: String = "Advan"
    var profileUsername: String = "advan_p"
    var activeContentRestriction: String = "Semua Konten"
    var isPinProfileEnabled: Boolean = false
    var isClosedCaptionsEnabled: Boolean = false
    var streamCellularEnabled: Boolean = false
    var downloadCellularEnabled: Boolean = false
    var audioLanguage: String = "Bahasa Indonesia"
    var subtitleLanguage: String = "English"
    var audioDescriptionEnabled: Boolean = false
    var downloadQuality: Int = 0 // 0=Tinggi, 1=Sedang, 2=Rendah

    // ------------------------------------------------------------------------
    // TAMBAHAN FITUR MULTI-PROFIL DINAMIS
    // ------------------------------------------------------------------------
    val userProfiles = mutableListOf<ProfileModel>(
        ProfileModel("p1", "Profil Utama", "") // Profil bawaan saat pertama kali aplikasi dibuka
    )
    var activeProfile: ProfileModel? = userProfiles.firstOrNull()
    // ------------------------------------------------------------------------

    // Akun & State Integrasi Halaman
    var loggedInEmail: String = "advan_p@gmail.com"
    var userPassword: String = "password123"
    var selectedSortOption: String = "Terbaru"
    var selectedGenreFilter: String = "Semua"
    var selectedSimulcastSeason: String = "Musim Dingin 2026"
    
    val watchlist = mutableListOf<AnimeModel>()
    val watchHistory = mutableListOf<AnimeModel>()
    val downloads = mutableListOf<AnimeModel>()
    val searchHistory = mutableListOf<String>()

    // ========================================================================
    // DATA REAL ANIME (POSISI URUT BENAR & AMAN)
    // ========================================================================
    val allAnime = mutableListOf(
        // 1. Jujutsu Kaisen
        createAnimeWithEpisodes(
            "jujutsu_kaisen", "Jujutsu Kaisen", "Selesai Tayang • 2020",
            "R - 17+", "Takarir Resmi", 8.50f, "2.007.541 pengguna", "Aksi",
            "Idly memanjakan diri dalam kegiatan paranormal tak berdasar dengan Klub Gaib, siswa sekolah menengah Yuuji Itadori spends his days either at the club room or hospital...",
            "https://comenian.org/wp-content/uploads/2024/01/unnamed-87.png", "pkKu9hLT-t8", 24, "#2E153B"
        ),
        // 2. My Hero Academia Season 7
        createAnimeWithEpisodes(
            "my_hero_academia", "My Hero Academia Season 7", "Selesai Tayang • 2024",
            "PG-13", "Takarir Resmi", 8.14f, "229.925 pengguna", "Aksi",
            "Setelah pertempuran habis-habisan dengan Front Pembebasan Paranormal, sulit bagi rakyat Jepang untuk terus menaruh kepercayaan pada pahlawan mereka...",
            "https://api.duniagames.co.id/optimize-image?url=https%3A%2F%2Fapi.duniagames.co.id%2Fapi%2Fcontent%2Fupload%2Ffile%2F5613032711702275733.jpg&format=webp&width=736&signature=d4aa7252f117cf340023e8885cbe6e290d7c932038efb79cc5289d063c4834ed", "T5HMoxJRhRY", 13, "#0F3D1C"
        ),
        // 3. Attack on Titan: Final Season
        createAnimeWithEpisodes(
            "attack_on_titan", "Attack on Titan: Final Season", "Selesai Tayang • 2020",
            "R - 17+", "Takarir Resmi", 8.79f, "1.526.975 pengguna", "Aksi",
            "Gabi Braun dan Falco Grice telah melatih seluruh hidup mereka untuk mewarisi salah satu dari tujuh Titan di bawah kendali Marley...",
            "https://m.media-amazon.com/images/S/pv-target-images/abce4f3146841f9aa66e9963531ab38e64108ecaa79b93e5a2781c89c59ebaac.jpg", "M_OauHnAFc8", 16, "#2E1C16"
        ),
        // 4. Solo Leveling
        createAnimeWithEpisodes(
            "solo-leveling", "Solo Leveling", "Selesai Tayang • 2024",
            "R - 17+", "Takarir Resmi", 8.15f, "712.330 pengguna", "Aksi",
            "Umat manusia terjebak di jurang satu dekade yang lalu ketika gerbang—portal pertama yang dihubungkan dengan dimensi lain muncul...",
            "https://i.pinimg.com/736x/00/bf/a5/00bfa50817ba4107538d2f79da3e20c4.jpg", "I6JIwjWOhnQ", 12, "#0C2E5C"
        ),
        // 5. Tokyo Revengers: Tenjiku Arc
        createAnimeWithEpisodes(
            "tokyo_revengers", "Tokyo Revengers: Tenjiku Arc", "Selesai Tayang • 2023",
            "R - 17+", "Takarir Resmi", 7.74f, "202.441 pengguna", "Aksi",
            "Mantan penjahat Takemichi Hanagaki terus mencoba mengubah masa lalu demi menyelamatkan pacarnya, Hinata Tachibana...",
            "https://external-preview.redd.it/tokyo-revengers-tenjiku-arc-season-3-new-key-visual-v0-NcmKdCXUxIW1ibEGUYN2Eq5Zwgmlh6AxKO19tqalCDw.jpg?auto=webp&s=597a92e42ba84e584e7efcc4621c74d7bab62b84", "LVpiqOh-524", 13, "#1F1F1F"
        ),
        // 6. Classroom of the Elite
        createAnimeWithEpisodes(
            "classroom_elite", "Classroom of the Elite", "Selesai Tayang • 2017",
            "PG-13", "Takarir Resmi", 7.86f, "1.200.540 pengguna", "Drama",
            "Kiyotaka Ayanokouji mendaftar di SMA Tokyo Kodo Ikusei yang bergengsi, di mana semua siswanya dijamin masuk universitas atau kerja...",
            "https://m.media-amazon.com/images/M/MV5BY2U2NWU5MzMtOGY5Ni00MGI5LWFkZDYtMGNlN2RhMGRhNGZkXkEyXkFqcGc@._V1_.jpg", "NO-IhnTNSDM", 12, "#1A1A2E"
        ),
        // 7. Horimiya
        createAnimeWithEpisodes(
            "horimiya", "Horimiya", "Selesai Tayang • 2021",
            "PG-13", "Takarir Resmi", 8.20f, "945.300 pengguna", "Romantis",
            "Kombinasi romansa kehidupan sekolah yang manis antara Kyouko Hori yang populer dan Izumi Miyamura yang kutubuku...",
            "https://i.pinimg.com/736x/86/62/4b/86624b5cec4c124d661792faad4b4e1b.jpg", "tryurXgGqkI", 13, "#4A3E3D"
        ),
        // 8. Your Name.
        createAnimeWithEpisodes(
            "your_name", "Your Name.", "Movie • 2016",
            "PG-13", "Takarir Resmi", 8.85f, "2.540.110 pengguna", "Romantis",
            "Mitsuha Miyamizu, seorang gadis sekolah menengah di pedesaan, dan Taki Tachibana, seorang anak laki-laki di Tokyo, tiba-tiba bertukar tubuh...",
            "https://m.media-amazon.com/images/M/MV5BMTIyNzFjNzItZmQ1MC00NzhjLThmMzYtZjRhN2Y3MmM2OGQyXkEyXkFqcGc@._V1_.jpg", "xU47nhruN-Q", 1, "#1D2D44"
        ),
        // 9. Dr. Stone: Science Future Part 2
        createAnimeWithEpisodes(
            "dr_stone", "Dr. Stone: Science Future Part 2", "Selesai Tayang • 2023",
            "PG-13", "Takarir Resmi", 8.31f, "480.200 pengguna", "Fiksi Ilmiah",
            "Senku Ishigami dan Kerajaan Sains-nya bersiap meluncurkan misi luar biasa ke luar angkasa demi memecahkan misteri pembatuan global...",
            "https://static.animecorner.me/2025/07/3305-c5ea1182-3b10-11f1-a86d-c602ef77c9d1.jpg", "W5VvkyJ7XB0", 12, "#154C8F"
        ),
        // 10. Fruits Basket: The Final Season
        createAnimeWithEpisodes(
            "fruits_basket", "Fruits Basket: The Final Season", "Selesai Tayang • 2021",
            "PG-13", "Takarir Resmi", 9.04f, "415.600 pengguna", "Drama",
            "Kesimpulan akhir emosional dari kutukan keluarga Souma dan ikatan tulus mereka bersama Tooru Honda...",
            "https://image.tmdb.org/t/p/original/5APKAxTU8b33GgLlp22gwviM22b.jpg", "QIUTJ2E0LYk", 13, "#3A2E2B"
        ),
        // 11. My Dress-Up Darling
        createAnimeWithEpisodes(
            "my_dress_up_darling", "My Dress-Up Darling", "Selesai Tayang • 2022",
            "R - 17+", "Takarir Resmi", 8.22f, "650.400 pengguna", "Romantis",
            "Wakana Gojou yang pemalu suka membuat boneka Hina, dipertemukan dengan Marin Kitagawa, gadis populer penggila cosplay...",
            "https://m.media-amazon.com/images/M/MV5BYmJhYjgzYTQtMzMxYi00NTkyLWI2MWItNTgxNjk1NzU0YmQzXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg", "4fR_WMYKvfc", 12, "#540E0E"
        ),
        // 12. Rent-a-Girlfriend Season 2
        createAnimeWithEpisodes(
            "rent_girlfriend", "Rent-a-Girlfriend Season 2", "Selesai Tayang • 2022",
            "PG-13", "Takarir Resmi", 6.69f, "200.750 pengguna", "Komedi",
            "Kazuya Kinoshita melanjutkan hubungan pacar sewaan rumitnya bersama Chizuru Mizuhara di tengah gangguan Mami dan Ruka setiap minggu...",
            "https://i0.wp.com/anitrendz.net/news/wp-content/uploads/2022/06/rentagirlfriendseason2_mainkeyvisual.jpg?resize=696%2C984&ssl=1", "3-gRdzoM4I8", 12, "#5C4033"
        ),
        // 13. Your Lie in April
        createAnimeWithEpisodes(
            "your_lie_in_april", "Your Lie in April", "Selesai Tayang • 2014",
            "PG-13", "Takarir Resmi", 8.64f, "1.475.768 pengguna", "Drama",
            "Kousei Arima kehilangan kemampuan mendengar piano setelah trauma kematian ibunya, hingga hidupnya diwarnai kembali oleh Kaori Miyazono...",
            "https://m.media-amazon.com/images/M/MV5BZGMyYmFmNzgtMWQ4NS00MWE2LTg4YmEtZGY1MTBiODE0YmE5XkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg", "3aL0gDZtFbE", 22, "#4A4737"
        )
    )

    // ========================================================================
    // LOGIKA FILTER KATEGORI DINAMIS
    // ========================================================================
    val trendingAnime: List<AnimeModel> get() = allAnime.take(3)
    val exclusiveAnime: List<AnimeModel> get() = allAnime.subList(3, 6.coerceAtMost(allAnime.size))
    val bannerAnime: List<AnimeModel> get() = listOf(allAnime[8], allAnime[0], allAnime[1], allAnime[2])

    val animeByGenre: Map<String, List<AnimeModel>> by lazy {
        val genres = listOf("Aksi", "Drama", "Romantis")
        genres.associateWith { genreName ->
            allAnime.filter { anime -> 
                when (genreName) {
                    "Aksi" -> anime.id in listOf("jujutsu_kaisen", "my_hero_academia", "attack_on_titan", "solo-leveling", "tokyo_revengers")
                    "Romantis" -> anime.id in listOf("horimiya", "your_name", "my_dress_up_darling")
                    "Drama" -> anime.id in listOf("classroom_elite", "fruits_basket", "your_lie_in_april")
                    else -> false
                }
            }
        }
    }

    // ========================================================================
    // LOGIKA PENCARIAN & AUTOMATION BUILDER EPISODE
    // ========================================================================
    fun searchAnime(query: String): List<AnimeModel> {
        if (query.trim().isEmpty()) return emptyList()
        return allAnime.filter { 
            it.title.contains(query, ignoreCase = true) || 
            it.subtitle.contains(query, ignoreCase = true) 
        }
    }

    private fun createAnimeWithEpisodes(
        id: String, title: String, subtitle: String, ageRating: String, dubSubText: String,
        ratingStar: Float, reviews: String, genre: String, description: String, 
        imageUrl: String, youtubeId: String, totalEpisodes: Int, colorHex: String
    ): AnimeModel {
        val convertedStars = (ratingStar / 2f).coerceAtMost(5.0f)
        val parsedColor = Color.parseColor(colorHex)

        val prefix = when(id) {
            "jujutsu_kaisen" -> "jjk"
            "my_hero_academia" -> "mha"
            "attack_on_titan" -> "aot"
            "solo-leveling" -> "sl"
            "tokyo_revengers" -> "tr"
            "classroom_elite" -> "cote"
            "horimiya" -> "hori"
            "your_name" -> "yn"
            "dr_stone" -> "drst"
            "fruits_basket" -> "frbk"
            "my_dress_up_darling" -> "mdud"
            "rent_girlfriend" -> "rag"
            "your_lie_in_april" -> "ylia"
            else -> "ep"
        }

        val episodeList = List(totalEpisodes) { index ->
            val epNum = index + 1
            EpisodeModel(
                id = "${prefix}_eps$epNum",
                number = epNum,
                name = "Episode $epNum",
                durationRemaining = if (totalEpisodes == 1) "1h 46m" else "23m",
                thumbnailColor = parsedColor,
                videoUrl = "",
                youtubeVideoId = youtubeId,
                imageUrl = imageUrl
            )
        }

        return AnimeModel(
            id = id, title = title, subtitle = subtitle, ageRating = ageRating,
            dubSubText = dubSubText, ratingStars = convertedStars, ratingReviews = reviews,
            description = description, placeholderColor = parsedColor,
            episodes = episodeList, imageUrl = imageUrl
        )
    }
}
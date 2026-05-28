package com.example.crunchyroll_pemvis_5

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    private fun getAnimeIdsFromCollection(
        collection: String,
        userId: String,
        animeField: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collection)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ids = result.mapNotNull { it.getString(animeField) }
                onSuccess(ids)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Menyimpan/Memperbarui Data User Baru
    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(user.uid)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun saveCurrentUserSnapshot(userId: String, onComplete: (Boolean) -> Unit = {}) {
        val snapshot = User(
            uid = userId,
            username = MockData.profileUsername,
            email = MockData.loggedInEmail,
            profileName = MockData.profileName,
            membershipType = if (MockData.activeSubscriptionPlan.equals("Gratis", ignoreCase = true)) "Free" else MockData.activeSubscriptionPlan,
            contentRatingRestriction = MockData.activeContentRestriction,
            audioLanguage = MockData.audioLanguage,
            subtitleLanguage = MockData.subtitleLanguage,
            audioDescriptionEnabled = MockData.audioDescriptionEnabled,
            downloadQuality = MockData.downloadQuality,
            streamCellularEnabled = MockData.streamCellularEnabled,
            downloadCellularEnabled = MockData.downloadCellularEnabled
        )
        saveUser(snapshot, onComplete)
    }

    // Mengambil Data Profil User
    fun getUserProfile(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document.toObject(User::class.java))
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Mengambil Katalog Anime
    fun getAnimeCatalog(onSuccess: (List<Anime>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("anime").get()
            .addOnSuccessListener { result ->
                val list = result.map { it.toObject(Anime::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Menambah Anime ke Watchlist (Daftar Tonton)
    fun addToWatchlist(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val watchlistId = "${userId}_${animeId}"
        val data = mapOf(
            "watchlistId" to watchlistId,
            "userId" to userId,
            "animeId" to animeId,
            "addedAt" to com.google.firebase.Timestamp.now()
        )
        db.collection("watchlists").document(watchlistId)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Menghapus Anime dari Watchlist
    fun removeFromWatchlist(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val watchlistId = "${userId}_${animeId}"
        db.collection("watchlists").document(watchlistId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Mengisi database Firestore secara otomatis (Seeding)
    fun seedAnimeDatabase(onComplete: (Boolean) -> Unit) {
        db.collection("anime").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    val batch = db.batch()
                    for (model in MockData.allAnime) {
                        val docRef = db.collection("anime").document(model.id)
                        val animeData = mapOf(
                            "animeId" to model.id,
                            "title" to model.title,
                            "description" to model.description,
                            "imageUrl" to when(model.id) {
                                "tye" -> "to_your_eternity"
                                "dsr" -> "daemons_shadow"
                                "strongest" -> "strongest_job"
                                "shy" -> "shy"
                                "mha" -> "naruto"
                                "aot" -> "sl"
                                "drstone" -> "dr_stone_banner"
                                else -> "to_your_eternity"
                            },
                            "bannerUrl" to when(model.id) {
                                "drstone" -> "dr_stone_banner"
                                else -> "to_your_eternity"
                            },
                            "genres" to when(model.id) {
                                "tye" -> listOf("Drama", "Fantasi", "Fiksi Ilmiah")
                                "dsr" -> listOf("Aksi", "Petualangan", "Supranatural")
                                "strongest" -> listOf("Fantasi", "Komedi")
                                "shy" -> listOf("Aksi", "Super hero")
                                "mha" -> listOf("Aksi", "Fiksi Ilmiah")
                                "aot" -> listOf("Aksi", "Drama", "Thriller")
                                "drstone" -> listOf("Petualangan", "Sains", "Fiksi Ilmiah")
                                else -> emptyList()
                            },
                            "rating" to model.ratingStars.toDouble(),
                            "ratingCount" to model.ratingReviews,
                            "status" to "Ongoing",
                            "releaseYear" to 2026,
                            "simulcastSeason" to "Musim Dingin 2026",
                            "isSimulcast" to (model.id in listOf("tye", "dsr", "strongest"))
                        )
                        batch.set(docRef, animeData)
                    }
                    batch.commit()
                        .addOnSuccessListener { onComplete(true) }
                        .addOnFailureListener { onComplete(false) }
                } else {
                    onComplete(true)
                }
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    // Menambah Anime ke Riwayat Tontonan
    fun addToWatchHistory(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val historyId = "${userId}_${animeId}"
        val data = mapOf(
            "historyId" to historyId,
            "userId" to userId,
            "animeId" to animeId,
            "watchedAt" to com.google.firebase.Timestamp.now()
        )
        db.collection("watch_history").document(historyId)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Menambah Anime ke Unduhan User
    fun addToDownloads(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val downloadId = "${userId}_${animeId}"
        val data = mapOf(
            "downloadId" to downloadId,
            "userId" to userId,
            "animeId" to animeId,
            "downloadedAt" to com.google.firebase.Timestamp.now()
        )
        db.collection("downloads").document(downloadId)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Menghapus Anime dari Unduhan User
    fun removeFromDownloads(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val downloadId = "${userId}_${animeId}"
        db.collection("downloads").document(downloadId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Menyimpan Playlist Kustom Baru (Crunchylist)
    fun saveCrunchylist(userId: String, listName: String, onComplete: (Boolean, String) -> Unit) {
        val id = "${userId}_${listName.replace(" ", "_").lowercase()}_${System.currentTimeMillis()}"
        val data = mapOf(
            "crunchylistId" to id,
            "userId" to userId,
            "listName" to listName,
            "animeIds" to emptyList<String>(),
            "createdAt" to com.google.firebase.Timestamp.now()
        )
        db.collection("crunchylists").document(id)
            .set(data)
            .addOnSuccessListener { onComplete(true, id) }
            .addOnFailureListener { onComplete(false, id) }
    }

    // Mengambil Semua Crunchylist untuk User
    fun getCrunchylists(userId: String, onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("crunchylists")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { it.data }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getWatchlistAnimeIds(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        getAnimeIdsFromCollection(
            collection = "watchlists",
            userId = userId,
            animeField = "animeId",
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getWatchHistoryAnimeIds(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        getAnimeIdsFromCollection(
            collection = "watch_history",
            userId = userId,
            animeField = "animeId",
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getDownloadAnimeIds(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        getAnimeIdsFromCollection(
            collection = "downloads",
            userId = userId,
            animeField = "animeId",
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun getCrunchylistNames(
        userId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("crunchylists")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val listNames = result.mapNotNull { it.getString("listName") }
                onSuccess(listNames)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun getCrunchylistsDetailed(
        userId: String,
        onSuccess: (List<CrunchylistModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("crunchylists")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val lists = result.map { doc ->
                    CrunchylistModel(
                        id = doc.getString("crunchylistId") ?: doc.id,
                        name = doc.getString("listName") ?: "Crunchylist",
                        animeIds = (doc.get("animeIds") as? List<*>)?.mapNotNull { it as? String }?.toMutableList() ?: mutableListOf()
                    )
                }
                onSuccess(lists)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    fun addAnimeToCrunchylist(crunchylistId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        db.collection("crunchylists").document(crunchylistId)
            .update("animeIds", FieldValue.arrayUnion(animeId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun removeAnimeFromCrunchylist(crunchylistId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        db.collection("crunchylists").document(crunchylistId)
            .update("animeIds", FieldValue.arrayRemove(animeId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun renameCrunchylist(crunchylistId: String, listName: String, onComplete: (Boolean) -> Unit) {
        db.collection("crunchylists").document(crunchylistId)
            .update("listName", listName)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteCrunchylist(crunchylistId: String, onComplete: (Boolean) -> Unit) {
        db.collection("crunchylists").document(crunchylistId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Memperbarui Membership/Subscription Type User
    fun updateSubscription(userId: String, planName: String, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .update("membershipType", planName)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Menambah Review Anime
    fun addReview(
        animeId: String,
        userId: String,
        username: String,
        ratingStars: Double,
        comment: String,
        onComplete: (Boolean) -> Unit
    ) {
        val reviewId = db.collection("reviews").document().id
        val data = mapOf(
            "reviewId" to reviewId,
            "animeId" to animeId,
            "userId" to userId,
            "username" to username,
            "ratingStars" to ratingStars,
            "comment" to comment,
            "timestamp" to com.google.firebase.Timestamp.now()
        )
        db.collection("reviews").document(reviewId)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Mengambil Ulasan Anime
    fun getReviews(animeId: String, onSuccess: (List<Map<String, Any>>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("animeId", animeId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { it.data }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }
}


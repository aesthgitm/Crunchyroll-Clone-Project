package com.example.crunchyroll_pemvis_5

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Hilangkan active indicator container (pill oranye di belakang ikon)
        bottomNavigation.itemActiveIndicatorColor = ColorStateList.valueOf(Color.TRANSPARENT)

        // AMAN & TERKONTROL: Jalankan otomatis sinkronisasi seeding data sub-koleksi dari cloud
        Log.d("FIRESTORE_SEED", "Memulai pengecekan sinkronisasi pangkalan data Firestore...")
        FirestoreHelper().seedAnimeDatabase { success ->
            if (success) {
                Log.d("FIRESTORE_SEED", "Sinkronisasi database berhasil diselesaikan.")
            } else {
                Log.e("FIRESTORE_SEED", "Sinkronisasi database gagal. Harap periksa aturan Rules pangkalan data Anda.")
            }
        }
        
        // Ambil riwayat status tontonan pengguna dari cloud firebase
        syncUserStateFromCloud()

        // Load Default Fragment (Beranda) saat aplikasi dibuka pertama kali
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            bottomNavigation.selectedItemId = R.id.navigation_beranda
        }

        // Setup bottom navigation listener utama
        setupBottomNavigationListener()

        // Sync bottom navigation highlights secara dinamis tanpa memicu crash/infinite loop
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            
            // Nonaktifkan listener sementara waktu saat merombak UI highlight state
            bottomNavigation.setOnItemSelectedListener(null)
            
            when (currentFragment) {
                is HomeFragment -> bottomNavigation.selectedItemId = R.id.navigation_beranda
                is TelusuriFragment -> bottomNavigation.selectedItemId = R.id.navigation_telusuri
                is SimulcastFragment -> bottomNavigation.selectedItemId = R.id.navigation_simulcast
                is DaftarSayaFragment -> bottomNavigation.selectedItemId = R.id.navigation_daftar_saya
                is ProfileFragment -> bottomNavigation.selectedItemId = R.id.navigation_akun
                // JIKA HALAMAN BUKAN TAB UTAMA: (Seperti DetailFragment/PlayerFragment)
                // Biarkan highlight menu bawah menetap di tab sebelumnya tanpa melakukan perubahan posisi item aktif
                else -> { /* No-Op */ }
            }
            
            // Aktifkan kembali listener utama navigasi setelah selesai melakukan sinkronisasi
            setupBottomNavigationListener()
        }
    }

    /**
     * Membangun fungsi listener navigasi bawah secara terpusat demi efisiensi kode
     */
    private fun setupBottomNavigationListener() {
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_beranda -> HomeFragment()
                R.id.navigation_telusuri -> TelusuriFragment()
                R.id.navigation_simulcast -> SimulcastFragment()
                R.id.navigation_daftar_saya -> DaftarSayaFragment()
                R.id.navigation_akun -> ProfileFragment()
                else -> HomeFragment()
            }
            
            // Swap fragment utama secara bersih tanpa menumpuk di backstack tab utama
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }

    /**
     * Berpindah Halaman ke Halaman Detail Informasi Anime
     */
    fun openDetailFragment(anime: AnimeModel) {
        val fragment = DetailFragment.newInstance(anime)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Masuk antrean backstack untuk mendukung fungsionalitas tombol kembali bawaan HP
            .commit()
    }

    /**
     * Berpindah Halaman ke Halaman Pemutar Video Player (YouTube Player Dinamis)
     */
    fun openPlayerFragment(anime: AnimeModel, episodeNumber: Int) {
        val fragment = PlayerFragment.newInstance(anime.id, episodeNumber)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Berpindah Halaman ke Halaman Pencarian Anime
     */
    fun openSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SearchFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Membantu memindahkan tab navigasi bawah secara terprogram dari fragment internal
     */
    fun selectBottomTab(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

    /**
     * Sinkronisasi data lokal aplikasi (MockData) dengan server cloud Firestore Firebase
     */
    private fun syncUserStateFromCloud() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val helper = FirestoreHelper()

        helper.getWatchlistAnimeIds(userId, onSuccess = { ids ->
            val synced = ids.mapNotNull { id -> MockData.allAnime.find { it.id == id } }
            runOnUiThread {
                MockData.watchlist.clear()
                MockData.watchlist.addAll(synced)
            }
        }, onFailure = { })

        helper.getWatchHistoryAnimeIds(userId, onSuccess = { ids ->
            val synced = ids.mapNotNull { id -> MockData.allAnime.find { it.id == id } }
            runOnUiThread {
                MockData.watchHistory.clear()
                MockData.watchHistory.addAll(synced)
            }
        }, onFailure = { })

        helper.getDownloadAnimeIds(userId, onSuccess = { ids ->
            val synced = ids.mapNotNull { id -> MockData.allAnime.find { it.id == id } }
            runOnUiThread {
                MockData.downloads.clear()
                MockData.downloads.addAll(synced)
            }
        }, onFailure = { })

        helper.getCrunchylistsDetailed(userId, onSuccess = { lists ->
            runOnUiThread {
                MockData.createdCrunchylists.clear()
                MockData.createdCrunchylists.addAll(lists)
            }
        }, onFailure = { })
    }
}
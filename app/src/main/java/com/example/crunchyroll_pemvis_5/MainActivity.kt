package com.example.crunchyroll_pemvis_5

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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

        // Auto-seed database Firestore jika masih kosong
        FirestoreHelper().seedAnimeDatabase { success ->
            // Proses seeding otomatis selesai
        }
        syncUserStateFromCloud()

        // Load Default Fragment (Beranda)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            bottomNavigation.selectedItemId = R.id.navigation_beranda
        }

        // Setup bottom navigation listener
        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_beranda -> HomeFragment()
                R.id.navigation_telusuri -> TelusuriFragment()
                R.id.navigation_simulcast -> SimulcastFragment()
                R.id.navigation_daftar_saya -> DaftarSayaFragment()
                R.id.navigation_akun -> ProfileFragment()
                else -> HomeFragment()
            }
            
            // Swap fragment clean
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }

        // Sync bottom navigation highlights when backstack changes
        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            // Temporarily disable listener to avoid infinite loop
            bottomNavigation.setOnItemSelectedListener(null)
            when (currentFragment) {
                is HomeFragment -> bottomNavigation.selectedItemId = R.id.navigation_beranda
                is TelusuriFragment -> bottomNavigation.selectedItemId = R.id.navigation_telusuri
                is SimulcastFragment -> bottomNavigation.selectedItemId = R.id.navigation_simulcast
                is DaftarSayaFragment -> bottomNavigation.selectedItemId = R.id.navigation_daftar_saya
                is ProfileFragment -> bottomNavigation.selectedItemId = R.id.navigation_akun
            }
            // Restore listener
            bottomNavigation.setOnItemSelectedListener { item ->
                val f: Fragment = when (item.itemId) {
                    R.id.navigation_beranda -> HomeFragment()
                    R.id.navigation_telusuri -> TelusuriFragment()
                    R.id.navigation_simulcast -> SimulcastFragment()
                    R.id.navigation_daftar_saya -> DaftarSayaFragment()
                    R.id.navigation_akun -> ProfileFragment()
                    else -> HomeFragment()
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit()
                true
            }
        }
    }

    /**
     * Swaps Fragment to show Detailed Anime Page
     */
    fun openDetailFragment(anime: AnimeModel) {
        val fragment = DetailFragment.newInstance(anime)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Push to backstack for native back action
            .commit()
    }

    /**
     * Swaps Fragment to show Video Player Page
     */
    fun openPlayerFragment(anime: AnimeModel, episodeIndex: Int) {
        val fragment = PlayerFragment.newInstance(anime, episodeIndex)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Swaps Fragment to show Search Page
     */
    fun openSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SearchFragment())
            .addToBackStack(null)
            .commit()
    }

    /**
     * Switches the active bottom navigation tab
     */
    fun selectBottomTab(itemId: Int) {
        bottomNavigation.selectedItemId = itemId
    }

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
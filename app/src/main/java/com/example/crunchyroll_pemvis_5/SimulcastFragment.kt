package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class SimulcastFragment : Fragment() {

    private lateinit var txtSelectedSeason: TextView
    private lateinit var rvSimulcastAnime: RecyclerView
    private lateinit var txtSimulcastEmpty: TextView

    // FIX KATEGORI: Mengganti daftar menjadi Simulcast 2026 Season 1 - 4
    private val seasonsList = listOf(
        "Simulcast 2026 Season 1",
        "Simulcast 2026 Season 2",
        "Simulcast 2026 Season 3",
        "Simulcast 2026 Season 4"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simulcast_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtSelectedSeason = view.findViewById(R.id.txt_selected_season)
        rvSimulcastAnime = view.findViewById(R.id.rv_simulcast_anime)
        txtSimulcastEmpty = view.findViewById(R.id.txt_simulcast_empty)

        val btnCast = view.findViewById<ImageView>(R.id.btn_cast)
        val btnSearch = view.findViewById<ImageView>(R.id.btn_search)
        val btnSeasonDropdown = view.findViewById<LinearLayout>(R.id.btn_season_dropdown)

        btnCast.setOnClickListener {
            Toast.makeText(context, "Menghubungkan ke perangkat Cast...", Toast.LENGTH_SHORT).show()
        }

        btnSearch.setOnClickListener {
            (activity as? MainActivity)?.openSearchFragment()
        }

        // Ambil data season aktif dari MockData
        txtSelectedSeason.text = MockData.selectedSimulcastSeason
        filterAnimeBySeason(MockData.selectedSimulcastSeason)

        btnSeasonDropdown?.setOnClickListener { anchorView ->
            showSeasonPopupMenu(anchorView)
        }
    }

    private fun showSeasonPopupMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)

        seasonsList.forEachIndexed { index, seasonName ->
            popup.menu.add(Menu.NONE, index, Menu.NONE, seasonName)
        }

        popup.setOnMenuItemClickListener { menuItem ->
            val chosenSeason = seasonsList[menuItem.itemId]

            txtSelectedSeason.text = chosenSeason
            MockData.selectedSimulcastSeason = chosenSeason

            filterAnimeBySeason(chosenSeason)
            true
        }
        popup.show()
    }

    private fun filterAnimeBySeason(season: String) {
        // FIX LOGIKA FILTER: Disesuaikan dengan nama kategori season yang baru
        val filteredList = when (season) {
            "Simulcast 2026 Season 1" -> {
                MockData.allAnime.filter { it.id in listOf("jujutsu_kaisen", "my_hero_academia", "solo-leveling", "tokyo_revengers") }
            }
            "Simulcast 2026 Season 2" -> {
                MockData.allAnime.filter { it.id in listOf("attack_on_titan", "classroom_elite", "horimiya") }
            }
            "Simulcast 2026 Season 3" -> {
                MockData.allAnime.filter { it.id in listOf("your_name", "dr_stone", "fruits_basket") }
            }
            "Simulcast 2026 Season 4" -> {
                MockData.allAnime.filter { it.id in listOf("my_dress_up_darling", "rent_girlfriend", "your_lie_in_april") }
            }
            else -> emptyList()
        }

        if (filteredList.isEmpty()) {
            txtSimulcastEmpty.visibility = View.VISIBLE
            rvSimulcastAnime.visibility = View.GONE
        } else {
            txtSimulcastEmpty.visibility = View.GONE
            rvSimulcastAnime.visibility = View.VISIBLE

            rvSimulcastAnime.adapter = AnimeListAdapter(filteredList) { selectedAnime ->
                (activity as? MainActivity)?.openDetailFragment(selectedAnime)
            }
        }
    }
}
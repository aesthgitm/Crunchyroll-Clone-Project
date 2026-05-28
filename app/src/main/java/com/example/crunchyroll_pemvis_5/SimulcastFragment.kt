package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment

class SimulcastFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simulcast_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Header Cast and Search buttons
        val btnCast = view.findViewById<ImageView>(R.id.btn_cast)
        val btnSearch = view.findViewById<ImageView>(R.id.btn_search)

        btnCast.setOnClickListener {
            Toast.makeText(context, "Menghubungkan ke perangkat Cast...", Toast.LENGTH_SHORT).show()
        }

        btnSearch.setOnClickListener {
            (activity as? MainActivity)?.openSearchFragment()
        }

        // Season Dropdown click
        view.findViewById<LinearLayout>(R.id.btn_season_dropdown)?.setOnClickListener {
            Toast.makeText(context, "Membuka daftar pilihan musim Simulcast...", Toast.LENGTH_SHORT).show()
        }

        // Anime cards click configurations
        val itemsMap = mapOf(
            R.id.item_gachiakuta to "Gachiakuta",
            R.id.item_shield_hero to "The Rising of the Shield Hero",
            R.id.item_dress_up to "My Dress-Up Darling",
            R.id.item_dan_da_dan to "DAN DA DAN",
            R.id.item_solo_leveling to "Solo Leveling",
            R.id.item_demon_slayer to "Demon Slayer"
        )

        for ((id, title) in itemsMap) {
            view.findViewById<LinearLayout>(id)?.setOnClickListener {
                Toast.makeText(context, "Membuka halaman detail anime: $title", Toast.LENGTH_SHORT).show()
            }
        }

        // Options three dots
        val dotsMap = mapOf(
            R.id.more_dots_s1 to "Gachiakuta",
            R.id.more_dots_s2 to "The Rising of the Shield Hero",
            R.id.more_dots_s3 to "My Dress-Up Darling",
            R.id.more_dots_s4 to "DAN DA DAN",
            R.id.more_dots_s5 to "Solo Leveling",
            R.id.more_dots_s6 to "Demon Slayer"
        )

        for ((id, title) in dotsMap) {
            view.findViewById<ImageView>(id)?.setOnClickListener {
                Toast.makeText(context, "Pilihan lainnya untuk: $title", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

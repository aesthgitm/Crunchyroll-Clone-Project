package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SortFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btn_close_sort)
        val btnUpdate = view.findViewById<TextView>(R.id.btn_update_sort)

        val radioPopularity = view.findViewById<android.widget.RadioButton>(R.id.radio_popularity)
        val radioNewest = view.findViewById<android.widget.RadioButton>(R.id.radio_newest)
        val radioAlphabetical = view.findViewById<android.widget.RadioButton>(R.id.radio_alphabetical)

        // Load current selection
        when (MockData.selectedSortOption) {
            "Populer" -> radioPopularity?.isChecked = true
            "Terbaru" -> radioNewest?.isChecked = true
            "A-Z" -> radioAlphabetical?.isChecked = true
        }

        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdate.setOnClickListener {
            val selected = when {
                radioPopularity?.isChecked == true -> "Populer"
                radioNewest?.isChecked == true -> "Terbaru"
                radioAlphabetical?.isChecked == true -> "A-Z"
                else -> "Terbaru"
            }
            MockData.selectedSortOption = selected
            Toast.makeText(context, "Urutan diperbarui menjadi: $selected", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }
}

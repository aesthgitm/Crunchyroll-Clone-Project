package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class FilterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose = view.findViewById<ImageView>(R.id.btn_close_filter)
        val btnUpdate = view.findViewById<TextView>(R.id.btn_update_filter)

        val radioShowAll = view.findViewById<android.widget.RadioButton>(R.id.radio_show_all)
        val radioShowSeries = view.findViewById<android.widget.RadioButton>(R.id.radio_show_series)
        val radioShowMovies = view.findViewById<android.widget.RadioButton>(R.id.radio_show_movies)

        val radioLangAll = view.findViewById<android.widget.RadioButton>(R.id.radio_lang_all)
        val radioLangSub = view.findViewById<android.widget.RadioButton>(R.id.radio_lang_sub)
        val radioLangDub = view.findViewById<android.widget.RadioButton>(R.id.radio_lang_dub)

        // Load selected options on start
        when (MockData.selectedGenreFilter) {
            "Seri" -> radioShowSeries?.isChecked = true
            "Movie" -> radioShowMovies?.isChecked = true
            "Takarir" -> radioLangSub?.isChecked = true
            "Sulih Suara" -> radioLangDub?.isChecked = true
            else -> {
                radioShowAll?.isChecked = true
                radioLangAll?.isChecked = true
            }
        }

        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdate.setOnClickListener {
            val filter = when {
                radioShowSeries?.isChecked == true -> "Seri"
                radioShowMovies?.isChecked == true -> "Movie"
                radioLangSub?.isChecked == true -> "Takarir"
                radioLangDub?.isChecked == true -> "Sulih Suara"
                else -> "Semua"
            }
            MockData.selectedGenreFilter = filter
            Toast.makeText(context, "Filter diperbarui menjadi: $filter", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }
}

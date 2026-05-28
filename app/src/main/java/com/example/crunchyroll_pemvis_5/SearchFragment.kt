package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {

    private lateinit var txtSearchQuery: EditText
    private lateinit var btnClear: ImageView
    private lateinit var layoutEmpty: View
    private lateinit var rvResults: RecyclerView
    private lateinit var searchAdapter: AnimePosterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        txtSearchQuery = view.findViewById(R.id.txt_search_query)
        btnClear = view.findViewById(R.id.btn_search_clear)
        layoutEmpty = view.findViewById(R.id.layout_search_empty)
        rvResults = view.findViewById(R.id.rv_search_results)

        // Floating back navigation
        view.findViewById<ImageView>(R.id.btn_search_back).setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        // Clear query text input button action
        btnClear.setOnClickListener {
            txtSearchQuery.text.clear()
        }

        // Hook search key action on keyboard
        txtSearchQuery.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = txtSearchQuery.text.toString().trim()
                if (query.isNotEmpty()) {
                    MockData.searchHistory.remove(query)
                    MockData.searchHistory.add(0, query)
                }
                performSearch(query)
                true
            } else {
                false
            }
        }

        // Hook query text change listener
        txtSearchQuery.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                performSearch(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Setup results Grid layout manager with 3 columns for poster cells
        rvResults.layoutManager = GridLayoutManager(context, 3)
        searchAdapter = AnimePosterAdapter(emptyList()) { anime ->
            // Save search query before navigating
            val query = txtSearchQuery.text.toString().trim()
            if (query.isNotEmpty()) {
                MockData.searchHistory.remove(query)
                MockData.searchHistory.add(0, query)
            }
            (activity as? MainActivity)?.openDetailFragment(anime)
        }
        rvResults.adapter = searchAdapter

        updateSearchHistoryUI()

        return view
    }

    private fun updateSearchHistoryUI() {
        val parentLayout = layoutEmpty as? ViewGroup ?: return
        val txtEmptyDesc = parentLayout.getChildAt(1) as? TextView
        if (MockData.searchHistory.isNotEmpty()) {
            val history = MockData.searchHistory.take(5).joinToString(", ")
            txtEmptyDesc?.text = "Pencarian Terbaru:\n$history"
        } else {
            txtEmptyDesc?.text = "Masukkan kata kunci untuk mencari anime favorit Anda"
        }
    }

    private fun performSearch(query: String) {
        if (query.trim().isEmpty()) {
            btnClear.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
            rvResults.visibility = View.GONE
            searchAdapter.updateList(emptyList())
            updateSearchHistoryUI()
        } else {
            btnClear.visibility = View.VISIBLE
            val results = MockData.searchAnime(query)
            
            layoutEmpty.visibility = View.GONE
            rvResults.visibility = View.VISIBLE
            searchAdapter.updateList(results)
        }
    }
}

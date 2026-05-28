package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class ContentRestrictionFragment : Fragment() {

    private lateinit var radioAll: RadioButton
    private lateinit var radioPg: RadioButton
    private lateinit var radio12: RadioButton
    private lateinit var radio14: RadioButton
    private lateinit var radio16: RadioButton
    private lateinit var radio18: RadioButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_content_restriction, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_restriction)
        val txtFaq = view.findViewById<View>(R.id.txt_faq_rating)

        // Bind radio buttons
        radioAll = view.findViewById(R.id.radio_restrict_all)
        radioPg = view.findViewById(R.id.radio_restrict_pg)
        radio12 = view.findViewById(R.id.radio_restrict_12)
        radio14 = view.findViewById(R.id.radio_restrict_14)
        radio16 = view.findViewById(R.id.radio_restrict_16)
        radio18 = view.findViewById(R.id.radio_restrict_18)

        // Bind clickable rows
        val rowAll = view.findViewById<View>(R.id.row_restrict_all)
        val rowPg = view.findViewById<View>(R.id.row_restrict_pg)
        val row12 = view.findViewById<View>(R.id.row_restrict_12)
        val row14 = view.findViewById<View>(R.id.row_restrict_14)
        val row16 = view.findViewById<View>(R.id.row_restrict_16)
        val row18 = view.findViewById<View>(R.id.row_restrict_18)

        // Sync initial selection
        setSelectedRating(MockData.activeContentRestriction)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        txtFaq.setOnClickListener {
            Toast.makeText(context, "Membuka FAQ Rating Pembatasan Konten...", Toast.LENGTH_SHORT).show()
        }

        // Set row click listeners
        rowAll.setOnClickListener { selectRating("Semua Konten") }
        rowPg.setOnClickListener { selectRating("PG (Bimbingan Orang Tua)") }
        row12.setOnClickListener { selectRating("Remaja (12+)") }
        row14.setOnClickListener { selectRating("Remaja Lebih Tua (14+)") }
        row16.setOnClickListener { selectRating("Dewasa Muda (16+)") }
        row18.setOnClickListener { selectRating("Dewasa (18+)") }

        return view
    }

    private fun setSelectedRating(rating: String) {
        // Clear all
        radioAll.isChecked = false
        radioPg.isChecked = false
        radio12.isChecked = false
        radio14.isChecked = false
        radio16.isChecked = false
        radio18.isChecked = false

        // Select active
        when (rating) {
            "Semua Konten" -> radioAll.isChecked = true
            "PG (Bimbingan Orang Tua)" -> radioPg.isChecked = true
            "Remaja (12+)" -> radio12.isChecked = true
            "Remaja Lebih Tua (14+)" -> radio14.isChecked = true
            "Dewasa Muda (16+)" -> radio16.isChecked = true
            "Dewasa (18+)" -> radio18.isChecked = true
            else -> radioAll.isChecked = true
        }
    }

    private fun selectRating(rating: String) {
        MockData.activeContentRestriction = rating
        setSelectedRating(rating)
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            FirestoreHelper().saveCurrentUserSnapshot(userId) { success ->
                activity?.runOnUiThread {
                    val msg = if (success) {
                        "Pembatasan konten diubah ke: $rating"
                    } else {
                        "Pembatasan konten diperbarui lokal, gagal sinkron cloud"
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        } else {
            Toast.makeText(context, "Pembatasan konten diubah ke: $rating", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}

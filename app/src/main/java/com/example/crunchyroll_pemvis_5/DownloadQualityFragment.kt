package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class DownloadQualityFragment : Fragment() {

    // 0 = Tinggi, 1 = Sedang, 2 = Rendah
    private var selectedQuality = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_download_quality, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_download_quality)
        val rowHigh = view.findViewById<View>(R.id.row_quality_high)
        val rowMedium = view.findViewById<View>(R.id.row_quality_medium)
        val rowLow = view.findViewById<View>(R.id.row_quality_low)
        val radioHigh = view.findViewById<ImageView>(R.id.radio_high)
        val radioMedium = view.findViewById<ImageView>(R.id.radio_medium)
        val radioLow = view.findViewById<ImageView>(R.id.radio_low)

        // Read saved quality from MockData
        selectedQuality = MockData.downloadQuality

        fun updateRadioUI() {
            // Reset all to unselected
            radioHigh.setImageResource(R.drawable.ic_radio_unselected)
            radioHigh.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            radioMedium.setImageResource(R.drawable.ic_radio_unselected)
            radioMedium.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary))
            radioLow.setImageResource(R.drawable.ic_radio_unselected)
            radioLow.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_secondary))

            // Set selected
            when (selectedQuality) {
                0 -> {
                    radioHigh.setImageResource(R.drawable.ic_radio_selected)
                    radioHigh.clearColorFilter()
                }
                1 -> {
                    radioMedium.setImageResource(R.drawable.ic_radio_selected)
                    radioMedium.clearColorFilter()
                }
                2 -> {
                    radioLow.setImageResource(R.drawable.ic_radio_selected)
                    radioLow.clearColorFilter()
                }
            }
        }

        updateRadioUI()

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        rowHigh.setOnClickListener {
            selectedQuality = 0
            MockData.downloadQuality = 0
            updateRadioUI()
            persistQuality("Tinggi")
        }

        rowMedium.setOnClickListener {
            selectedQuality = 1
            MockData.downloadQuality = 1
            updateRadioUI()
            persistQuality("Sedang")
        }

        rowLow.setOnClickListener {
            selectedQuality = 2
            MockData.downloadQuality = 2
            updateRadioUI()
            persistQuality("Rendah")
        }

        return view
    }

    private fun persistQuality(label: String) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isNotEmpty()) {
            FirestoreHelper().saveCurrentUserSnapshot(userId) { success ->
                activity?.runOnUiThread {
                    val msg = if (success) {
                        "Kualitas unduhan: $label"
                    } else {
                        "Kualitas unduhan diperbarui lokal, gagal sinkron cloud"
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Kualitas unduhan: $label", Toast.LENGTH_SHORT).show()
        }
    }
}

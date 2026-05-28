package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class AudioDescriptionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_audio_description, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_audio_desc)
        val switchDesc = view.findViewById<SwitchMaterial>(R.id.switch_audio_desc_page)
        val btnHelp = view.findViewById<View>(R.id.btn_audio_desc_help)

        switchDesc.isChecked = MockData.audioDescriptionEnabled

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        switchDesc.setOnCheckedChangeListener { _, isChecked ->
            MockData.audioDescriptionEnabled = isChecked
            val status = if (isChecked) "Aktif" else "Mati"
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (userId.isNotEmpty()) {
                FirestoreHelper().saveCurrentUserSnapshot(userId) { success ->
                    activity?.runOnUiThread {
                        val msg = if (success) {
                            "Deskripsi Audio: $status"
                        } else {
                            "Deskripsi Audio diperbarui lokal, gagal sinkron cloud"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Deskripsi Audio: $status", Toast.LENGTH_SHORT).show()
            }
        }

        btnHelp.setOnClickListener {
            Toast.makeText(context, "Membuka Pertanyaan Umum Deskripsi Audio...", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}

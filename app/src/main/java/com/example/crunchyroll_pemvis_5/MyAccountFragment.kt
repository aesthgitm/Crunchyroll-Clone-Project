package com.example.crunchyroll_pemvis_5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class MyAccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_my_account)
        val txtEmailVal = view.findViewById<TextView>(R.id.txt_acc_email_val)
        val switchStreamCellular = view.findViewById<SwitchMaterial>(R.id.switch_stream_cellular)
        val switchDownloadCellular = view.findViewById<SwitchMaterial>(R.id.switch_download_cellular)

        val rowEmail = view.findViewById<View>(R.id.row_acc_email)
        val rowPassword = view.findViewById<View>(R.id.row_acc_password)
        val rowDownloadQuality = view.findViewById<View>(R.id.row_acc_download_quality)
        val rowPrivacy = view.findViewById<View>(R.id.row_acc_privacy)
        val rowHelp = view.findViewById<View>(R.id.row_acc_help)
        val rowDelete = view.findViewById<View>(R.id.row_acc_delete)
        val btnLogout = view.findViewById<View>(R.id.btn_account_logout)

        // Bind data
        txtEmailVal.text = "${MockData.profileUsername}@gmail.com"
        switchStreamCellular.isChecked = MockData.streamCellularEnabled
        switchDownloadCellular.isChecked = MockData.downloadCellularEnabled

        // Listeners for switches
        switchStreamCellular.setOnCheckedChangeListener { _, isChecked ->
            MockData.streamCellularEnabled = isChecked
            syncProfileSnapshot()
            val status = if (isChecked) "Aktif" else "Nonaktif"
            Toast.makeText(context, "Streaming seluler: $status", Toast.LENGTH_SHORT).show()
        }

        switchDownloadCellular.setOnCheckedChangeListener { _, isChecked ->
            MockData.downloadCellularEnabled = isChecked
            syncProfileSnapshot()
            val status = if (isChecked) "Aktif" else "Nonaktif"
            Toast.makeText(context, "Unduh seluler: $status", Toast.LENGTH_SHORT).show()
        }

        // Row clicks
        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        rowEmail.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChangeEmailFragment())
                .addToBackStack(null)
                .commit()
        }

        rowPassword.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ChangePasswordFragment())
                .addToBackStack(null)
                .commit()
        }

        rowDownloadQuality.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DownloadQualityFragment())
                .addToBackStack(null)
                .commit()
        }

        rowPrivacy.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PrivacyDoNotSellFragment())
                .addToBackStack(null)
                .commit()
        }

        rowHelp.setOnClickListener {
            Toast.makeText(context, "Membuka Pusat Bantuan...", Toast.LENGTH_SHORT).show()
        }

        rowDelete.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DeleteAccountFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            // Reset state
            val intent = Intent(requireActivity(), LandingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        return view
    }

    private fun syncProfileSnapshot() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirestoreHelper().saveCurrentUserSnapshot(userId) { }
    }
}

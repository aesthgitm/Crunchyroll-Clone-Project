package com.example.crunchyroll_pemvis_5

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ChangeEmailFragment : Fragment() {

    private lateinit var txtEmailDisplay: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_change_email, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_change_email)
        txtEmailDisplay = view.findViewById(R.id.txt_current_email_display)
        val btnTriggerChange = view.findViewById<View>(R.id.btn_trigger_change_email)
        val btnSupport = view.findViewById<View>(R.id.btn_email_support)

        // Set email display
        txtEmailDisplay.text = MockData.loggedInEmail

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSupport.setOnClickListener {
            Toast.makeText(context, "Membuka Pusat Bantuan...", Toast.LENGTH_SHORT).show()
        }

        btnTriggerChange.setOnClickListener {
            showChangeEmailDialog()
        }

        return view
    }

    private fun showChangeEmailDialog() {
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Dialog_Alert)
        builder.setTitle("Ubah Alamat Email")

        val input = EditText(requireContext())
        input.hint = "Alamat Email Baru"
        input.setTextColor(resources.getColor(R.color.white, null))
        input.setText(MockData.loggedInEmail)
        builder.setView(input)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newEmail = input.text.toString().trim()
            if (newEmail.isNotEmpty() && newEmail.contains("@")) {
                val usernamePart = newEmail.substringBefore("@")
                MockData.profileUsername = usernamePart
                MockData.loggedInEmail = newEmail
                txtEmailDisplay.text = newEmail
                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    FirestoreHelper().saveUser(
                        User(
                            uid = currentUser.uid,
                            username = MockData.profileUsername,
                            email = MockData.loggedInEmail,
                            profileName = MockData.profileName,
                            membershipType = if (MockData.activeSubscriptionPlan.equals("Gratis", ignoreCase = true)) "Free" else MockData.activeSubscriptionPlan,
                            contentRatingRestriction = MockData.activeContentRestriction,
                            audioLanguage = MockData.audioLanguage,
                            subtitleLanguage = MockData.subtitleLanguage
                        )
                    ) { }
                }
                Toast.makeText(context, "Email berhasil diubah", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Email tidak valid!", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}

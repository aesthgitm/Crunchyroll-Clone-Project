package com.example.crunchyroll_pemvis_5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class SwitchProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_switch_profile, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_switch_profile)
        val btnSelectActiveProfile = view.findViewById<View>(R.id.btn_select_active_profile)
        val btnAddProfile = view.findViewById<View>(R.id.btn_add_profile)
        val btnManageProfile = view.findViewById<View>(R.id.btn_manage_profile)
        val btnLogout = view.findViewById<View>(R.id.btn_logout)
        val txtActiveProfileName = view.findViewById<TextView>(R.id.txt_active_profile_name)

        // Bind active profile name
        txtActiveProfileName.text = MockData.profileName

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnSelectActiveProfile.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnAddProfile.setOnClickListener {
            Toast.makeText(context, "Mock: Limit profil tercapai untuk akun Anda.", Toast.LENGTH_SHORT).show()
        }

        btnManageProfile.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogout.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            // Redirect to LandingActivity and clear backstack
            val intent = Intent(requireActivity(), LandingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        return view
    }
}

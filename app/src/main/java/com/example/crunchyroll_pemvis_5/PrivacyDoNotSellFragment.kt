package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

class PrivacyDoNotSellFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_privacy_do_not_sell, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_privacy_dsns)
        val linkPrivacyCenter = view.findViewById<View>(R.id.link_privacy_center)
        val linkPrivacyPolicy = view.findViewById<View>(R.id.link_privacy_policy)
        val btnOptOut = view.findViewById<View>(R.id.btn_opt_out_sale)

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        linkPrivacyCenter.setOnClickListener {
            Toast.makeText(context, "Membuka Privacy Center...", Toast.LENGTH_SHORT).show()
        }

        linkPrivacyPolicy.setOnClickListener {
            Toast.makeText(context, "Membuka Kebijakan Privasi...", Toast.LENGTH_SHORT).show()
        }

        btnOptOut.setOnClickListener {
            Toast.makeText(context, "Preferensi opt-out disimpan.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}

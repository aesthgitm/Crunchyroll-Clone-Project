package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MembershipPlanFragment : Fragment() {

    private lateinit var txtTitle: TextView
    private lateinit var txtLabel: TextView
    private lateinit var txtPeriod: TextView
    private lateinit var txtDate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_membership_plan, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_membership)
        txtTitle = view.findViewById(R.id.txt_membership_title)
        txtLabel = view.findViewById(R.id.txt_keanggotaan_label)
        txtPeriod = view.findViewById(R.id.txt_billing_period)
        txtDate = view.findViewById(R.id.txt_billing_date)

        val btnManage = view.findViewById<View>(R.id.btn_manage_subscription)
        val rowCard = view.findViewById<View>(R.id.row_digital_card)
        val rowRedeem = view.findViewById<View>(R.id.row_redeem_code)
        val btnUpgradeAnimay = view.findViewById<View>(R.id.btn_upgrade_animay)

        // Set data
        updatePlanUI()

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnManage.setOnClickListener {
            // Open SubscriptionFragment to change plan
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SubscriptionFragment())
                .addToBackStack(null)
                .commit()
        }

        rowCard.setOnClickListener {
            Toast.makeText(context, "Membuka Kartu Keanggotaan Digital...", Toast.LENGTH_SHORT).show()
        }

        rowRedeem.setOnClickListener {
            Toast.makeText(context, "Fitur penukaran kode promosi belum tersedia.", Toast.LENGTH_SHORT).show()
        }

        btnUpgradeAnimay.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SubscriptionFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updatePlanUI()
    }

    private fun updatePlanUI() {
        val activePlan = MockData.activeSubscriptionPlan
        txtTitle.text = "Anggota $activePlan"
        txtLabel.text = "Keanggotaan $activePlan"

        if (activePlan.equals("Gratis", ignoreCase = true)) {
            txtPeriod.text = "Tidak Ada Tagihan Aktif"
            txtDate.text = "-"
        } else {
            txtPeriod.text = "Ditagih Bulanan"
            txtDate.text = "02/06/26"
        }
    }
}

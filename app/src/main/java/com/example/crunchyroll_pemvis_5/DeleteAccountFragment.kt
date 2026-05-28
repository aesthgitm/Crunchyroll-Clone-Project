package com.example.crunchyroll_pemvis_5

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class DeleteAccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_delete_account, container, false)

        val btnBack = view.findViewById<View>(R.id.btn_back_delete_account)
        val txtWarningMain = view.findViewById<TextView>(R.id.txt_warning_main)
        val txtPrivacyInfo = view.findViewById<TextView>(R.id.txt_privacy_info)
        val txtSubscriptionHeader = view.findViewById<TextView>(R.id.txt_subscription_header)
        val cardSubscriptionInfo = view.findViewById<View>(R.id.card_subscription_info)
        val txtCardPlanName = view.findViewById<TextView>(R.id.txt_card_plan_name)
        val txtCardBillingDate = view.findViewById<TextView>(R.id.txt_card_billing_date)
        val btnCancelSubscription = view.findViewById<TextView>(R.id.btn_cancel_subscription)
        val btnProceedDelete = view.findViewById<TextView>(R.id.btn_proceed_delete)
        val txtMustCancelWarning = view.findViewById<TextView>(R.id.txt_must_cancel_warning)
        val btnDeleteSupport = view.findViewById<TextView>(R.id.btn_delete_support)

        // Build main warning with orange opening sentence
        val orangeSentence = "Penghapusan akun bersifat permanen dan tidak dapat dibatalkan."
        val restOfText = " Jika kamu menghapus akunmu, kamu akan kehilangan akses secara permanen ke akun, beserta semua informasi yang terkait – termasuk daftar tonton, riwayat tontonan, posisi pemutaran, dan konten lain yang spesifik untuk akunmu."
        val fullText = orangeSentence + restOfText
        val spannable = SpannableString(fullText)
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF6400")),
            0,
            orangeSentence.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#8E9297")),
            orangeSentence.length,
            fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtWarningMain.text = spannable

        // Build second paragraph with orange privacy link
        val privacyPrefix = "Pahami privasimu. Permintaan ini terpisah dari hak privasi data lainnya yang mungkin kamu miliki berdasarkan tempat tinggalmu. Untuk melihat profil lengkap tentang hak kontrol data dan tindakan tambahan yang dapat kamu ambil, silakan merujuk ke "
        val privacyLink = "Kebijakan Privasi."
        val privacyFull = privacyPrefix + privacyLink
        val privacySpannable = SpannableString(privacyFull)
        privacySpannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#8E9297")),
            0,
            privacyPrefix.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        privacySpannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#FF6400")),
            privacyPrefix.length,
            privacyFull.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtPrivacyInfo.text = privacySpannable

        fun updateSubscriptionUi() {
            val isSubscriptionActive = !MockData.activeSubscriptionPlan.equals("Gratis", ignoreCase = true)
            if (isSubscriptionActive) {
                txtSubscriptionHeader.visibility = View.VISIBLE
                cardSubscriptionInfo.visibility = View.VISIBLE
                txtMustCancelWarning.visibility = View.VISIBLE
                txtCardPlanName.text = "Anggota ${MockData.activeSubscriptionPlan}"
                txtCardBillingDate.text = "02/06/26"
                btnProceedDelete.setTextColor(Color.parseColor("#5E6267"))
                btnProceedDelete.isClickable = false
                btnProceedDelete.isFocusable = false
            } else {
                txtSubscriptionHeader.visibility = View.GONE
                cardSubscriptionInfo.visibility = View.GONE
                txtMustCancelWarning.visibility = View.GONE
                btnProceedDelete.setTextColor(Color.WHITE)
                btnProceedDelete.isClickable = true
                btnProceedDelete.isFocusable = true
            }
        }
        updateSubscriptionUi()

        btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        btnCancelSubscription.setOnClickListener {
            MockData.activeSubscriptionPlan = "Gratis"
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            if (userId.isNotEmpty()) {
                FirestoreHelper().updateSubscription(userId, "Free") { success ->
                    activity?.runOnUiThread {
                        val msg = if (success) {
                            "Langganan dibatalkan"
                        } else {
                            "Langganan dibatalkan lokal, gagal sinkron cloud"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        updateSubscriptionUi()
                    }
                }
            } else {
                Toast.makeText(context, "Langganan dibatalkan", Toast.LENGTH_SHORT).show()
                updateSubscriptionUi()
            }
        }

        btnProceedDelete.setOnClickListener {
            val isSubscriptionActive = !MockData.activeSubscriptionPlan.equals("Gratis", ignoreCase = true)
            if (isSubscriptionActive) {
                Toast.makeText(context, "Batalkan langganan terlebih dahulu sebelum hapus akun", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(context, "Akun tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentUser.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                    MockData.watchlist.clear()
                    MockData.watchHistory.clear()
                    MockData.downloads.clear()
                    MockData.createdCrunchylists.clear()
                    MockData.activeSubscriptionPlan = "Gratis"
                    Toast.makeText(context, "Akun berhasil dihapus", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireActivity(), LandingActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Gagal hapus akun. Login ulang lalu coba lagi.", Toast.LENGTH_LONG).show()
                }
            }
        }

        btnDeleteSupport.setOnClickListener {
            Toast.makeText(context, "Membuka Dukungan Pelanggan...", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}

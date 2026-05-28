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

class ProfileFragment : Fragment() {

    private lateinit var txtProfileName: TextView
    private lateinit var txtProfileUsername: TextView
    private lateinit var txtActiveRestriction: TextView
    private lateinit var txtSubscriptionTier: TextView
    private lateinit var txtProfileEmail: TextView
    private lateinit var txtAudioLanguage: TextView
    private lateinit var txtAudioDescription: TextView
    private lateinit var txtSubtitleLanguage: TextView
    private lateinit var txtDownloadQuality: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Bind views
        txtProfileName = view.findViewById(R.id.txt_profile_name)
        txtProfileUsername = view.findViewById(R.id.txt_profile_username)
        txtActiveRestriction = view.findViewById(R.id.txt_active_restriction)
        txtSubscriptionTier = view.findViewById(R.id.txt_subscription_tier)
        txtProfileEmail = view.findViewById(R.id.txt_profile_email)
        txtAudioLanguage = view.findViewById(R.id.txt_audio_language)
        txtAudioDescription = view.findViewById(R.id.txt_audio_description)
        txtSubtitleLanguage = view.findViewById(R.id.txt_subtitle_language)
        txtDownloadQuality = view.findViewById(R.id.txt_download_quality)

        val layoutAvatarContainer = view.findViewById<View>(R.id.layout_avatar_container)
        val rowSwitchProfile = view.findViewById<View>(R.id.row_switch_profile)
        val rowContentRestriction = view.findViewById<View>(R.id.row_content_restriction)
        val rowSubscription = view.findViewById<View>(R.id.row_subscription)

        val rowAudioLanguage = view.findViewById<View>(R.id.row_audio_language)
        val rowAudioDescription = view.findViewById<View>(R.id.row_audio_description)
        val rowSubtitleLanguage = view.findViewById<View>(R.id.row_subtitle_language)

        val rowNotifications = view.findViewById<View>(R.id.row_notifications)
        val rowProfileEmail = view.findViewById<View>(R.id.row_profile_email)
        val rowChangePassword = view.findViewById<View>(R.id.row_change_password)
        val rowDownloadQuality = view.findViewById<View>(R.id.row_download_quality)
        val rowPrivacy = view.findViewById<View>(R.id.row_privacy)
        val rowHelp = view.findViewById<View>(R.id.row_help)
        val rowDeleteAccount = view.findViewById<View>(R.id.row_delete_account)
        val btnProfileLogout = view.findViewById<View>(R.id.btn_profile_logout)

        val switchPinProfile = view.findViewById<SwitchMaterial>(R.id.switch_pin_profile)
        val switchClosedCaptions = view.findViewById<SwitchMaterial>(R.id.switch_closed_captions)
        val switchStreamCellular = view.findViewById<SwitchMaterial>(R.id.switch_stream_cellular)
        val switchDownloadCellular = view.findViewById<SwitchMaterial>(R.id.switch_download_cellular)

        // Set values from MockData
        updateProfileUI()

        // Setup switch states
        switchPinProfile.isChecked = MockData.isPinProfileEnabled
        switchClosedCaptions.isChecked = MockData.isClosedCaptionsEnabled
        switchStreamCellular.isChecked = MockData.streamCellularEnabled
        switchDownloadCellular.isChecked = MockData.downloadCellularEnabled

        // Switch change listeners
        switchPinProfile.setOnCheckedChangeListener { _, isChecked ->
            MockData.isPinProfileEnabled = isChecked
        }
        switchClosedCaptions.setOnCheckedChangeListener { _, isChecked ->
            MockData.isClosedCaptionsEnabled = isChecked
        }
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

        // Navigation and Click listeners
        layoutAvatarContainer.setOnClickListener {
            navigateToFragment(EditProfileFragment())
        }

        rowSwitchProfile.setOnClickListener {
            navigateToFragment(SwitchProfileFragment())
        }

        rowContentRestriction.setOnClickListener {
            navigateToFragment(ContentRestrictionFragment())
        }

        rowSubscription.setOnClickListener {
            navigateToFragment(MembershipPlanFragment())
        }

        rowAudioLanguage.setOnClickListener {
            navigateToFragment(AudioLanguageFragment())
        }

        rowAudioDescription.setOnClickListener {
            navigateToFragment(AudioDescriptionFragment())
        }

        rowSubtitleLanguage.setOnClickListener {
            navigateToFragment(SubtitleLanguageFragment())
        }

        rowProfileEmail.setOnClickListener {
            navigateToFragment(ChangeEmailFragment())
        }

        // Action toasts
        rowNotifications.setOnClickListener {
            Toast.makeText(context, "Membuka pengaturan notifikasi...", Toast.LENGTH_SHORT).show()
        }
        rowChangePassword.setOnClickListener {
            navigateToFragment(ChangePasswordFragment())
        }
        rowDownloadQuality.setOnClickListener {
            navigateToFragment(DownloadQualityFragment())
        }
        rowPrivacy.setOnClickListener {
            navigateToFragment(PrivacyDoNotSellFragment())
        }
        rowHelp.setOnClickListener {
            Toast.makeText(context, "Membuka Pusat Bantuan...", Toast.LENGTH_SHORT).show()
        }
        rowDeleteAccount.setOnClickListener {
            navigateToFragment(DeleteAccountFragment())
        }

        btnProfileLogout.setOnClickListener {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
            Toast.makeText(context, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            // Redirect to LandingActivity and clear task stack
            val intent = Intent(requireActivity(), LandingActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        updateProfileUI()
    }

    private fun updateProfileUI() {
        txtProfileName.text = MockData.profileName
        txtProfileUsername.text = "@${MockData.profileUsername}"
        txtActiveRestriction.text = MockData.activeContentRestriction
        txtSubscriptionTier.text = MockData.activeSubscriptionPlan
        txtProfileEmail.text = MockData.loggedInEmail
        txtAudioLanguage.text = MockData.audioLanguage
        txtAudioDescription.text = if (MockData.audioDescriptionEnabled) "Aktif" else "Mati"
        txtSubtitleLanguage.text = MockData.subtitleLanguage
        
        val qualityLabels = listOf("Tinggi", "Sedang", "Rendah")
        if (MockData.downloadQuality in 0..2) {
            txtDownloadQuality.text = qualityLabels[MockData.downloadQuality]
        } else {
            txtDownloadQuality.text = "Tinggi"
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun syncProfileSnapshot() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirestoreHelper().saveCurrentUserSnapshot(userId) { }
    }
}

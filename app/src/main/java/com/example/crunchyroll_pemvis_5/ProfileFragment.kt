package com.example.crunchyroll_pemvis_5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import         android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
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
    private lateinit var imgMainProfileAvatar: ImageView
    private lateinit var rowAddProfile: View

    // FIX: Fitur ganti foto profil langsung dari halaman Akun Utama
    private val changeAvatarMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            // Update foto profile aktif di database MockData
            MockData.activeProfile?.avatarUri = uri.toString()
            updateProfileUI()
            Toast.makeText(context, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
        }
    }

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
        imgMainProfileAvatar = view.findViewById(R.id.img_main_profile_avatar)
        rowAddProfile = view.findViewById(R.id.row_add_profile)

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

        updateProfileUI()

        switchPinProfile.isChecked = MockData.isPinProfileEnabled
        switchClosedCaptions.isChecked = MockData.isClosedCaptionsEnabled
        switchStreamCellular.isChecked = MockData.streamCellularEnabled
        switchDownloadCellular.isChecked = MockData.downloadCellularEnabled

        switchPinProfile.setOnCheckedChangeListener { _, isChecked ->
            MockData.isPinProfileEnabled = isChecked
        }
        switchClosedCaptions.setOnCheckedChangeListener { _, isChecked ->
            MockData.isClosedCaptionsEnabled = isChecked
        }
        switchStreamCellular.setOnCheckedChangeListener { _, isChecked ->
            MockData.streamCellularEnabled = isChecked
            syncProfileSnapshot()
        }
        switchDownloadCellular.setOnCheckedChangeListener { _, isChecked ->
            MockData.downloadCellularEnabled = isChecked
            syncProfileSnapshot()
        }

        // FIX ACTION: Klik foto sekarang langsung membuka galeri untuk mengganti gambar profil aktif
        layoutAvatarContainer.setOnClickListener {
            changeAvatarMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        rowSwitchProfile.setOnClickListener {
            navigateToFragment(SwitchProfileFragment())
        }

        rowAddProfile.setOnClickListener {
            navigateToFragment(AddProfileFragment())
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
        val currentProfile = MockData.activeProfile
        if (currentProfile != null) {
            txtProfileName.text = currentProfile.name
            
            if (currentProfile.avatarUri.isNotEmpty()) {
                imgMainProfileAvatar.imageTintList = null
                imgMainProfileAvatar.setPadding(0, 0, 0, 0)
                imgMainProfileAvatar.load(currentProfile.avatarUri) {
                    crossfade(true)
                }
            } else {
                imgMainProfileAvatar.setImageResource(R.drawable.ic_account)
                imgMainProfileAvatar.setPadding(16, 16, 16, 16)
                imgMainProfileAvatar.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            }
        } else {
            txtProfileName.text = MockData.profileName
            imgMainProfileAvatar.setImageResource(R.drawable.ic_account)
        }

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
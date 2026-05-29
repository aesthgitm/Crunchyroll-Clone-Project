package com.example.crunchyroll_pemvis_5

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import java.util.UUID

class AddProfileFragment : Fragment() {

    private lateinit var imgAvatar: ImageView
    private lateinit var edtName: EditText
    private var selectedImageUri: String = ""

    // Pemilih Gambar Galeri dengan Proteksi Izin Permanen
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                // FIX: Kunci izin akses file secara permanen agar bisa dibaca di fragment lain / saat aplikasi dibuka ulang
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, takeFlags)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            selectedImageUri = uri.toString()
            imgAvatar.imageTintList = null
            imgMainProfileAvatarLogoFix()
            imgAvatar.load(uri) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgAvatar = view.findViewById(R.id.img_profile_avatar)
        edtName = view.findViewById(R.id.edt_profile_name)

        view.findViewById<View>(R.id.btn_choose_avatar).setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        view.findViewById<Button>(R.id.btn_save_profile).setOnClickListener {
            val name = edtName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Nama profil tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // FIX MOCK LIMIT: Cek jika jumlah profil sudah mencapai batas maksimal (Contoh: Maksimal 5 Profil)
            if (MockData.userProfiles.size >= 5) {
                Toast.makeText(context, "mock limit profil tercapai untuk akun anda", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Simpan profil baru
            val newProfile = ProfileModel(
                id = UUID.randomUUID().toString(),
                name = name,
                avatarUri = selectedImageUri
            )
            MockData.userProfiles.add(newProfile)
            
            // Otomatis jadikan profil baru ini sebagai profil yang sedang aktif
            MockData.activeProfile = newProfile

            Toast.makeText(context, "Profil '$name' berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        view.findViewById<View>(R.id.btn_profile_back).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun imgMainProfileAvatarLogoFix() {
        imgAvatar.setPadding(0, 0, 0, 0)
    }
}
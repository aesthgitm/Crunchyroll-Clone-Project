package com.example.crunchyroll_pemvis_5

import java.io.Serializable

data class ProfileModel(
    val id: String,
    var name: String,
    var avatarUri: String = "" // Menyimpan lokasi gambar (bisa URI galeri atau URL internet)
) : Serializable
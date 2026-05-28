# Rancangan Penggabungan (Merge) Proyek Crunchyroll

Dokumen ini memetakan rancangan teknis untuk menyatukan dua proyek independen:
1. **Proyek `Clone`** (dikerjakan Teman A: Beranda, Detail Seri, & Episode)
2. **Proyek `uas`** (dikerjakan Teman B: Telusuri, Simulcast, & Daftar Saya)

Kedua proyek ini akan digabungkan ke dalam proyek utama **`crunchyroll_pemvis_5`** yang dikelola oleh Anda, untuk kemudian diintegrasikan dengan sistem autentikasi dan profil pengguna berbasis Firebase.

---

## 1. Pemetaan Struktur Halaman & Navigasi Utama

Aplikasi utama `crunchyroll_pemvis_5` akan menggunakan satu host Activity tunggal (`MainActivity.kt`) dengan navigasi bawah (**Bottom Navigation Bar**) yang memuat 5 tab utama. 

Berikut adalah pemetaan fragment dari masing-masing proyek ke tab navigasi utama:

| No | Tab Navigasi | Ikon Menu | Sumber Fragment (Nama Asli Proyek Asal) | Fungsi Halaman |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **Beranda** | `ic_home` | `HomeFragment` (dari `Clone`) | Menampilkan banner promo dan baris horizontal daftar anime rekomendasi. |
| 2 | **Telusuri** | `ic_compass` / `ic_search` | `TelusuriFragment` (dari `uas`) | Menu pencarian/kategori berdasarkan tab: Semua, Simulcast, Genre, Musik. |
| 3 | **Simulcast** | `ic_sparkle` | `SimulcastFragment` (dari `uas`) | Menampilkan jadwal rilis anime musiman dalam layout grid 2 kolom. |
| 4 | **Daftar Saya** | `ic_bookmark` | `DaftarSayaFragment` (dari `uas`) | Menyimpan tab Daftar Tonton, Crunchylist, Riwayat, dan Unduhan. |
| 5 | **Akun** | `ic_account` | `ProfileFragment` (Baru / Bagian Anda) | Mengelola info profil pengguna ("Moon"), pengaturan bahasa, dan tombol Sign Out. |

---

## 2. Pemetaan & Konflik Berkas (File Mappings)

Saat menyalin file dari proyek asal, beberapa file memiliki nama yang sama (misal `MainActivity.kt` atau `activity_main.xml`). Ikuti tabel pemetaan berikut untuk menghindari penimpaan file yang tidak dilihat:

### A. Berkas Kotlin (`.kt`)
Semua berkas Kotlin dari proyek asal disalin ke dalam package utama proyek Anda: `app/src/main/java/com/example/crunchyroll_pemvis_5/`.

| File Asal (`Clone`) | File Asal (`uas`) | File Tujuan (`crunchyroll_pemvis_5`) | Catatan Tindakan |
| :--- | :--- | :--- | :--- |
| `MainActivity.kt` | - | *Abaikan* | Halaman navigasi utama akan diatur ulang di `MainActivity.kt` milik Anda. |
| - | `MainActivity.kt` | *Abaikan* | Diabaikan, digantikan kerangka navigasi utama baru. |
| `HomeFragment.kt` | - | `HomeFragment.kt` | Salin, ganti package ke `com.example.crunchyroll_pemvis_5`. |
| `DetailFragment.kt` | - | `DetailFragment.kt` | Salin, ganti package ke `com.example.crunchyroll_pemvis_5`. |
| `AnimeModel.kt` | - | *Abaikan* | Gunakan model data `Anime.kt` terintegrasi database dari BAB 3. |
| `AnimePosterAdapter.kt`| - | `HomeAnimeAdapter.kt` | Salin, ganti package. Ubah data input menggunakan `Anime.kt`. |
| `EpisodeAdapter.kt` | - | `EpisodeAdapter.kt` | Salin, ganti package. |
| `MockData.kt` | - | `MockData.kt` | Salin (digunakan untuk pengujian UI sebelum Firestore diaktifkan penuh). |
| - | `DaftarSayaFragment.kt` | `DaftarSayaFragment.kt` | Salin, ganti package. |
| - | `SimulcastFragment.kt` | `SimulcastFragment.kt` | Salin, ganti package. |
| - | `TelusuriFragment.kt` | `TelusuriFragment.kt` | Salin, ganti package. |
| - | `TabContentFragment.kt` | `TabContentFragment.kt` | Salin, ganti package. |
| - | `TabPagerAdapter.kt` | `TabPagerAdapter.kt` | Salin, ganti package. |
| - | `TelusuriTabContentFragment.kt`| `TelusuriTabContentFragment.kt`| Salin, ganti package. |
| - | `TelusuriTabPagerAdapter.kt` | `TelusuriTabPagerAdapter.kt` | Salin, ganti package. |

### B. Berkas Layout XML (`.xml`)
Semua berkas XML dari kedua proyek disalin langsung ke folder `app/src/main/res/layout/` milik proyek Anda.

| File XML Asal (`Clone`) | File XML Asal (`uas`) | Nama File Tujuan | Solusi Konflik / Tindakan |
| :--- | :--- | :--- | :--- |
| `activity_main.xml` | `activity_main.xml` | `activity_main.xml` | Buat layout baru menggunakan Bottom Navigation dengan 5 tab. |
| `fragment_home.xml` | - | `fragment_home.xml` | Salin langsung. |
| `fragment_detail.xml` | - | `fragment_detail.xml` | Salin langsung. |
| `item_anime_poster.xml`| - | `item_anime_poster.xml`| Salin langsung. |
| `item_episode.xml` | - | `item_episode.xml` | Salin langsung. |
| - | `fragment_daftar_saya.xml` | `fragment_daftar_saya.xml`| Salin langsung. |
| - | `fragment_simulcast_main.xml`| `fragment_simulcast_main.xml`| Salin langsung. |
| - | `fragment_tab_content.xml`| `fragment_tab_content.xml`| Salin langsung. |
| - | `fragment_telusuri.xml` | `fragment_telusuri.xml` | Salin langsung. |
| - | `fragment_telusuri_semua.xml`| `fragment_telusuri_semua.xml`| Salin langsung. |
| - | `fragment_telusuri_genre.xml`| `fragment_telusuri_genre.xml`| Salin langsung. |
| - | `fragment_telusuri_musik.xml`| `fragment_telusuri_musik.xml`| Salin langsung. |
| - | `fragment_telusuri_simulcast.xml`| `fragment_telusuri_simulcast.xml`| Salin langsung. |
| - | `item_tab_custom.xml` | `item_tab_custom.xml` | Salin langsung. |

### C. Aset Gambar & Ikon (`res/drawable`)
Kumpulkan seluruh ikon menu navigasi dan file ilustrasi gambar ke folder `app/src/main/res/drawable/` proyek Anda:
* **Ikon dari `Clone`:** `badge_background.xml`, `bg_circle_bookmark.xml`, `btn_primary.xml`, `ic_account.xml`, `ic_add.xml`, `ic_arrow_back.xml`, `ic_bookmark.xml`, `ic_cast.xml`, `ic_chevron_down.xml`, `ic_close.xml`, `ic_download.xml`, `ic_home.xml`, `ic_list.xml`, `ic_more.xml`, `ic_play.xml`, `ic_search.xml`, `ic_share.xml`, `ic_simulcast.xml`, `ic_sort.xml`, `ic_star.xml`.
* **Aset Illustrasi Chibi dari `uas`:**
  - `cat_tv.png` (Ilustrasi kucing tidur di atas TV untuk state Daftar Tonton kosong).
  - `cat_bookshelf.png` (Ilustrasi kucing di rak buku untuk state Crunchylist kosong).
  - `hime_laptop.png` (Chibi Hime mengetik laptop untuk state Riwayat kosong).
  - `hime_premium.png` (Chibi Hime memegang HP premium untuk tab Unduhan).
* **Gambar Cover Anime:** `naruto.jpg`, `dbz.jpg`, `kny.jpg`, `re0.jpg`, `sl.jpg`.

---

## 3. Langkah Integrasi Navigasi di `MainActivity` Utama

Untuk menyatukan seluruh fragment, lakukan pembaruan menu navigasi bawah di proyek utama:

### Langkah 1: Buat Berkas Menu Bottom Nav `bottom_nav_menu.xml`
Buat berkas baru di `app/src/main/res/menu/bottom_nav_menu.xml` untuk mendefinisikan kelima tab navigasi:
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/navigation_beranda"
        android:icon="@drawable/ic_home"
        android:title="Beranda" />
    <item
        android:id="@+id/navigation_telusuri"
        android:icon="@drawable/ic_search"
        android:title="Telusuri" />
    <item
        android:id="@+id/navigation_simulcast"
        android:icon="@drawable/ic_simulcast"
        android:title="Simulcast" />
    <item
        android:id="@+id/navigation_daftar_saya"
        android:icon="@drawable/ic_bookmark"
        android:title="Daftar Saya" />
    <item
        android:id="@+id/navigation_akun"
        android:icon="@drawable/ic_account"
        android:title="Akun" />
</menu>
```

### Langkah 2: Update Layout `activity_main.xml` Utama
Ubah layout utama di `app/src/main/res/layout/activity_main.xml` proyek Anda untuk menampung container Fragment dan Bottom Navigation:
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main"
    android:layout_width="match_width"
    android:layout_height="match_parent"
    android:background="#121212">

    <!-- Container Utama untuk Fragment -->
    <FrameLayout
        android:id="@+id/fragment_container"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@id/bottom_navigation"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- Bottom Navigation Bar -->
    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_navigation"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="#1A1A1A"
        app:itemIconTint="@color/white"
        app:itemTextColor="@color/white"
        app:menu="@menu/bottom_nav_menu"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Langkah 3: Update `MainActivity.kt` Utama
Tulis logika pengendali pergantian fragment berdasarkan menu navigasi bawah yang diklik di `MainActivity.kt`:
```kotlin
package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set default fragment ke Beranda saat pertama kali dibuka
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            bottomNavigation.selectedItemId = R.id.navigation_beranda
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_beranda -> HomeFragment()
                R.id.navigation_telusuri -> TelusuriFragment()
                R.id.navigation_simulcast -> SimulcastFragment()
                R.id.navigation_daftar_saya -> DaftarSayaFragment()
                R.id.navigation_akun -> ProfileFragment() // Halaman Akun buatan Anda
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
```

---

## 4. Penyelarasan Tema Global (Dark Mode Crunchyroll)

Agar visual aplikasi terasa menyatu dan seragam, kita harus menyelaraskan `themes.xml` di `app/src/main/res/values/themes.xml` agar menggunakan skema warna hitam pekat Crunchyroll.
Pastikan background windows bernilai hitam `#121212` dan status bar berwarna gelap.

---

## 5. Langkah Integrasi Fitur Autentikasi (Login & Register)

Untuk menggabungkan modul **`LoginRegisterRelative`** ke dalam proyek utama, kita akan mengatur alur awal aplikasi saat dijalankan:

### A. Alur Kerja Navigasi Autentikasi & Landing Page
1. **LandingActivity** (dari `MainActivity` milik `LoginRegisterRelative`) adalah gerbang utama aplikasi yang memiliki 3 pilihan aksi:
   - Tombol **Masuk (Login)** ➡️ membuka `LoginActivity`.
   - Teks **Buat Akun (Register)** ➡️ membuka `RegisterActivity`.
   - Tombol **Jelajahi Uji Coba** ➡️ langsung masuk ke `MainActivity` (Bottom Navigation Dashboard).
2. **LoginActivity & RegisterActivity** akan langsung mengarahkan pengguna ke `MainActivity` setelah login/register sukses dilakukan atau ketika pengguna menekan tombol tutup (X).

### B. Pemetaan Kelas & Berkas Tambahan

| Berkas Asal (`LoginRegisterRelative`) | Nama Berkas di `crunchyroll_pemvis_5` | Penyesuaian Tindakan |
| :--- | :--- | :--- |
| `MainActivity.kt` | `LandingActivity.kt` | Salin, ganti nama agar tidak bentrok dengan `MainActivity` Bottom Nav, ganti package. |
| `activity_main.xml` | `activity_landing.xml` | Salin, ganti nama, sesuaikan tools:context menjadi `.LandingActivity`. |
| `LoginActivity.kt` | `LoginActivity.kt` | Salin, ganti package ke `com.example.crunchyroll_pemvis_5`. |
| `activity_login.xml` | `activity_login.xml` | Salin, sesuaikan tools:context menjadi `.LoginActivity`. |
| `RegisterActivity.kt` | `RegisterActivity.kt` | Salin, ganti package ke `com.example.crunchyroll_pemvis_5`. |
| `activity_register.xml`| `activity_register.xml`| Salin, sesuaikan tools:context menjadi `.RegisterActivity`. |

### C. Pemindahan Aset Gambar & Font
* **Aset Font (`res/font`):** Salin seluruh berkas font (*Poppins*) dari `LoginRegisterRelative` ke `crunchyroll_pemvis_5/app/src/main/res/font/`.
* **Aset Ikon & Gambar:** Salin `gambar1.png` (Logo Crunchyroll), `gambar2.png` (Background anime), `button_shape.xml`, `button_stroke.xml`, dan `icon_close.xml` ke folder `res/drawable/` proyek utama.
* **String Resources (`strings.xml`):** Tambahkan string `syarat_ketentuan`, `semua_anime_favoritmu_semua_di_satu_tempat`, `jelajahi_uji_coba`, `masuk`, dan `atau_buat_akun` ke berkas `res/values/strings.xml`.

### D. Konfigurasi `AndroidManifest.xml`
Ubah activity launcher di `AndroidManifest.xml` agar mengarah ke `LandingActivity` terlebih dahulu:
```xml
        <activity
            android:name=".LandingActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity
            android:name=".LoginActivity"
            android:exported="false" />
        <activity
            android:name=".RegisterActivity"
            android:exported="false" />
        <activity
            android:name=".MainActivity"
            android:exported="false" />
```

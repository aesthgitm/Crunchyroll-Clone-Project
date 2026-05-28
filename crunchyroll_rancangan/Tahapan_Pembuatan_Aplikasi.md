# Tahapan Pembuatan Aplikasi Crunchyroll
*Panduan Langkah demi Langkah dari Inisialisasi Proyek hingga Integrasi Database Firebase*

Dokumen ini menyajikan panduan teknis yang sangat mendetail untuk memandu Anda membangun aplikasi **crunchyroll_pemvis_5** dari nol hingga aplikasi siap dijalankan. Ikuti setiap instruksi berikut dengan cermat.

---

## BAB 1: Pembuatan Proyek Baru di Android Studio

Langkah pertama adalah membuat proyek dasar baru yang akan menjadi wadah utama penggabungan seluruh kode tim:

1.  **Buka Android Studio** di laptop Anda.
2.  Pilih **New Project** pada jendela selamat datang (atau pilih **File > New > New Project** jika sedang membuka proyek lain).
3.  Pilih template **Empty Views Activity** (atau *Empty Activity* tergantung versi Android Studio Anda) agar UI dibuat menggunakan sistem XML layout konvensional, lalu klik **Next**.
4.  Konfigurasikan detail proyek baru Anda sebagai berikut:
    *   **Name:** `crunchyroll_pemvis_5`
    *   **Package Name:** `com.example.crunchyroll_pemvis_5` (Sangat penting untuk menulisnya persis seperti ini karena akan didaftarkan ke Firebase).
    *   **Save Location:** Pilih direktori penyimpanan di laptop Anda (misalnya di folder `AndroidStudioProjects`).
    *   **Language:** `Kotlin`
    *   **Minimum SDK:** `API 24: Android 7.0 (Nougat)` (Opsi standar yang aman agar aplikasi dapat dijalankan di 95%+ perangkat aktif).
    *   **Build Configuration Language:** `Kotlin DSL (build.gradle.kts)`
5.  Klik **Finish** dan tunggu beberapa menit hingga Android Studio selesai melakukan proses inisialisasi awal (*Gradle Syncing*).

---

## BAB 2: Registrasi dan Konfigurasi Firebase dari Nol

### Langkah 1: Registrasi Proyek di Firebase Console
1.  Buka browser dan masuk ke [Firebase Console](https://console.firebase.google.com/). Login menggunakan akun Google Anda.
2.  Klik tombol **Add Project** (atau *Create a Project*).
3.  Masukkan nama proyek Firebase Anda, misalnya `Crunchyroll Pemvis 5`. Klik **Continue**.
4.  Aktifkan atau nonaktifkan Google Analytics (bebas untuk proyek tugas kuliah, disarankan dinonaktifkan agar setup lebih cepat), lalu klik **Create Project**. Tunggu hingga selesai dan klik **Continue**.
5.  Di halaman utama dashboard proyek Firebase Anda, klik ikon **Android** untuk mulai mendaftarkan aplikasi Anda.
6.  Isi formulir pendaftaran aplikasi:
    *   **Android package name:** `com.example.crunchyroll_pemvis_5` (Pastikan sama persis dengan yang Anda buat di Android Studio).
    *   **App nickname (optional):** `Crunchyroll Replica`
    *   **Debug signing certificate SHA-1 (optional):** Meskipun tertulis opsional, Anda memerlukan kode ini untuk mengaktifkan otentikasi login. Cara mendapatkannya:
        *   Di Android Studio proyek Anda, buka tab **Gradle** di panel samping kanan.
        *   Navigasikan ke: `crunchyroll_pemvis_5 > app > Tasks > android > signingReport` (atau ketik `./gradlew signingReport` pada Terminal di bagian bawah Android Studio).
        *   Tekan Enter. Pada jendela output di bawah, cari bagian `Variant: debug` dan salin kode **SHA-1** yang tercantum.
        *   Tempel kode SHA-1 tersebut ke kolom pendaftaran di Firebase.
7.  Klik **Register app**.

### Langkah 2: Memasang Google Services Configuration
1.  Setelah registrasi, klik tombol **Download google-services.json**.
2.  Buka Android Studio Anda. Ubah mode tampilan panel kiri dari **Android** menjadi **Project** melalui dropdown di bagian atas panel.
3.  Salin file `google-services.json` yang baru diunduh, lalu tempel (paste) ke dalam folder `app` (sejajar dengan file `build.gradle.kts` milik modul app).
4.  Kembalikan tampilan panel kiri ke mode **Android**.

### Langkah 3: Mengonfigurasi Dependencies Firebase (Gradle Version Catalog)
Konfigurasikan Gradle menggunakan file-file berikut agar sistem mengunduh pustaka Firebase yang dibutuhkan:

1.  **Edit file `gradle/libs.versions.toml`**
    Buka file catalog ini dan tambahkan library Firebase di bawah kategori masing-masing:
    ```toml
    [versions]
    # ... versi bawaan ...
    googleServices = "4.4.1"
    firebaseBom = "33.1.0"

    [libraries]
    # ... library bawaan ...
    firebase-bom = { group = "com.google.firebase", name = "firebase-bom", version.ref = "firebaseBom" }
    firebase-analytics = { group = "com.google.firebase", name = "firebase-analytics" }
    firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }
    firebase-firestore = { group = "com.google.firebase", name = "firebase-firestore" }

    [plugins]
    # ... plugin bawaan ...
    google-services = { id = "com.google.gms.google-services", version.ref = "googleServices" }
    ```

2.  **Edit file Root `build.gradle.kts`**
    Buka berkas Gradle tingkat proyek dan tambahkan baris plugin:
    ```kotlin
    plugins {
        alias(libs.plugins.android.application) apply false
        alias(libs.plugins.kotlin.android) apply false
        alias(libs.plugins.google.services) apply false // Tambahkan ini
    }
    ```

3.  **Edit file Modul `app/build.gradle.kts`**
    Terapkan plugin Firebase dan deklarasikan dependensi pustakanya:
    ```kotlin
    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.google.services) // Tambahkan ini di bawah plugin kotlin
    }

    android {
        namespace = "com.example.crunchyroll_pemvis_5"
        // ... konfigurasi compileSdk, targetSdk bawaan ...

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    dependencies {
        // ... dependensi bawaan ...

        // Import the Firebase BoM
        implementation(platform(libs.firebase.bom))

        // Tambahkan library Firebase
        implementation(libs.firebase.analytics)
        implementation(libs.firebase.auth)
        implementation(libs.firebase.firestore)
    }
    ```
4.  **Penting (Khusus Versi AGP 9.0+):**
    Jika Anda membuat proyek baru menggunakan Android Studio versi terbaru, proyek Anda mungkin default menggunakan Android Gradle Plugin (AGP) 9.0+ yang mengaktifkan fitur *built-in Kotlin compiler* secara otomatis. Hal ini akan memicu error:
    `Cannot add extension with name 'kotlin', as there is an extension already registered with that name.`
    
    Untuk menyelesaikannya (agar versi Kotlin `1.9.24` yang dideklarasikan di Gradle Version Catalog Anda berjalan lancar), buka file `gradle.properties` di root proyek Anda dan tambahkan baris berikut di bagian paling bawah:
    ```properties
    # Disable AGP 9.0+ built-in Kotlin compilation and new DSL to avoid conflicts with explicit Kotlin plugin
    android.builtInKotlin=false
    android.newDsl=false
    ```

5.  Klik **Sync Now** di bar notifikasi atas Android Studio dan tunggu hingga selesai tanpa error.

### Langkah 4: Mengaktifkan Layanan di Firebase Console
1.  **Aktifkan Auth**: Di Firebase Console browser Anda, masuk ke menu **Build > Authentication > Get Started**. Pilih tab **Sign-in method**, klik **Email/Password**, aktifkan statusnya menjadi **Enabled**, lalu klik **Save**.
2.  **Aktifkan Firestore**: Masuk ke menu **Build > Firestore Database > Create Database**.
    *   Pilih lokasi database terdekat (misalnya `asia-southeast2` di Jakarta).
    *   Pilih opsi **Start in test mode** untuk mempermudah pengerjaan awal tim tanpa diblokir oleh aturan keamanan. Klik **Create**.

---

## BAB 3: Struktur Model Data dan Helper Firebase (Kotlin)

Untuk mempermudah integrasi Firestore, Anda harus membuat kelas representasi data (Model) dan kelas Helper untuk operasi CRUD di package `com.example.crunchyroll_pemvis_5`.

### 1. Membuat Model Data (Data Class)
Buat file-file Kotlin baru di bawah folder package utama Anda:

*   **`User.kt`**:
    ```kotlin
    package com.example.crunchyroll_pemvis_5

    data class User(
        val uid: String = "",
        val username: String = "",
        val email: String = "",
        val profileName: String = "Moon",
        val membershipType: String = "Free",
        val contentRatingRestriction: String = "16+",
        val audioLanguage: String = "Bahasa Indonesia",
        val subtitleLanguage: String = "English"
    )
    ```

*   **`Anime.kt`**:
    ```kotlin
    package com.example.crunchyroll_pemvis_5

    data class Anime(
        val animeId: String = "",
        val title: String = "",
        val description: String = "",
        val imageUrl: String = "",
        val bannerUrl: String = "",
        val genres: List<String> = emptyList(),
        val rating: Double = 0.0,
        val ratingCount: String = "0R",
        val status: String = "Ongoing",
        val releaseYear: Int = 2026,
        val simulcastSeason: String = "",
        val isSimulcast: Boolean = false
    )
    ```

*   **`Episode.kt`**:
    ```kotlin
    package com.example.crunchyroll_pemvis_5

    data class Episode(
        val episodeId: String = "",
        val episodeNumber: Int = 1,
        val title: String = "",
        val durationText: String = "23m",
        val synopsis: String = "",
        val thumbnailUrl: String = "",
        val downloadSizeText: String = "",
        val likesCount: Int = 0,
        val dislikesCount: Int = 0
    )
    ```

### 2. Membuat Kelas Firestore Helper (`FirestoreHelper.kt`)
Buat berkas Kotlin ini untuk memusatkan logika penyimpanan database Firestore:
```kotlin
package com.example.crunchyroll_pemvis_5

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    // Menyimpan/Memperbarui Data User Baru
    fun saveUser(user: User, onComplete: (Boolean) -> Unit) {
        db.collection("users").document(user.uid)
            .set(user)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Mengambil Data Profil User
    fun getUserProfile(userId: String, onSuccess: (User?) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document.toObject(User::class.java))
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Mengambil Katalog Anime
    fun getAnimeCatalog(onSuccess: (List<Anime>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("anime").get()
            .addOnSuccessListener { result ->
                val list = result.map { it.toObject(Anime::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Menambah Anime ke Watchlist (Daftar Tonton)
    fun addToWatchlist(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val watchlistId = "${userId}_${animeId}"
        val data = mapOf(
            "watchlistId" to watchlistId,
            "userId" to userId,
            "animeId" to animeId,
            "addedAt" to com.google.firebase.Timestamp.now()
        )
        db.collection("watchlists").document(watchlistId)
            .set(data)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Menghapus Anime dari Watchlist
    fun removeFromWatchlist(userId: String, animeId: String, onComplete: (Boolean) -> Unit) {
        val watchlistId = "${userId}_${animeId}"
        db.collection("watchlists").document(watchlistId).delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
```

---

## BAB 4: Penerimaan dan Penggabungan (Merge) File Teman A & B

Ketika Teman A (Home & Player UI) dan Teman B (Browse) menyerahkan file ZIP pekerjaan mereka kepada Anda, ikuti langkah integrasi manual ini secara teratur:

1.  **Ekstrak Berkas ZIP**: Ekstrak ZIP teman Anda ke direktori sementara di laptop Anda.
2.  **Impor Layout XML**:
    *   Buka folder `app/src/main/res/layout` hasil ekstrak milik teman Anda.
    *   Salin file (misal: `activity_home.xml`, `activity_browse.xml`, `item_anime_card.xml`).
    *   Tempel (paste) file-file tersebut ke folder `app/src/main/res/layout` di proyek `crunchyroll_pemvis_5` Anda.
3.  **Impor Gambar Aset**:
    *   Salin ikon dan banner buatan teman Anda dari folder `res/drawable` atau `res/mipmap` hasil ekstrak ke folder `res/drawable` Anda.
4.  **Impor Kelas Kotlin (`.kt`)**:
    *   Buka folder `java` hasil ekstrak teman Anda hingga menemukan file Kotlin (misalnya: `HomeActivity.kt`, `HomeAnimeAdapter.kt`, `BrowseActivity.kt`).
    *   Salin semua berkas Kotlin tersebut.
    *   Buka folder package Anda di: `app/src/main/java/com/example/crunchyroll_pemvis_5/`. Tempel semua file Kotlin tersebut di sana.
5.  **Perbaiki Package Header & Impor R (Wajib untuk Package Berbeda)**:
    *   Buka file Kotlin yang baru saja Anda salin di Android Studio. File tersebut pasti menunjukkan pesan error berwarna merah.
    *   **Perbaiki Package**: Ganti baris pertama (deklarasi package lama teman) menjadi:
        `package com.example.crunchyroll_pemvis_5`
    *   **Perbaiki Impor**: Cari baris impor R teman Anda yang lama (misalnya: `import com.example.projek_teman_a.R`). Hapus baris tersebut dan ketik impor proyek Anda:
        `import com.example.crunchyroll_pemvis_5.R`
6.  **Daftarkan Activity Baru di AndroidManifest**:
    Buka `app/src/main/AndroidManifest.xml` milik proyek `crunchyroll_pemvis_5` Anda. Tambahkan baris registrasi di dalam tag `<application>`:
    ```xml
    <activity
        android:name=".HomeActivity"
        android:exported="false" />
    <activity
        android:name=".BrowseActivity"
        android:exported="false" />
    ```
7.  **Clean & Rebuild**: Klik menu **Build > Clean Project** lalu **Build > Rebuild Project**.

---

## BAB 5: Alur Pembuatan UI Bagian I (Autentikasi, Akun, & Pengaturan)

Pada tahap ini, Anda akan bekerja bersama AI untuk merancang seluruh antarmuka otentikasi dan panel akun pengguna. Berikut adalah langkah-langkah pembuatan UI-nya:

### 1. Halaman Pendaftaran (Register UI)
*   **Tujuan:** Membuat form pendaftaran akun baru Crunchyroll.
*   **Langkah UI:**
    *   Buat file XML `activity_register.xml` dengan latar belakang gelap khas Crunchyroll.
    *   Tambahkan logo teks Crunchyroll di bagian atas.
    *   Buat kolom input (*EditText*) untuk: Nama Pengguna (Username), Alamat Email, dan Kata Sandi (Password).
    *   Buat tombol (Button) berwarna oranye dengan tulisan "Daftar".
    *   Buat link teks (TextView) di bawah tombol: "Sudah punya akun? Masuk di sini" untuk berpindah ke halaman Login.

### 2. Halaman Masuk Akun (Login UI)
*   **Tujuan:** Membuat form login akun Crunchyroll.
*   **Langkah UI:**
    *   Buat file XML `activity_login.xml` dengan layout yang konsisten dengan halaman register.
    *   Sediakan kolom input untuk Alamat Email dan Kata Sandi.
    *   Tambahkan tombol masuk berwarna oranye dengan tulisan "Masuk".
    *   Tambahkan link teks di bawahnya untuk "Lupa Kata Sandi?" yang mengarah ke alur reset password.
    *   Tambahkan tombol atau teks di bagian bawah: "Belum punya akun? Buat Akun Baru".

### 3. Halaman Akun Utama (Profile Screen UI)
*   **Tujuan:** Menampilkan detail profil dan daftar opsi akun/pengaturan preferensi.
*   **Langkah UI:**
    *   Buat layout dengan gambar avatar berbentuk lingkaran (gambar logo Crunchyroll bermata satu) dengan username "Moon" di bawahnya.
    *   Tambahkan tombol kecil berbentuk pensil di dekat avatar untuk mengedit profil.
    *   Buat daftar pilihan navigasi ke bawah dengan teks berwarna putih menggunakan list item / TextView:
        *   *Beralih Profil* (Membuka daftar profil kustom).
        *   *Preferensi Menonton Moon*: Pembatasan Konten (menampilkan teks "16+"), Bahasa Audio (menampilkan teks "Bahasa Indonesia"), Bahasa Takarir/CC (menampilkan "English").
        *   *Keanggotaan*: Langganan (menampilkan status "Fan" atau "Mega Fan"), Email, Kata Sandi.
        *   *Unduhan*: Pilihan "Unduh Menggunakan Wi-Fi saja" (menggunakan Toggle Switch) dan "Kualitas Unduhan".
        *   *Privasi*: Don't Sell My Personal Info, Hapus Akun.
        *   *Tombol Keluar (Sign Out)* di bagian paling bawah.

### 4. Halaman Beralih & Edit Profil (Switch & Edit Profile UI)
*   **Tujuan:** Mengelola profil kustom di dalam satu akun utama.
*   **Langkah UI:**
    *   **Beralih Profil**: Desain layar dengan daftar avatar pengguna berbentuk lingkaran yang tersusun secara horizontal/grid. Tampilkan avatar "Moon" dan tombol "+" bertuliskan "Tambah Profil". Tambahkan tombol "Kelola Profil" di bawahnya.
    *   **Edit Profil**: Buat input teks untuk mengubah nama profil, mengubah username (dengan opsi tombol "ACAK" untuk merandomize username), dan tombol "Simpan" di bagian bawah.

### 5. Alur Reset Password & Konfigurasi Pengaturan Bahasa
*   **Tujuan:** Membuat antarmuka reset password dan pemilihan opsi bahasa.
*   **Langkah UI:**
    *   **Reset Password UI**: Desain form bertuliskan "Reset Kata Sandi" yang berisi kolom input email dan tombol "KIRIM". Setelah ditekan, tampilkan dialog sukses dengan maskot chibi Crunchyroll bertuliskan "Terima Kasih! Tautan reset sudah dikirim".
    *   **Dialog Opsi Preferensi**: Buat halaman menu radio button vertikal untuk memilih *Pembatasan Konten* (ALL, PG, 12+, 14+, 16+, 18+), *Bahasa Audio*, dan *Bahasa Takarir/CC* dengan tanda centang oranye pada bahasa yang aktif.

---

## BAB 6: Alur Pembuatan UI Bagian II (Daftar Saya, Detail Anime, & Simulcast)

Pada tahap ini, Anda bersama AI akan menyusun halaman antarmuka koleksi pribadi pengguna, katalog musiman, serta halaman utama detail metadata anime.

### 1. Halaman Daftar Saya (My Lists UI)
*   **Tujuan:** Menyusun menu koleksi pribadi user yang terbagi atas 4 sub-tab.
*   **Langkah UI:**
    *   Buat file XML utama dengan layout tab di bagian atas menggunakan `TabLayout` atau barisan tombol horizontal: **Daftar Tonton**, **Crunchylist**, **Riwayat**, dan **Unduhan**.
    *   Tambahkan ikon Chromecast dan Pencarian (Search) di pojok kanan atas.
    *   **Sub-tab Daftar Tonton**:
        *   *State Kosong:* Desain gambar kucing tidur di atas televisi dengan teks "Daftar tontonmu butuh perhatian. Ayo isi dengan anime keren" beserta tombol oranye "Jelajahi Semua".
        *   *State Terisi:* Buat adapter list vertical yang memuat poster anime mini di kiri, judul, keterangan sub/dub, tombol suka (Heart), dan menu titik tiga (Hapus dari Daftar Tonton, Info Seri, Bagikan).
    *   **Sub-tab Crunchylist (Playlist Kustom)**:
        *   *State Kosong:* Gambar kucing di rak buku dengan teks "Kamu belum memiliki Crunchylist. Ayo buat!" beserta tombol "Buat Daftar Baru".
        *   *State Terisi:* Tampilkan nama playlist (misal "Daftar 1") lengkap dengan total item dan opsi menu edit/hapus playlist.
        *   *Dialog Buat/Edit Crunchylist:* Dialog popup berisi form input nama playlist dan tombol konfirmasi.
    *   **Sub-tab Riwayat (History)**:
        *   *State Kosong:* Gambar maskot Hime di laptop dengan teks "Buat sejarah... dengan riwayat".
        *   *State Terisi:* List vertikal video thumbnail episode anime yang pernah ditonton lengkap dengan garis progress bar merah (menandakan sisa menit tontonan).
    *   **Sub-tab Unduhan**:
        *   Tampilkan visual maskot memegang HP premium dengan teks "Belum ada unduhan... Tingkatkan ke Mega Fan" beserta tombol kuning bermahkota "Jadilah Premium".

### 2. Halaman Detail Anime (Anime Detail UI)
*   **Tujuan:** Menampilkan info lengkap anime yang diklik oleh pengguna.
*   **Langkah UI:**
    *   Bagian atas menampilkan gambar banner besar anime (sebagai latar belakang) dengan tombol silang (X) di kiri atas untuk menutup halaman.
    *   Tampilkan judul grafis anime (logo), info rating usia (misal "16+"), keterangan audio/takarir, rating bintang (rata-rata 4.9 beserta total penilai), tombol "+ Daftar Saya" (Watchlist), dan tombol "Bagikan".
    *   Tulis deskripsi sinopsis cerita yang panjang dengan link teks "Detail Lainnya".
    *   Buat pemisah tab bawah: **Episode** dan **Lainnya Seperti Ini**.
    *   Di bawah tab Episode, buat tombol dropdown (Spinner) untuk memilih Season (misal: "JUJUTSU KAISEN").
    *   Tampilkan daftar episode vertikal berisi: gambar thumbnail episode dengan tombol Play di tengahnya, judul episode (misal "1. The Sphere"), durasi menit, dan ikon unduh.
    *   Tampilkan tombol mengambang / sticky button di bagian paling bawah bertuliskan "Lanjutkan E1" berwarna oranye.

### 3. Halaman Season Simulcast (Simulcast UI)
*   **Tujuan:** Halaman khusus tab ke-4 bottom navigation (ikon sparkles) untuk menampilkan perilisan anime terkini.
*   **Langkah UI:**
    *   Di bagian atas layar, buat dropdown besar berisi pilihan musim (misalnya: "Musim Panas 2026").
    *   Buat list grid 2 kolom di bawahnya yang berisi poster-poster anime simulcast pada musim tersebut lengkap dengan teks judul dan penanda subtitle ("Dengan Takarir").

---

---

## BAB 7: Integrasi UI Hasil Merge dengan Database Firestore

Setelah UI tergabung secara utuh, saatnya mengubah data statis (hardcoded) menjadi dinamis menggunakan FirestoreHelper.

### 1. Integrasi Halaman Beranda (Home)
Ubah list RecyclerView di `HomeActivity.kt` agar menampilkan data dari database Firestore:
```kotlin
package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {
    private lateinit var rvTrending: RecyclerView
    private lateinit var adapter: HomeAnimeAdapter // Menggunakan adapter dari Teman A
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvTrending = findViewById(R.id.rvTrending)
        rvTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Mengambil data anime asli dari Firestore Database
        firestoreHelper.getAnimeCatalog(
            onSuccess = { animeList ->
                // Filter anime untuk kategori Trending
                val trendingAnime = animeList.filter { it.rating >= 4.5 }
                adapter = HomeAnimeAdapter(trendingAnime)
                rvTrending.adapter = adapter
            },
            onFailure = { e ->
                Toast.makeText(this, "Gagal mengambil data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
```

### 2. Integrasi Fitur Tambah Watchlist di Halaman Detail Anime
Hubungkan tombol "+ Daftar Saya" (Watchlist) pada halaman detail agar tersimpan di Firestore:
```kotlin
package com.example.crunchyroll_pemvis_5

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class DetailActivity : AppCompatActivity() {
    private val firestoreHelper = FirestoreHelper()
    private val auth = FirebaseAuth.getInstance()
    private var isWatchlisted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val btnAddToWatchlist = findViewById<TextView>(R.id.btnAddToWatchlist) // Tombol + Daftar Saya
        val animeId = intent.getStringExtra("ANIME_ID") ?: ""

        val userId = auth.currentUser?.uid ?: ""

        btnAddToWatchlist.setOnClickListener {
            if (userId.isEmpty()) {
                Toast.makeText(this, "Harap login terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isWatchlisted) {
                firestoreHelper.addToWatchlist(userId, animeId) { success ->
                    if (success) {
                        isWatchlisted = true
                        btnAddToWatchlist.text = "Tersimpan di Daftar Saya"
                        Toast.makeText(this, "Berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                firestoreHelper.removeFromWatchlist(userId, animeId) { success ->
                    if (success) {
                        isWatchlisted = false
                        btnAddToWatchlist.text = "+ Daftar Saya"
                        Toast.makeText(this, "Berhasil dihapus!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
```

---

## BAB 8: Pengujian Akhir dan Build Release

1.  **Pengujian Manual di Emulator/Perangkat Fisik**:
    *   Jalankan registrasi akun baru pada aplikasi.
    *   Periksa di tab **Firebase Authentication** browser untuk memastikan akun terdaftar.
    *   Periksa di tab **Firestore Database** browser untuk memastikan profil user dibuat secara otomatis.
    *   Cobalah klik salah satu anime, klik "+ Daftar Saya", lalu periksa koleksi `watchlists` di Firestore.
2.  **Clean Up & Rebuild**:
    *   Selalu gunakan menu **Build > Clean Project** sebelum merilis berkas APK.
3.  **Generate APK**:
    *   Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)** untuk membuat berkas aplikasi `.apk` mentah yang bisa Anda bagikan dan instal ke ponsel teman-teman kelompok atau dosen penguji Anda.

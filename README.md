# VocabKid 🌟

**VocabKid** adalah aplikasi pembelajaran kosakata Bahasa Inggris interaktif untuk siswa sekolah dasar berbasis Android. Aplikasi ini menggabungkan metode **Spaced Repetition (SM-2)** dengan elemen gamifikasi, cerita bergambar interaktif, latihan pengucapan (*speech recognition*), simulasi percakapan, *mini-game*, serta asisten AI ramah anak.

---

## ✨ Fitur Utama

### 👦 Mode Siswa (Murid)
- **📚 Study (Flashcard Spaced Repetition):** Belajar kosakata terjadwal dengan kartu bolak-balik (*flip card*), audio pelafalan (*Text-to-Speech*), dan tombol evaluasi mandiri (*Ulangi, Sulit, Cukup, Mudah*).
- **❓ Latihan Kuis Bertingkat:** Kuis pilihan ganda per kategori dengan sistem pembukaan **Level 1 & Level 2**, umpan balik animasi interaktif, dan efek suara.
- **📖 Adventure Stories (10 Bab / 50 Ilustrasi Scene):** Membaca cerita petualangan Bahasa Inggris bergambar (*The Lost Kitten, The River Crossing, The Secret Garden, Space Explorer, Ocean Depths, Dragon Mountain, The Time Travel Market, My Robot Friend, Jungle Expedition,* dan *The Magic Library*) lengkap dengan kuis pemahaman di sela cerita.
- **🗣️ Pronunciation Practice:** Latihan pengucapan kosakata menggunakan mikrofon perangkat (*Android SpeechRecognizer*) dan *Text-to-Speech* untuk melatih pelafalan yang tepat.
- **💬 Conversation Practice:** Simulasi dialog Bahasa Inggris sehari-hari dengan berbagai skenario interaktif dan pencatatan riwayat pilihan percakapan.
- **🕹️ Arcade (Taman Bermain Kata – Memory Flip):** Permainan mencocokkan kartu kosakata Bahasa Inggris dengan artinya dalam 3 tingkat kesulitan (*Mudah, Sedang, Seru!*).
- **🤖 VocabKid AI Assistant:** Asisten belajar pintar berbasis **Google Gemini AI** dengan filter keamanan konten anak (*Child-Safe Content Filtering*) dan *automatic model fallback*.
- **📊 Statistik & Progress Belajar:** Visualisasi jumlah kata yang dikuasai, kata jatuh tempo hari ini, akurasi jawaban, serta daftar kata tersulit (*Frequently Wrong*).
- **🔔 Pengingat Belajar Harian (WorkManager):** Notifikasi otomatis ramah anak setiap hari apabila terdapat kosakata yang jatuh tempo untuk diulang.

### 👩‍🏫 Mode Ruang Guru (Teacher Dashboard)
- **Kelola Materi Belajar:** Menambah, mengubah, menghapus, dan mencari daftar kosakata beserta kalimat contoh dan kategorinya.
- **Data Siswa per Kelas:** Melihat daftar siswa berdasarkan kelas (Kelas 4–6) dan profil demografis siswa.

---

## 🧠 Algoritma Spaced Repetition (SM-2)

VocabKid menjadwalkan pengulangan kosakata secara adaptif menggunakan algoritma **SM-2** yang disederhanakan pada [`SpacedRepetitionAlgorithm.kt`](app/src/main/java/com/example/vocabkid/domain/algorithm/SpacedRepetitionAlgorithm.kt):

### Parameter Progress Kosakata
- `repetition`: Jumlah pengulangan benar berturut-turut.
- `intervalDays`: Jarak hari menuju jadwal review berikutnya.
- `easeFactor`: Faktor kemudahan kata (nilai awal `2.5`, batas minimum `1.3`).
- `dueDate`: Tanggal jatuh tempo pengulangan berikutnya.
- `lastReviewedDate`: Waktu terakhir kata dipelajari/diuji.
- `correctCount` & `wrongCount`: Akumulasi jawaban benar dan salah.
- `status`: Status penguasaan kata (`Baru`, `Dipelajari`, `Dikuasai`, atau `Sering Salah`).

### Pemetaan Nilai Kualitas (`quality`)
| Mode | Respons Siswa | Nilai `quality` |
| :--- | :--- | :---: |
| **Flashcard** | Ulangi | `0` |
| **Flashcard** | Sulit | `3` |
| **Flashcard** | Cukup | `4` |
| **Flashcard** | Mudah | `5` |
| **Kuis** | Jawaban Benar | `4` |
| **Kuis** | Jawaban Salah | `0` |

---

## 🛠️ Teknologi & Arsitektur

- **Bahasa Pemrograman:** Kotlin (JVM Target 17)
- **Antarmuka Pengguna (UI):** Jetpack Compose & Material 3 (mendukung *Light & Dark Theme*)
- **Arsitektur:** MVVM (*Model-View-ViewModel*) + Repository Pattern
- **Database Lokal:** Room Database (v10 dengan migrasi skema penuh `MIGRATION_1_2` s.d. `MIGRATION_9_10` & KSP)
- **Asynchronous & Reactive:** Kotlin Coroutines & StateFlow / Flow
- **Tugas Latar Belakang:** AndroidX WorkManager (`DailyStudyReminderWorker`)
- **Autentikasi:** Firebase Authentication (Email/Password) & Sesi Lokal Cepat (*Username Login*)
- **AI Assistant:** Google Generative AI SDK (`gemini-2.0-flash-lite` dengan *fallback* ke `gemini-1.5-flash`)
- **Pengujian:** JUnit 4, Kotlinx Coroutines Test, & Room `MigrationTestHelper`

---

## 📂 Struktur Proyek

```text
com.example.vocabkid
├── audio                  # Efek suara (SoundPool) & musik latar
├── data
│   ├── local
│   │   ├── dao            # Room Data Access Objects
│   │   ├── database       # VocabKidDatabase (v10) & InitialVocabulary
│   │   ├── entity         # Entitas tabel SQLite/Room
│   │   └── model          # Model agregasi query lokal
│   ├── remote             # GeminiChatService (AI Assistant)
│   └── repository         # VocabKidRepository (Single Source of Truth)
├── domain
│   ├── algorithm          # SpacedRepetitionAlgorithm (SM-2)
│   └── model              # Model domain & utilitas tanggal
├── notification           # DailyStudyReminderWorker & StudyReminderScheduler
├── presentation
│   ├── arcade             # Mini-game Memory Flip
│   ├── auth               # Login & registrasi (Siswa & Guru)
│   ├── chatassistant      # Overlay AI Chat Assistant
│   ├── components         # Komponen UI bersama, TTS, & RobotCoach
│   ├── conversation       # Latihan percakapan interaktif
│   ├── home               # Beranda siswa & statistik harian
│   ├── navigation         # Navigasi utama & Bottom Bar
│   ├── onboarding         # Pengenalan awal siswa
│   ├── profile            # Pengaturan profil, avatar, & pengingat harian
│   ├── progress           # Laporan perkembangan belajar
│   ├── pronunciation      # Latihan pelafalan suara
│   ├── quiz               # Kuis pilihan ganda bertingkat
│   ├── splash             # Layar pembuka aplikasi
│   ├── story              # 10 bab cerita interaktif & 50 ilustrasi scene
│   ├── study              # Belajar flashcard SM-2
│   ├── teacher            # Ruang Guru (Dashboard, Materi, & Data Siswa)
│   └── vocabulary         # Manajemen & detail kosakata
└── ui.theme               # Konfigurasi warna, tipografi, & tema
```

---

## 🚀 Cara Menjalankan Proyek

1. **Clone repositori ini:**
   ```bash
   git clone https://github.com/AdeAl29/VocabKid.git
   ```
2. **Buka di Android Studio** (Ladybug atau versi terbaru yang mendukung AGP `8.6.1` dan Compile SDK `35`).
3. *(Opsional)* Untuk mengaktifkan fitur **AI Chat Assistant**, tambahkan kunci API Gemini pada file `local.properties` di root project:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
4. Tunggu proses **Gradle Sync** selesai, lalu jalankan modul `app` pada emulator atau perangkat Android (minimal **Android 7.0 / SDK 24**).
5. Untuk menjalankan *unit test*:
   ```bash
   ./gradlew testDebugUnitTest
   ```

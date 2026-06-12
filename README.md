# VocabKid

VocabKid adalah aplikasi Android offline untuk membantu siswa sekolah dasar menghafal kosakata Bahasa Inggris. Aplikasi ini dibuat dengan Kotlin, Jetpack Compose, Material 3, Navigation Compose, Room Database, ViewModel, Coroutines, dan Flow.

Judul skripsi:

> Implementasi Algoritma Spaced Repetition pada Aplikasi Hafalan Kosakata Bahasa Inggris untuk Siswa Sekolah Dasar

## Fitur Aplikasi

- Onboarding siswa dengan input nama dan pilihan kelas 3, 4, 5, atau 6 SD.
- Beranda dengan sapaan siswa, jumlah kosakata jatuh tempo hari ini, kosakata dikuasai, dan latihan hari ini.
- Belajar flashcard dengan tombol Ulangi, Sulit, Cukup, dan Mudah.
- Kuis pilihan ganda dengan 4 pilihan jawaban.
- Daftar kosakata dengan fitur tambah, edit, hapus, dan detail kosakata.
- Progress belajar berisi total kosakata, kosakata dikuasai, kosakata perlu diulang, akurasi, dan jumlah review.
- Database lokal Room sehingga aplikasi tetap berjalan offline tanpa Firebase atau backend.
- Seed data awal lebih dari 1.000 kosakata Bahasa Inggris untuk siswa SD.

## Algoritma Spaced Repetition

Aplikasi menggunakan algoritma SM-2 yang disederhanakan. Setiap kosakata memiliki progress:

- `repetition`
- `intervalDays`
- `easeFactor`
- `dueDate`
- `lastReviewedDate`
- `correctCount`
- `wrongCount`
- `status`

Nilai awal progress:

- `repetition = 0`
- `intervalDays = 0`
- `easeFactor = 2.5`
- `dueDate = hari ini`
- `status = Baru`

Mapping flashcard:

- Ulangi = quality 0
- Sulit = quality 3
- Cukup = quality 4
- Mudah = quality 5

Untuk kuis:

- Jawaban benar = quality 4
- Jawaban salah = quality 0

Fungsi utama ada di:

- `domain/algorithm/SpacedRepetitionAlgorithm.kt`
- `calculateNextReview()`
- `updateProgressAfterReview()`

Fungsi tersebut diberi komentar agar mudah dijelaskan pada bagian implementasi skripsi.

## Cara Menjalankan

1. Buka folder project ini di Android Studio.
2. Tunggu proses Gradle Sync selesai.
3. Pilih emulator atau perangkat Android.
4. Jalankan modul `app`.

Konfigurasi utama:

- Package: `com.example.vocabkid`
- Minimal SDK: 24
- Compile SDK: 35
- Offline database: Room

## Struktur Project

```text
com.example.vocabkid
├── data
│   ├── local
│   │   ├── dao
│   │   ├── database
│   │   └── entity
│   └── repository
├── domain
│   ├── algorithm
│   └── model
├── presentation
│   ├── components
│   ├── home
│   ├── navigation
│   ├── onboarding
│   ├── progress
│   ├── quiz
│   ├── study
│   └── vocabulary
└── ui.theme
```

## Catatan Implementasi

- Data siswa, kosakata, progress, dan riwayat review tersimpan di Room.
- `VocabKidRepository` menjadi pusat pengelolaan data dan pemanggilan algoritma.
- ViewModel dipisah per halaman agar mudah dijelaskan dan diuji.
- Tidak ada fitur login online, Firebase, atau backend.

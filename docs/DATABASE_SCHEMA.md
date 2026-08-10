# VocaRepeat Database Schema

VocaRepeat memakai Room Database di atas SQLite. Database utama bernama `vocabkid.db` dan schema Room diekspor otomatis ke `app/schemas/com.example.vocabkid.data.local.database.VocabKidDatabase/`.

## Versi

Current version: `5`

Riwayat migrasi:

- `1 -> 2`: menambahkan kolom `avatar` pada tabel `students`.
- `2 -> 3`: menambahkan tabel `conversation_lines`.
- `3 -> 4`: menambahkan metadata audit, normalized word, dan index tambahan.
- `4 -> 5`: menambahkan progress skenario percakapan dan riwayat pilihan jawaban.

## ERD Singkat

```text
students

words 1 --- 1 word_progress
words 1 --- * review_history

conversation_lines

conversation_scenario_progress 1 --- * conversation_choice_history
```

## Tabel

### `students`

Menyimpan profil siswa lokal.

Kolom utama:

- `id`: primary key.
- `name`: nama siswa.
- `grade`: kelas siswa, dinormalisasi oleh repository ke rentang 1-6.
- `avatar`: id avatar siswa.
- `createdAt`, `updatedAt`: metadata audit.

Index:

- `name`

### `words`

Menyimpan kosakata utama.

Kolom utama:

- `id`: primary key.
- `englishWord`: kata bahasa Inggris.
- `indonesianMeaning`: arti bahasa Indonesia.
- `category`: kategori kata.
- `exampleSentence`: contoh kalimat.
- `normalizedEnglishWord`: versi lowercase dan trim untuk pencarian/anti-duplikasi logis.
- `createdAt`, `updatedAt`: metadata audit.

Index:

- `normalizedEnglishWord`
- `category, englishWord`

### `word_progress`

Menyimpan status spaced repetition per kata.

Relasi:

- `wordId` foreign key ke `words.id`.
- `ON DELETE CASCADE`, jadi progress terhapus otomatis ketika kata dihapus.

Kolom utama:

- `repetition`, `intervalDays`, `easeFactor`: parameter spaced repetition.
- `dueDate`: tanggal review berikutnya.
- `lastReviewedDate`: tanggal review terakhir.
- `correctCount`, `wrongCount`: akumulasi performa.
- `status`: `Baru`, `Dipelajari`, `Sering Salah`, atau `Dikuasai`.
- `updatedAt`: metadata audit.

Index:

- unique `wordId`
- `dueDate`
- `status`
- `lastReviewedDate`

### `review_history`

Menyimpan riwayat setiap latihan/review.

Relasi:

- `wordId` foreign key ke `words.id`.
- `ON DELETE CASCADE`, jadi riwayat ikut terhapus ketika kata dihapus.

Kolom utama:

- `reviewDate`: waktu review.
- `quality`: skor kualitas 0-5.
- `isCorrect`: apakah jawaban benar.
- `mode`: mode latihan, misalnya study atau quiz.

Index:

- `wordId`
- `reviewDate`
- `mode`

### `conversation_lines`

Menyimpan percakapan manual lama. Mode skenario chat baru memakai data lokal di presentation layer, tetapi tabel ini tetap dipertahankan agar kompatibel dengan data lama.

Kolom utama:

- `speaker`: sisi pembicara.
- `englishSentence`: kalimat bahasa Inggris.
- `indonesianMeaning`: arti Indonesia.
- `displayOrder`: urutan tampil.
- `createdAt`, `updatedAt`: metadata audit.

Index:

- `displayOrder, createdAt`

### `conversation_scenario_progress`

Menyimpan progress latihan skenario chat.

Kolom utama:

- `scenarioId`: primary key dari katalog skenario lokal.
- `sceneId`, `sceneTitle`: identitas scene.
- `scenarioTitle`: judul skenario.
- `partnerName`: nama lawan bicara.
- `totalSteps`, `completedSteps`: jumlah langkah dan progres user.
- `choiceCount`: total pilihan jawaban yang pernah dipilih.
- `completedCount`: berapa kali skenario selesai.
- `isCompleted`: apakah skenario pernah selesai.
- `firstPlayedAt`, `lastPlayedAt`: waktu mulai pertama dan latihan terakhir.
- `createdAt`, `updatedAt`: metadata audit.

Index:

- `sceneId`
- `lastPlayedAt`
- `completedCount`

### `conversation_choice_history`

Menyimpan histori pilihan jawaban user di skenario chat.

Relasi:

- `scenarioId` foreign key ke `conversation_scenario_progress.scenarioId`.
- `ON DELETE CASCADE`, jadi histori pilihan terhapus otomatis saat progress skenario dihapus.

Kolom utama:

- `scenarioId`, `sceneId`: identitas skenario dan scene.
- `stepIndex`: langkah percakapan.
- `choiceIndex`: pilihan A/B yang dipilih.
- `choiceEnglish`, `choiceIndonesian`: teks jawaban user.
- `responseEnglish`: respons lawan bicara setelah pilihan.
- `selectedAt`, `createdAt`: waktu pencatatan.

Index:

- `scenarioId`
- `sceneId`
- `selectedAt`

## Repository Layer

`VocabKidRepository` menjadi pintu utama untuk akses data. Tanggung jawab penting:

- Menyediakan Flow untuk profil siswa, kosakata, progress, statistik home, statistik progress, kategori, mode review, progress skenario chat, dan statistik chat.
- Menjalankan seeding kosakata awal dengan cek `normalizedEnglishWord`.
- Menjamin setiap kata punya row progress.
- Menjalankan spaced repetition saat review.
- Mencatat skenario chat dimulai, pilihan jawaban user, dan skenario chat selesai.
- Menyediakan audit database lewat `getDatabaseHealthReport()`.
- Menyediakan maintenance lewat `cleanupDatabase()` untuk menghapus progress/history yatim jika ada.

## File Penting

- Database: `app/src/main/java/com/example/vocabkid/data/local/database/VocabKidDatabase.kt`
- Entity: `app/src/main/java/com/example/vocabkid/data/local/entity/`
- DAO: `app/src/main/java/com/example/vocabkid/data/local/dao/`
- Repository: `app/src/main/java/com/example/vocabkid/data/repository/VocabKidRepository.kt`
- Exported schema: `app/schemas/com.example.vocabkid.data.local.database.VocabKidDatabase/5.json`

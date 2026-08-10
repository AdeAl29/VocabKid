package com.example.vocabkid.data.remote

import com.example.vocabkid.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Wrapper for Google Gemini AI API to power the VocabKid Chat Assistant.
 * Uses a persistent Chat session for multi-turn context-aware conversations.
 */
class GeminiChatService {

    private val apiKey: String = BuildConfig.GEMINI_API_KEY

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-2.0-flash-lite",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.75f
                topP = 0.95f
                topK = 40
                maxOutputTokens = 1024
            },
            systemInstruction = content {
                text(SYSTEM_INSTRUCTION)
            }
        )
    }

    private var chat = model.startChat(history = emptyList())

    val isApiKeyConfigured: Boolean
        get() = apiKey.isNotBlank()

    /**
     * Send a user message and receive the AI response as a collected string.
     * Uses streaming internally for better latency perception.
     */
    fun sendMessage(userMessage: String): Flow<String> = flow {
        if (!isApiKeyConfigured) {
            emit("⚠️ API Key belum dikonfigurasi.\n\nSilakan tambahkan Gemini API key di file `local.properties`:\n```\nGEMINI_API_KEY=your_key_here\n```\nDapatkan API key gratis di: https://aistudio.google.com/apikey")
            return@flow
        }

        try {
            val response = chat.sendMessageStream(userMessage)
            var fullResponse = ""
            response.collect { chunk ->
                val text = chunk.text
                if (text != null) {
                    fullResponse += text
                    emit(fullResponse)
                }
            }
            if (fullResponse.isBlank()) {
                emit("Maaf, saya tidak bisa menjawab saat ini. Coba tanya lagi ya! 😊")
            }
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("API_KEY", ignoreCase = true) == true ->
                    "⚠️ API Key tidak valid. Pastikan key yang benar sudah dimasukkan di `local.properties`."
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "⏳ Batas penggunaan API tercapai. Coba lagi dalam beberapa menit ya!"
                e.message?.contains("network", ignoreCase = true) == true ||
                e.message?.contains("connect", ignoreCase = true) == true ->
                    "📡 Tidak ada koneksi internet. Pastikan perangkat terhubung ke internet ya!"
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    "⏰ Waktu koneksi habis. Coba kirim ulang pertanyaanmu ya!"
                else ->
                    "Ups, terjadi kesalahan: ${e.localizedMessage ?: "tidak diketahui"}. Coba lagi ya! 🔄"
            }
            emit(errorMessage)
        }
    }

    /**
     * Reset the chat session for a fresh conversation.
     */
    fun resetChat() {
        chat = model.startChat(history = emptyList())
    }

    private companion object {
        const val SYSTEM_INSTRUCTION = """
Kamu adalah **VocabKid Assistant** 🤖 — asisten AI yang ramah, ceria, dan pintar untuk aplikasi belajar kosakata bahasa Inggris bernama **VocabKid**.

## Identitas & Sifat
- Kamu berbicara dalam **Bahasa Indonesia** secara default, kecuali diminta berbicara dalam bahasa lain.
- Gaya bahasamu **ramah, hangat, semangat, dan mudah dipahami anak SD kelas 1-6** maupun orang dewasa.
- Gunakan emoji secukupnya untuk membuat percakapan lebih hidup 🌟
- Jika ditanya hal di luar topik belajar bahasa, tetap jawab secara singkat dan ramah, lalu arahkan kembali ke konteks belajar.

## Kemampuan Utama

### 1. Menjelaskan Kosakata
- Berikan **arti** kata bahasa Inggris ke Indonesia (dan sebaliknya)
- Sertakan **contoh kalimat** dalam bahasa Inggris beserta terjemahannya
- Jelaskan **cara pengucapan** sederhana jika diminta
- Berikan **sinonim/antonim** jika relevan
- Bisa menjelaskan idiom, phrasal verb, dan ungkapan umum

### 2. Panduan Penggunaan Fitur VocabKid
Jika ditanya tentang cara menggunakan fitur, jelaskan berdasarkan informasi berikut:

- **📚 Study (Belajar)**: Fitur flashcard untuk mempelajari kosakata. Kartu menampilkan kata bahasa Inggris, tekan untuk membalik dan lihat artinya dalam bahasa Indonesia. Gunakan tombol "Sudah Hafal" atau "Belum Hafal" untuk melanjutkan.
- **❓ Quiz**: Tes pilihan ganda untuk menguji pemahaman kosakata. Pilih jawaban yang benar dari 4 pilihan. Skor ditampilkan di akhir.
- **📖 Vocabulary**: Daftar lengkap semua kosakata. Bisa mencari kata tertentu. Tekan kata untuk melihat detail lengkap beserta contoh kalimat.
- **📊 Progress**: Statistik belajar: total kata, kata yang dikuasai, akurasi, dan grafik perkembangan.
- **🗣️ Pronunciation**: Latihan pengucapan kata bahasa Inggris. Dengarkan cara pengucapan yang benar dan coba ulangi.
- **💬 Conversation**: Latihan percakapan bahasa Inggris dengan berbagai skenario (di restoran, sekolah, toko, dll). Pilih respons untuk berlatih dialog.
- **📖 Story**: Baca cerita menarik dalam bahasa Inggris sambil menjawab quiz di tengah cerita.
- **🏠 Home**: Halaman utama yang menampilkan ringkasan profil, statistik harian, dan akses cepat ke semua fitur.

### 3. Tips Belajar
- Berikan tips dan strategi belajar kosakata yang efektif
- Sarankan penggunaan spaced repetition (sistem pengulangan cerdas yang sudah ada di VocabKid)
- Motivasi pengguna untuk belajar secara rutin

## Aturan Penting
- Jawab dalam **Bahasa Indonesia** kecuali diminta menggunakan bahasa lain
- Jaga jawaban tetap **ringkas dan jelas** (maksimal 3-4 paragraf)
- Jika tidak yakin, jujur katakan dan sarankan alternatif
- JANGAN pernah memberikan informasi yang salah tentang arti kata
- JANGAN menjawab pertanyaan yang berbau SARA, kekerasan, atau konten tidak pantas
"""
    }
}

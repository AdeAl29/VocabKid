package com.example.vocabkid.data.remote

import com.example.vocabkid.BuildConfig
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Wrapper for Google Gemini AI API to power the VocabKid Chat Assistant.
 * Uses a persistent Chat session with child-safe content filters and model fallback.
 */
class GeminiChatService {

    private val apiKey: String = BuildConfig.GEMINI_API_KEY
    private var learnerContextBlock: String = ""
    private var activeModelIndex: Int = 0

    private val childSafetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.LOW_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.LOW_AND_ABOVE)
    )

    private fun createModel(modelName: String): GenerativeModel {
        val fullInstruction = if (learnerContextBlock.isBlank()) {
            SYSTEM_INSTRUCTION
        } else {
            "$SYSTEM_INSTRUCTION\n\n$learnerContextBlock"
        }
        return GenerativeModel(
            modelName = modelName,
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.7f
                topP = 0.95f
                topK = 40
                maxOutputTokens = 1024
            },
            safetySettings = childSafetySettings,
            systemInstruction = content {
                text(fullInstruction)
            }
        )
    }

    private var chat: Chat = createModel(MODEL_CANDIDATES.first()).startChat(history = emptyList())

    val isApiKeyConfigured: Boolean
        get() = apiKey.isNotBlank()

    /**
     * Updates the dynamic learner context (e.g. student name, grade, and difficult words)
     * so the AI assistant can personalize explanations and examples.
     */
    fun updateLearnerContext(
        studentName: String?,
        grade: Int?,
        difficultWords: List<String>
    ) {
        val builder = StringBuilder("## Konteks Siswa Saat Ini\n")
        if (!studentName.isNullOrBlank()) {
            builder.append("- Nama siswa: $studentName\n")
        }
        if (grade != null) {
            builder.append("- Kelas: $grade SD\n")
        }
        if (difficultWords.isNotEmpty()) {
            builder.append("- Kata yang masih sering salah / perlu dilatih: ${difficultWords.joinToString(", ")}\n")
        }
        val newContext = builder.toString().trim()
        if (newContext != learnerContextBlock) {
            learnerContextBlock = newContext
            resetChat()
        }
    }

    /**
     * Send a user message and receive the AI response as a collected string.
     * Uses streaming internally for better latency perception and falls back to secondary model if needed.
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
            // Try fallback model once if model not found or overloaded
            val shouldFallback = activeModelIndex < MODEL_CANDIDATES.lastIndex &&
                (e.message?.contains("not found", ignoreCase = true) == true ||
                    e.message?.contains("overloaded", ignoreCase = true) == true ||
                    e.message?.contains("503", ignoreCase = true) == true)

            if (shouldFallback) {
                activeModelIndex++
                chat = createModel(MODEL_CANDIDATES[activeModelIndex]).startChat(history = emptyList())
                try {
                    val retryResponse = chat.sendMessageStream(userMessage)
                    var fullRetryText = ""
                    retryResponse.collect { chunk ->
                        val text = chunk.text
                        if (text != null) {
                            fullRetryText += text
                            emit(fullRetryText)
                        }
                    }
                    if (fullRetryText.isNotBlank()) return@flow
                } catch (_: Exception) {
                    // Proceed to user-friendly error message below
                }
            }

            val errorMessage = when {
                e.message?.contains("API_KEY", ignoreCase = true) == true ->
                    "⚠️ API Key tidak valid. Pastikan key yang benar sudah dimasukkan di `local.properties`."
                e.message?.contains("quota", ignoreCase = true) == true ->
                    "⏳ Batas penggunaan API tercapai. Coba lagi dalam beberapa menit ya!"
                e.message?.contains("SAFETY", ignoreCase = true) == true ->
                    "🛡️ Maaf, pertanyaan ini tidak dapat dijawab karena alasan keamanan konten anak. Yuk tanya seputar kosakata bahasa Inggris! 🌟"
                e.message?.contains("network", ignoreCase = true) == true ||
                e.message?.contains("connect", ignoreCase = true) == true ||
                e.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
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
        chat = createModel(MODEL_CANDIDATES[activeModelIndex]).startChat(history = emptyList())
    }

    private companion object {
        val MODEL_CANDIDATES = listOf(
            "gemini-2.0-flash-lite",
            "gemini-1.5-flash"
        )

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
- **🕹️ Arcade**: Taman bermain kata seperti Memory Flip untuk mencocokkan kartu kosakata sambil bermain.
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

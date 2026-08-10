package com.example.vocabkid.presentation.pronunciation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.repository.VocabKidRepository
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

enum class PronunciationResult {
    IDLE,
    CORRECT,
    INCORRECT
}

data class PronunciationUiState(
    val currentWord: WordWithProgressEntity? = null,
    val isListening: Boolean = false,
    val recognizedText: String = "",
    val result: PronunciationResult = PronunciationResult.IDLE,
    val isLoading: Boolean = true,
    val sessionCorrect: Int = 0,
    val sessionTotal: Int = 0,
    val streak: Int = 0,
    val wordIndex: Int = 0,
    val totalWords: Int = 0
)

class PronunciationViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PronunciationUiState())
    val uiState: StateFlow<PronunciationUiState> = _uiState.asStateFlow()

    private var allVocabulary: List<WordWithProgressEntity> = emptyList()
    private var currentIndex = 0

    init {
        loadVocabulary()
    }

    private fun loadVocabulary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val words = repository.observeWordsWithProgress().first()
            if (words.isNotEmpty()) {
                allVocabulary = words.shuffled()
                currentIndex = 0
                _uiState.value = _uiState.value.copy(
                    currentWord = allVocabulary[currentIndex],
                    isLoading = false,
                    result = PronunciationResult.IDLE,
                    recognizedText = "",
                    wordIndex = 1,
                    totalWords = allVocabulary.size
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun nextWord() {
        if (allVocabulary.isEmpty()) return
        currentIndex = (currentIndex + 1) % allVocabulary.size
        _uiState.value = _uiState.value.copy(
            currentWord = allVocabulary[currentIndex],
            result = PronunciationResult.IDLE,
            recognizedText = "",
            isListening = false,
            wordIndex = currentIndex + 1
        )
    }

    fun repeatWord() {
        _uiState.value = _uiState.value.copy(
            result = PronunciationResult.IDLE,
            recognizedText = "",
            isListening = false
        )
    }

    fun setListeningState(isListening: Boolean) {
        _uiState.value = _uiState.value.copy(isListening = isListening)
    }

    fun onSpeechResult(spokenText: String) {
        val currentWord = _uiState.value.currentWord ?: return

        val spokenClean = cleanText(spokenText)
        val expectedClean = cleanText(currentWord.word.englishWord)

        val isCorrect = checkPronunciation(spokenClean, expectedClean)

        val newStreak = if (isCorrect) _uiState.value.streak + 1 else 0
        val newCorrect = _uiState.value.sessionCorrect + if (isCorrect) 1 else 0
        val newTotal = _uiState.value.sessionTotal + 1

        _uiState.value = _uiState.value.copy(
            recognizedText = spokenText,
            result = if (isCorrect) PronunciationResult.CORRECT else PronunciationResult.INCORRECT,
            isListening = false,
            streak = newStreak,
            sessionCorrect = newCorrect,
            sessionTotal = newTotal
        )
    }

    fun onSpeechPartialResult(partialText: String) {
        _uiState.value = _uiState.value.copy(
            recognizedText = partialText,
            result = PronunciationResult.IDLE
        )
    }

    fun onSpeechError() {
        _uiState.value = _uiState.value.copy(
            isListening = false,
            result = PronunciationResult.IDLE,
            recognizedText = "Coba lagi..."
        )
    }

    private fun checkPronunciation(spoken: String, expected: String): Boolean {
        if (spoken.isEmpty()) return false
        if (spoken == expected) return true
        if (spoken.contains(expected)) return true
        if (expected.contains(spoken) && spoken.length > 2) return true

        // Fuzzy Levenshtein matching – tolerant for kids with accent
        val maxDist = when {
            expected.length <= 4 -> 1
            expected.length <= 7 -> 2
            else -> 3
        }
        if (levenshtein(spoken, expected) <= maxDist) return true

        // Token match – speech result might include extra words
        return spoken.split(" ").any { token ->
            token == expected || levenshtein(token, expected) <= maxDist
        }
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
            }
        }
        return dp[a.length][b.length]
    }

    private fun cleanText(text: String): String {
        return text.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9 ]"), "")
            .trim()
    }
}


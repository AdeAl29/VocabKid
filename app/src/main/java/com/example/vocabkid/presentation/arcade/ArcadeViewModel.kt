package com.example.vocabkid.presentation.arcade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.repository.VocabKidRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── Data Models ─────────────────────────────────────────────────────────────────

data class MemoryCard(
    val id: Int,
    val pairId: Int,
    val displayText: String,
    val isImage: Boolean, // true = shows emoji/category icon, false = shows English word
    val category: String,
    val englishWord: String,
    val indonesianMeaning: String,
    val isFlipped: Boolean = false,
    val isMatched: Boolean = false
)

enum class GameDifficulty(val label: String, val pairs: Int, val columns: Int) {
    EASY("Mudah", 4, 2),
    MEDIUM("Sedang", 6, 3),
    HARD("Seru!", 8, 4)
}

enum class ArcadeScreenMode {
    HUB,
    CATEGORY_SELECT,
    GAME,
    RESULT
}

data class GameResult(
    val totalPairs: Int,
    val moves: Int,
    val timeSeconds: Int,
    val stars: Int // 1-3
)

data class ArcadeUiState(
    val mode: ArcadeScreenMode = ArcadeScreenMode.HUB,
    val availableCategories: List<String> = emptyList(),
    val selectedCategory: String? = null, // null = campuran
    val difficulty: GameDifficulty = GameDifficulty.EASY,
    val cards: List<MemoryCard> = emptyList(),
    val firstFlippedIndex: Int? = null,
    val secondFlippedIndex: Int? = null,
    val moves: Int = 0,
    val matchedPairs: Int = 0,
    val totalPairs: Int = 0,
    val isChecking: Boolean = false,
    val elapsedSeconds: Int = 0,
    val gameResult: GameResult? = null,
    val isLoading: Boolean = false
)

// ── ViewModel ───────────────────────────────────────────────────────────────────

class ArcadeViewModel(
    private val repository: VocabKidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArcadeUiState())
    val uiState: StateFlow<ArcadeUiState> = _uiState.asStateFlow()

    private var allWords: List<WordEntity> = emptyList()
    private var timerRunning = false

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            allWords = repository.getAllWords()
            val categories = allWords.map { it.category }.distinct().sorted()
            _uiState.update {
                it.copy(
                    availableCategories = categories,
                    isLoading = false
                )
            }
        }
    }

    fun navigateToHub() {
        timerRunning = false
        _uiState.update {
            ArcadeUiState(
                mode = ArcadeScreenMode.HUB,
                availableCategories = it.availableCategories
            )
        }
    }

    fun navigateToCategorySelect() {
        _uiState.update { it.copy(mode = ArcadeScreenMode.CATEGORY_SELECT) }
    }

    fun selectCategoryAndDifficulty(category: String?, difficulty: GameDifficulty) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                difficulty = difficulty
            )
        }
        startGame(category, difficulty)
    }

    private fun startGame(category: String?, difficulty: GameDifficulty) {
        val sourceWords = if (category != null) {
            allWords.filter { it.category == category }
        } else {
            allWords
        }

        if (sourceWords.size < difficulty.pairs) {
            // Not enough words — fallback to all words
            startGame(null, difficulty)
            return
        }

        val selectedWords = sourceWords.shuffled().take(difficulty.pairs)
        val cards = mutableListOf<MemoryCard>()

        selectedWords.forEachIndexed { index, word ->
            // Card A: shows the emoji/meaning (Indonesian)
            cards.add(
                MemoryCard(
                    id = index * 2,
                    pairId = index,
                    displayText = word.indonesianMeaning,
                    isImage = true,
                    category = word.category,
                    englishWord = word.englishWord,
                    indonesianMeaning = word.indonesianMeaning
                )
            )
            // Card B: shows the English word
            cards.add(
                MemoryCard(
                    id = index * 2 + 1,
                    pairId = index,
                    displayText = word.englishWord,
                    isImage = false,
                    category = word.category,
                    englishWord = word.englishWord,
                    indonesianMeaning = word.indonesianMeaning
                )
            )
        }

        _uiState.update {
            it.copy(
                mode = ArcadeScreenMode.GAME,
                cards = cards.shuffled(),
                firstFlippedIndex = null,
                secondFlippedIndex = null,
                moves = 0,
                matchedPairs = 0,
                totalPairs = difficulty.pairs,
                isChecking = false,
                elapsedSeconds = 0,
                gameResult = null
            )
        }

        // Start timer
        timerRunning = true
        viewModelScope.launch {
            while (timerRunning) {
                kotlinx.coroutines.delay(1000)
                if (timerRunning) {
                    _uiState.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
                }
            }
        }
    }

    fun flipCard(cardIndex: Int) {
        val state = _uiState.value
        if (state.isChecking) return

        val card = state.cards[cardIndex]
        if (card.isFlipped || card.isMatched) return

        if (state.firstFlippedIndex == null) {
            // First card flip
            _uiState.update {
                it.copy(
                    firstFlippedIndex = cardIndex,
                    cards = it.cards.mapIndexed { i, c ->
                        if (i == cardIndex) c.copy(isFlipped = true) else c
                    }
                )
            }
        } else if (state.secondFlippedIndex == null) {
            // Second card flip
            val firstCard = state.cards[state.firstFlippedIndex]
            val newMoves = state.moves + 1

            _uiState.update {
                it.copy(
                    secondFlippedIndex = cardIndex,
                    moves = newMoves,
                    isChecking = true,
                    cards = it.cards.mapIndexed { i, c ->
                        if (i == cardIndex) c.copy(isFlipped = true) else c
                    }
                )
            }

            // Check match
            viewModelScope.launch {
                kotlinx.coroutines.delay(700) // show cards briefly

                if (firstCard.pairId == card.pairId) {
                    // Match!
                    val newMatchedPairs = state.matchedPairs + 1
                    _uiState.update {
                        it.copy(
                            cards = it.cards.map { c ->
                                if (c.pairId == card.pairId) c.copy(isMatched = true, isFlipped = true)
                                else c
                            },
                            matchedPairs = newMatchedPairs,
                            firstFlippedIndex = null,
                            secondFlippedIndex = null,
                            isChecking = false
                        )
                    }

                    // Check if game is complete
                    if (newMatchedPairs == state.totalPairs) {
                        timerRunning = false
                        val finalState = _uiState.value
                        val stars = calculateStars(
                            moves = finalState.moves,
                            pairs = finalState.totalPairs,
                            timeSeconds = finalState.elapsedSeconds
                        )
                        _uiState.update {
                            it.copy(
                                mode = ArcadeScreenMode.RESULT,
                                gameResult = GameResult(
                                    totalPairs = finalState.totalPairs,
                                    moves = finalState.moves,
                                    timeSeconds = finalState.elapsedSeconds,
                                    stars = stars
                                )
                            )
                        }
                    }
                } else {
                    // No match — flip back
                    _uiState.update {
                        it.copy(
                            cards = it.cards.mapIndexed { i, c ->
                                if (i == state.firstFlippedIndex || i == cardIndex) {
                                    c.copy(isFlipped = false)
                                } else c
                            },
                            firstFlippedIndex = null,
                            secondFlippedIndex = null,
                            isChecking = false
                        )
                    }
                }
            }
        }
    }

    fun playAgain() {
        val state = _uiState.value
        startGame(state.selectedCategory, state.difficulty)
    }

    private fun calculateStars(moves: Int, pairs: Int, timeSeconds: Int): Int {
        val perfectMoves = pairs // absolute minimum moves
        val moveRatio = moves.toFloat() / perfectMoves
        val timePerPair = timeSeconds.toFloat() / pairs

        return when {
            moveRatio <= 1.8f && timePerPair <= 8f -> 3
            moveRatio <= 2.8f && timePerPair <= 14f -> 2
            else -> 1
        }
    }
}

package com.example.vocabkid.data.repository

import androidx.room.withTransaction
import com.example.vocabkid.data.local.database.InitialVocabulary
import com.example.vocabkid.data.local.database.VocabKidDatabase
import com.example.vocabkid.data.local.entity.ReviewHistoryEntity
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordProgressEntity
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import com.example.vocabkid.domain.algorithm.SpacedRepetitionAlgorithm
import com.example.vocabkid.domain.model.DateUtils
import com.example.vocabkid.domain.model.HomeStats
import com.example.vocabkid.domain.model.ProgressStats
import com.example.vocabkid.domain.model.QuizQuestion
import com.example.vocabkid.domain.model.ReviewMode
import com.example.vocabkid.domain.model.WordStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class VocabKidRepository(
    private val database: VocabKidDatabase
) {
    private val studentDao = database.studentDao()
    private val wordDao = database.wordDao()
    private val progressDao = database.wordProgressDao()
    private val historyDao = database.reviewHistoryDao()

    fun observeStudent(): Flow<StudentEntity?> = studentDao.observeStudent()

    fun observeWordsWithProgress(): Flow<List<WordWithProgressEntity>> {
        return wordDao.observeWordsWithProgress()
    }

    fun observeWordWithProgress(wordId: Long): Flow<WordWithProgressEntity?> {
        return wordDao.observeWordWithProgress(wordId)
    }

    fun observeDueWords(): Flow<List<WordWithProgressEntity>> {
        return wordDao.observeDueWords(DateUtils.todayMillis())
    }

    fun observeHomeStats(): Flow<HomeStats> {
        val today = DateUtils.todayMillis()
        return combine(
            progressDao.observeDueCount(today),
            progressDao.observeMasteredCount(),
            historyDao.observeReviewCountSince(today),
            wordDao.observeWordCount()
        ) { dueToday, masteredWords, reviewsToday, totalWords ->
            HomeStats(
                dueToday = dueToday,
                masteredWords = masteredWords,
                reviewsToday = reviewsToday,
                totalWords = totalWords
            )
        }
    }

    fun observeProgressStats(): Flow<ProgressStats> {
        val today = DateUtils.todayMillis()
        val statFlows = listOf(
            wordDao.observeWordCount(),
            progressDao.observeMasteredCount(),
            progressDao.observeDueCount(today),
            progressDao.observeFrequentlyWrongCount(),
            historyDao.observeCorrectAnswerCount(),
            historyDao.observeAnswerCount(),
            historyDao.observeTotalReviewCount()
        )

        return combine(statFlows) { values ->
            val totalWords = values[0]
            val masteredWords = values[1]
            val dueWords = values[2]
            val frequentlyWrongWords = values[3]
            val correctAnswers = values[4]
            val answerCount = values[5]
            val totalReviews = values[6]
            val accuracy = if (answerCount == 0) {
                0
            } else {
                ((correctAnswers.toDouble() / answerCount.toDouble()) * 100).toInt()
            }

            ProgressStats(
                totalWords = totalWords,
                masteredWords = masteredWords,
                dueWords = dueWords,
                frequentlyWrongWords = frequentlyWrongWords,
                accuracyPercent = accuracy,
                totalReviews = totalReviews
            )
        }
    }

    suspend fun saveStudent(name: String, grade: Int) {
        database.withTransaction {
            studentDao.clearStudents()
            studentDao.insertStudent(StudentEntity(name = name.trim(), grade = grade))
        }
    }

    suspend fun seedInitialVocabularyIfNeeded() {
        database.withTransaction {
            val today = DateUtils.todayMillis()
            val existingWords = wordDao.getNormalizedEnglishWords().toSet()
            val newWords = InitialVocabulary.words.filterNot { word ->
                word.englishWord.trim().lowercase() in existingWords
            }

            if (newWords.isNotEmpty()) {
                val ids = wordDao.insertWords(newWords)
                val progressList = ids.map { wordId ->
                    createInitialProgress(wordId = wordId, today = today)
                }
                progressDao.insertProgressList(progressList)
            }

            ensureProgressForExistingWords(today)
        }
    }

    suspend fun addWord(
        englishWord: String,
        indonesianMeaning: String,
        category: String,
        exampleSentence: String
    ) {
        database.withTransaction {
            val wordId = wordDao.insertWord(
                WordEntity(
                    englishWord = englishWord.trim(),
                    indonesianMeaning = indonesianMeaning.trim(),
                    category = category.trim(),
                    exampleSentence = exampleSentence.trim()
                )
            )
            progressDao.insertProgress(createInitialProgress(wordId, DateUtils.todayMillis()))
        }
    }

    suspend fun updateWord(word: WordEntity) {
        wordDao.updateWord(word)
    }

    suspend fun deleteWord(word: WordEntity) {
        wordDao.deleteWord(word)
    }

    suspend fun reviewWord(
        wordId: Long,
        quality: Int,
        mode: ReviewMode,
        isCorrectOverride: Boolean? = null
    ) {
        database.withTransaction {
            val today = DateUtils.todayMillis()
            val currentProgress = progressDao.getProgressByWordId(wordId)
                ?: createInitialProgress(wordId = wordId, today = today)
            val updatedProgress = SpacedRepetitionAlgorithm.updateProgressAfterReview(
                current = currentProgress,
                quality = quality,
                todayMillis = today
            )
            progressDao.insertProgress(updatedProgress)
            historyDao.insertHistory(
                ReviewHistoryEntity(
                    wordId = wordId,
                    reviewDate = System.currentTimeMillis(),
                    quality = quality.coerceIn(0, 5),
                    isCorrect = isCorrectOverride ?: (quality >= 3),
                    mode = mode.name
                )
            )
        }
    }

    suspend fun createQuizQuestions(questionCount: Int = 10): List<QuizQuestion> {
        val words = wordDao.getAllWords()
        if (words.size < 4) return emptyList()

        val meanings = words.map { it.indonesianMeaning }.distinct()
        return words.shuffled().take(questionCount.coerceAtMost(words.size)).map { word ->
            val distractors = meanings
                .filterNot { it == word.indonesianMeaning }
                .shuffled()
                .take(3)
            QuizQuestion(
                word = word,
                choices = (distractors + word.indonesianMeaning).shuffled()
            )
        }
    }

    private suspend fun ensureProgressForExistingWords(today: Long = DateUtils.todayMillis()) {
        val existingProgressWordIds = progressDao.getProgressWordIds().toSet()
        val missingProgress = wordDao.getAllWords()
            .filterNot { it.id in existingProgressWordIds }
            .map { word -> createInitialProgress(word.id, today) }

        if (missingProgress.isNotEmpty()) {
            progressDao.insertProgressList(missingProgress)
        }
    }

    private fun createInitialProgress(wordId: Long, today: Long): WordProgressEntity {
        return WordProgressEntity(
            wordId = wordId,
            repetition = 0,
            intervalDays = 0,
            easeFactor = 2.5,
            dueDate = today,
            lastReviewedDate = null,
            correctCount = 0,
            wrongCount = 0,
            status = WordStatus.NEW
        )
    }
}

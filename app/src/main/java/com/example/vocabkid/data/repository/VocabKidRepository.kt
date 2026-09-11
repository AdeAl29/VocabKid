package com.example.vocabkid.data.repository

import androidx.room.withTransaction
import com.example.vocabkid.data.local.database.InitialVocabulary
import com.example.vocabkid.data.local.database.VocabKidDatabase
import com.example.vocabkid.data.local.entity.ConversationChoiceHistoryEntity
import com.example.vocabkid.data.local.entity.ConversationLineEntity
import com.example.vocabkid.data.local.entity.ConversationScenarioProgressEntity
import com.example.vocabkid.data.local.entity.ReviewHistoryEntity
import com.example.vocabkid.data.local.entity.StudentEntity
import com.example.vocabkid.data.local.entity.WordEntity
import com.example.vocabkid.data.local.entity.WordProgressEntity
import com.example.vocabkid.data.local.entity.WordWithProgressEntity
import com.example.vocabkid.data.local.model.CategoryCount
import com.example.vocabkid.data.local.model.ReviewModeCount
import com.example.vocabkid.domain.algorithm.SpacedRepetitionAlgorithm
import com.example.vocabkid.domain.model.ConversationPracticeStats
import com.example.vocabkid.domain.model.DatabaseHealthReport
import com.example.vocabkid.domain.model.DatabaseMaintenanceResult
import com.example.vocabkid.domain.model.DateUtils
import com.example.vocabkid.domain.model.HomeStats
import com.example.vocabkid.domain.model.ProgressStats
import com.example.vocabkid.domain.model.QuizQuestion
import com.example.vocabkid.domain.model.ReviewMode
import com.example.vocabkid.domain.model.StudentAvatar
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
    private val conversationLineDao = database.conversationLineDao()
    private val conversationProgressDao = database.conversationProgressDao()

    fun observeStudent(): Flow<StudentEntity?> = studentDao.observeStudent()

    fun observeWordsWithProgress(): Flow<List<WordWithProgressEntity>> {
        return wordDao.observeWordsWithProgress()
    }

    fun observeWordWithProgress(wordId: Long): Flow<WordWithProgressEntity?> {
        return wordDao.observeWordWithProgress(wordId)
    }

    fun searchWordsWithProgress(query: String): Flow<List<WordWithProgressEntity>> {
        return wordDao.searchWordsWithProgress(query)
    }

    fun observeDueWords(): Flow<List<WordWithProgressEntity>> {
        return wordDao.observeDueWords(DateUtils.todayMillis())
    }

    fun observeDifficultWords(limit: Int = 12): Flow<List<WordWithProgressEntity>> {
        return wordDao.observeDifficultWords(limit)
    }

    fun observeConversationLines(): Flow<List<ConversationLineEntity>> {
        return conversationLineDao.observeConversationLines()
    }

    fun observeConversationScenarioProgress(): Flow<List<ConversationScenarioProgressEntity>> {
        return conversationProgressDao.observeScenarioProgress()
    }

    fun observeConversationPracticeStats(): Flow<ConversationPracticeStats> {
        return conversationProgressDao.observePracticeStats()
    }

    fun observeCategoryCounts(): Flow<List<CategoryCount>> {
        return wordDao.observeCategoryCounts()
    }

    fun observeReviewModeCounts(): Flow<List<ReviewModeCount>> {
        return historyDao.observeReviewModeCounts()
    }

    fun observeHomeStats(): Flow<HomeStats> {
        val today = DateUtils.todayMillis()
        return combine(
            progressDao.observeDueCount(today),
            progressDao.observeMasteredCount(),
            historyDao.observeReviewCountSince(today),
            wordDao.observeWordCount(),
            progressDao.observeFrequentlyWrongCount()
        ) { dueToday, masteredWords, reviewsToday, totalWords, frequentlyWrongWords ->
            HomeStats(
                dueToday = dueToday,
                masteredWords = masteredWords,
                reviewsToday = reviewsToday,
                totalWords = totalWords,
                frequentlyWrongWords = frequentlyWrongWords
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

    suspend fun saveStudent(
        name: String,
        grade: Int,
        avatar: StudentAvatar = StudentAvatar.SISWA,
        nis: String = ""
    ) {
        val now = System.currentTimeMillis()
        database.withTransaction {
            studentDao.clearStudents()
            studentDao.insertStudent(
                StudentEntity(
                    name = name.trim().ifBlank { DEFAULT_STUDENT_NAME },
                    nis = nis.trim(),
                    grade = grade.coerceIn(MIN_GRADE, MAX_GRADE),
                    avatar = avatar.id,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun clearStudent() {
        studentDao.clearStudents()
    }

    suspend fun addConversationLine(
        speaker: String,
        englishSentence: String,
        indonesianMeaning: String
    ) {
        val now = System.currentTimeMillis()
        val displayOrder = conversationLineDao.nextDisplayOrder()
        conversationLineDao.insertConversationLine(
            ConversationLineEntity(
                speaker = speaker,
                englishSentence = englishSentence.trim(),
                indonesianMeaning = indonesianMeaning.trim(),
                displayOrder = displayOrder,
                createdAt = now,
                updatedAt = now
            )
        )
    }

    suspend fun deleteConversationLine(line: ConversationLineEntity) {
        conversationLineDao.deleteConversationLine(line)
    }

    suspend fun recordConversationScenarioStarted(
        scenarioId: String,
        sceneId: String,
        sceneTitle: String,
        scenarioTitle: String,
        partnerName: String,
        totalSteps: Int
    ) {
        database.withTransaction {
            val now = System.currentTimeMillis()
            val currentProgress = conversationProgressDao.getScenarioProgress(scenarioId)
            conversationProgressDao.insertScenarioProgress(
                currentProgress?.copy(
                    sceneId = sceneId,
                    sceneTitle = sceneTitle,
                    scenarioTitle = scenarioTitle,
                    partnerName = partnerName,
                    totalSteps = totalSteps,
                    lastPlayedAt = now,
                    updatedAt = now
                ) ?: ConversationScenarioProgressEntity(
                    scenarioId = scenarioId,
                    sceneId = sceneId,
                    sceneTitle = sceneTitle,
                    scenarioTitle = scenarioTitle,
                    partnerName = partnerName,
                    totalSteps = totalSteps,
                    firstPlayedAt = now,
                    lastPlayedAt = now,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun recordConversationChoice(
        scenarioId: String,
        sceneId: String,
        sceneTitle: String,
        scenarioTitle: String,
        partnerName: String,
        totalSteps: Int,
        stepIndex: Int,
        choiceIndex: Int,
        choiceEnglish: String,
        choiceIndonesian: String,
        responseEnglish: String
    ) {
        database.withTransaction {
            val now = System.currentTimeMillis()
            val currentProgress = conversationProgressDao.getScenarioProgress(scenarioId)
            val nextCompletedSteps = maxOf(
                currentProgress?.completedSteps ?: 0,
                (stepIndex + 1).coerceIn(0, totalSteps)
            )

            conversationProgressDao.insertScenarioProgress(
                currentProgress?.copy(
                    sceneId = sceneId,
                    sceneTitle = sceneTitle,
                    scenarioTitle = scenarioTitle,
                    partnerName = partnerName,
                    totalSteps = totalSteps,
                    completedSteps = nextCompletedSteps,
                    choiceCount = currentProgress.choiceCount + 1,
                    lastPlayedAt = now,
                    updatedAt = now
                ) ?: ConversationScenarioProgressEntity(
                    scenarioId = scenarioId,
                    sceneId = sceneId,
                    sceneTitle = sceneTitle,
                    scenarioTitle = scenarioTitle,
                    partnerName = partnerName,
                    totalSteps = totalSteps,
                    completedSteps = nextCompletedSteps,
                    choiceCount = 1,
                    firstPlayedAt = now,
                    lastPlayedAt = now,
                    createdAt = now,
                    updatedAt = now
                )
            )

            conversationProgressDao.insertChoiceHistory(
                ConversationChoiceHistoryEntity(
                    scenarioId = scenarioId,
                    sceneId = sceneId,
                    stepIndex = stepIndex,
                    choiceIndex = choiceIndex,
                    choiceEnglish = choiceEnglish.trim(),
                    choiceIndonesian = choiceIndonesian.trim(),
                    responseEnglish = responseEnglish.trim(),
                    selectedAt = now,
                    createdAt = now
                )
            )
        }
    }

    suspend fun recordConversationScenarioCompleted(
        scenarioId: String,
        sceneId: String,
        sceneTitle: String,
        scenarioTitle: String,
        partnerName: String,
        totalSteps: Int
    ) {
        database.withTransaction {
            val now = System.currentTimeMillis()
            val currentProgress = conversationProgressDao.getScenarioProgress(scenarioId)
            conversationProgressDao.insertScenarioProgress(
                currentProgress?.copy(
                    sceneId = sceneId,
                    sceneTitle = sceneTitle,
                    scenarioTitle = scenarioTitle,
                    partnerName = partnerName,
                    totalSteps = totalSteps,
                    completedSteps = totalSteps,
                    completedCount = currentProgress.completedCount + 1,
                    isCompleted = true,
                    lastPlayedAt = now,
                    updatedAt = now
                ) ?: ConversationScenarioProgressEntity(
                    scenarioId = scenarioId,
                    sceneId = sceneId,
                    sceneTitle = sceneTitle,
                    scenarioTitle = scenarioTitle,
                    partnerName = partnerName,
                    totalSteps = totalSteps,
                    completedSteps = totalSteps,
                    completedCount = 1,
                    isCompleted = true,
                    firstPlayedAt = now,
                    lastPlayedAt = now,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    suspend fun seedInitialVocabularyIfNeeded() {
        database.withTransaction {
            val today = DateUtils.todayMillis()
            val existingWords = wordDao.getNormalizedEnglishWords().toSet()
            val now = System.currentTimeMillis()
            val newWords = InitialVocabulary.words
                .map { word -> word.normalizedForStorage(now) }
                .filterNot { word -> word.normalizedEnglishWord in existingWords }

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
            val now = System.currentTimeMillis()
            val today = DateUtils.todayMillis()
            val normalizedEnglish = normalizeEnglishWord(englishWord)
            val existingWord = wordDao.getWordByNormalizedEnglish(normalizedEnglish)
            val wordForStorage = WordEntity(
                englishWord = englishWord,
                indonesianMeaning = indonesianMeaning,
                category = category,
                exampleSentence = exampleSentence,
                createdAt = now,
                updatedAt = now
            ).normalizedForStorage(now)

            val wordId = if (existingWord != null) {
                wordDao.updateWord(
                    wordForStorage.copy(
                        id = existingWord.id,
                        createdAt = existingWord.createdAt.takeIf { it > 0 } ?: now
                    )
                )
                existingWord.id
            } else {
                wordDao.insertWord(wordForStorage)
            }

            ensureProgressForWord(wordId = wordId, today = today)
        }
    }

    suspend fun updateWord(word: WordEntity) {
        wordDao.updateWord(word.normalizedForStorage())
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
            val now = System.currentTimeMillis()
            val currentProgress = progressDao.getProgressByWordId(wordId)
                ?: createInitialProgress(wordId = wordId, today = today)
            val updatedProgress = SpacedRepetitionAlgorithm.updateProgressAfterReview(
                current = currentProgress,
                quality = quality,
                todayMillis = today
            ).copy(updatedAt = now)
            progressDao.insertProgress(updatedProgress)
            historyDao.insertHistory(
                ReviewHistoryEntity(
                    wordId = wordId,
                    reviewDate = now,
                    quality = quality.coerceIn(0, 5),
                    isCorrect = isCorrectOverride ?: (quality >= 3),
                    mode = mode.name
                )
            )
        }
    }

    suspend fun resetWordProgress(wordId: Long) {
        val today = DateUtils.todayMillis()
        val updatedRows = progressDao.resetProgressForWord(
            wordId = wordId,
            today = today
        )

        if (updatedRows == 0) {
            progressDao.insertProgress(createInitialProgress(wordId, today))
        }
    }

    suspend fun getRecentReviewHistory(
        wordId: Long,
        limit: Int = 20
    ): List<ReviewHistoryEntity> {
        return historyDao.getRecentHistoryForWord(
            wordId = wordId,
            limit = limit.coerceIn(1, 100)
        )
    }

    suspend fun cleanupDatabase(): DatabaseMaintenanceResult {
        return database.withTransaction {
            DatabaseMaintenanceResult(
                deletedOrphanProgressRows = progressDao.deleteOrphanProgress(),
                deletedOrphanHistoryRows = historyDao.deleteOrphanHistory()
            )
        }
    }

    suspend fun getDatabaseHealthReport(): DatabaseHealthReport {
        return DatabaseHealthReport(
            studentCount = studentDao.countStudents(),
            wordCount = wordDao.countWords(),
            progressRowCount = progressDao.countProgressRows(),
            reviewHistoryCount = historyDao.countReviewHistory(),
            conversationLineCount = conversationLineDao.countConversationLines(),
            conversationScenarioProgressCount = conversationProgressDao.countScenarioProgressRows(),
            conversationChoiceHistoryCount = conversationProgressDao.countChoiceHistoryRows()
        )
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

    private suspend fun ensureProgressForWord(wordId: Long, today: Long) {
        if (progressDao.getProgressByWordId(wordId) == null) {
            progressDao.insertProgress(createInitialProgress(wordId, today))
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
            status = WordStatus.NEW,
            updatedAt = System.currentTimeMillis()
        )
    }

    private fun WordEntity.normalizedForStorage(
        now: Long = System.currentTimeMillis()
    ): WordEntity {
        return copy(
            englishWord = englishWord.trim(),
            indonesianMeaning = indonesianMeaning.trim(),
            category = category.trim().ifBlank { DEFAULT_CATEGORY },
            exampleSentence = exampleSentence.trim(),
            normalizedEnglishWord = normalizeEnglishWord(englishWord),
            createdAt = createdAt.takeIf { it > 0 } ?: now,
            updatedAt = now
        )
    }

    private fun normalizeEnglishWord(value: String): String {
        return value.trim().lowercase()
    }

    private companion object {
        const val DEFAULT_STUDENT_NAME = "Siswa VocabKid"
        const val DEFAULT_CATEGORY = "Object"
        const val MIN_GRADE = 1
        const val MAX_GRADE = 6
    }
}

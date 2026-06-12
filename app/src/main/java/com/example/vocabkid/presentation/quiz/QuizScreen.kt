package com.example.vocabkid.presentation.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vocabkid.presentation.components.EmptyMessage
import com.example.vocabkid.presentation.components.KidTopBar

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    onBackClick: () -> Unit
) {
    val state = viewModel.uiState
    val currentQuestion = state.currentQuestion

    Scaffold(
        topBar = {
            KidTopBar(
                title = "Latihan Kuis",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                    Text(text = "Menyiapkan soal...")
                }

                state.questions.isEmpty() -> {
                    EmptyMessage(
                        title = "Belum cukup kosakata",
                        message = "Minimal perlu 4 kata untuk membuat kuis pilihan ganda."
                    )
                }

                state.isFinished -> {
                    QuizResult(
                        score = state.score,
                        total = state.questions.size,
                        onRestartClick = viewModel::restartQuiz
                    )
                }

                currentQuestion != null -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Soal ${state.currentIndex + 1} dari ${state.questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearProgressIndicator(
                            progress = {
                                (state.currentIndex + 1).toFloat() / state.questions.size.toFloat()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Apa arti kata ini?",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = currentQuestion.word.englishWord,
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    currentQuestion.choices.forEach { choice ->
                        ChoiceButton(
                            choice = choice,
                            selectedAnswer = state.selectedAnswer,
                            correctAnswer = currentQuestion.correctAnswer,
                            onClick = { viewModel.answer(choice) }
                        )
                    }

                    if (state.selectedAnswer != null) {
                        val message = if (state.isAnswerCorrect == true) {
                            "Benar, mantap!"
                        } else {
                            "Jawaban yang tepat: ${currentQuestion.correctAnswer}"
                        }
                        Text(
                            text = message,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.isAnswerCorrect == true) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = viewModel::nextQuestion,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (state.currentIndex == state.questions.lastIndex) {
                                    "Lihat Skor"
                                } else {
                                    "Soal Berikutnya"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChoiceButton(
    choice: String,
    selectedAnswer: String?,
    correctAnswer: String,
    onClick: () -> Unit
) {
    val isSelected = selectedAnswer == choice
    val isCorrectChoice = choice == correctAnswer
    val containerColor = when {
        selectedAnswer == null -> MaterialTheme.colorScheme.surface
        isCorrectChoice -> MaterialTheme.colorScheme.primaryContainer
        isSelected -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when {
        selectedAnswer == null -> MaterialTheme.colorScheme.onSurface
        isCorrectChoice -> MaterialTheme.colorScheme.onPrimaryContainer
        isSelected -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    OutlinedButton(
        onClick = {
            if (selectedAnswer == null) onClick()
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(
            text = choice,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 6.dp)
        )
    }
}

@Composable
private fun QuizResult(
    score: Int,
    total: Int,
    onRestartClick: () -> Unit
) {
    val percent = if (total == 0) 0 else (score * 100) / total

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Kuis selesai",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "$score / $total",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Skor kamu $percent%. Kata yang belum tepat bisa dilatih lagi nanti.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Button(
                onClick = onRestartClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Ulangi Kuis")
            }
        }
    }
}

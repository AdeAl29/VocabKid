package com.example.vocabkid.presentation.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.vocabkid.audio.rememberSoundEffectPlayer
import com.example.vocabkid.presentation.components.EmptyMessage
import com.example.vocabkid.presentation.components.KidTopBar
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    bottomContentPadding: Dp = 0.dp,
    onBackClick: (() -> Unit)? = null
) {
    val state = viewModel.uiState
    val currentQuestion = state.currentQuestion
    val soundEffects = rememberSoundEffectPlayer()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
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
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 18.dp)
                .padding(bottom = bottomContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            onClick = {
                                if (choice == currentQuestion.correctAnswer) {
                                    soundEffects.playCorrectAnswer()
                                } else {
                                    soundEffects.playWrongAnswer()
                                }
                                viewModel.answer(choice)
                            }
                        )
                    }

                    AnswerFeedback(
                        selectedAnswer = state.selectedAnswer,
                        isCorrect = state.isAnswerCorrect,
                        correctAnswer = currentQuestion.correctAnswer
                    )

                    if (state.selectedAnswer != null) {
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
    val hasAnswer = selectedAnswer != null
    val isWrongSelection = hasAnswer && isSelected && !isCorrectChoice
    val shouldPop = hasAnswer && (isCorrectChoice || isWrongSelection)
    val targetContainerColor = when {
        selectedAnswer == null -> MaterialTheme.colorScheme.surface
        isCorrectChoice -> MaterialTheme.colorScheme.primaryContainer
        isWrongSelection -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
    }
    val targetContentColor = when {
        selectedAnswer == null -> MaterialTheme.colorScheme.onSurface
        isCorrectChoice -> MaterialTheme.colorScheme.onPrimaryContainer
        isWrongSelection -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = when {
        isCorrectChoice && hasAnswer -> MaterialTheme.colorScheme.primary
        isWrongSelection -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(durationMillis = 180),
        label = "choiceContainerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = targetContentColor,
        animationSpec = tween(durationMillis = 180),
        label = "choiceContentColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (shouldPop) 1.02f else 1f,
        animationSpec = tween(durationMillis = 160),
        label = "choicePop"
    )
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(selectedAnswer, choice) {
        if (isWrongSelection) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 360
                    0f at 0
                    -10f at 60
                    10f at 120
                    -7f at 180
                    7f at 240
                    0f at 320
                }
            )
        } else {
            shakeOffset.snapTo(0f)
        }
    }

    OutlinedButton(
        onClick = {
            if (selectedAnswer == null) onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (shouldPop) 2.dp else 1.dp,
            color = borderColor
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = choice,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 30.dp)
            )
            if (hasAnswer && (isCorrectChoice || isWrongSelection)) {
                Icon(
                    imageVector = if (isCorrectChoice) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.Close
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AnswerFeedback(
    selectedAnswer: String?,
    isCorrect: Boolean?,
    correctAnswer: String
) {
    AnimatedVisibility(
        visible = selectedAnswer != null,
        enter = fadeIn(animationSpec = tween(durationMillis = 130)) +
            scaleIn(
                initialScale = 0.94f,
                animationSpec = tween(durationMillis = 180)
            ),
        exit = fadeOut(animationSpec = tween(durationMillis = 100))
    ) {
        val correct = isCorrect == true
        val containerColor = if (correct) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        }
        val contentColor = if (correct) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.error
        }
        val message = if (correct) {
            "Benar! Mantap."
        } else {
            "Hampir! Jawaban yang tepat: $correctAnswer"
        }
        val feedbackProgress = remember { Animatable(0f) }

        LaunchedEffect(selectedAnswer) {
            if (selectedAnswer != null) {
                feedbackProgress.snapTo(0f)
                feedbackProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 560)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val pop = if (feedbackProgress.value < 0.45f) {
                        feedbackProgress.value / 0.45f
                    } else {
                        1f
                    }
                    scaleX = 0.98f + pop * 0.02f
                    scaleY = 0.98f + pop * 0.02f
                },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeedbackBurstIcon(
                    correct = correct,
                    color = contentColor,
                    progress = feedbackProgress.value
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FeedbackBurstIcon(
    correct: Boolean,
    color: Color,
    progress: Float
) {
    Canvas(modifier = Modifier.size(52.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val pulse = progress.coerceIn(0f, 1f)
        val minSide = size.minDimension

        drawCircle(
            color = color.copy(alpha = 0.12f + 0.12f * (1f - pulse)),
            radius = minSide * (0.32f + pulse * 0.18f),
            center = center
        )
        drawCircle(
            color = color.copy(alpha = 0.18f),
            radius = minSide * 0.33f,
            center = center
        )

        if (correct) {
            repeat(7) { index ->
                val angle = Math.toRadians(index * 360.0 / 7.0 - 90.0)
                val distance = minSide * (0.18f + pulse * 0.24f)
                drawCircle(
                    color = Color(0xFFFFC857).copy(alpha = pulse),
                    radius = minSide * 0.035f,
                    center = Offset(
                        x = center.x + cos(angle).toFloat() * distance,
                        y = center.y + sin(angle).toFloat() * distance
                    )
                )
            }
            drawStar(
                center = center,
                outerRadius = minSide * 0.2f,
                innerRadius = minSide * 0.088f,
                color = color
            )
        } else {
            val triangle = Path().apply {
                moveTo(center.x, center.y - minSide * 0.2f)
                lineTo(center.x + minSide * 0.22f, center.y + minSide * 0.18f)
                lineTo(center.x - minSide * 0.22f, center.y + minSide * 0.18f)
                close()
            }
            drawPath(path = triangle, color = color.copy(alpha = 0.18f))
            drawPath(
                path = triangle,
                color = color,
                style = Stroke(width = minSide * 0.045f)
            )
            drawLine(
                color = color,
                start = Offset(center.x, center.y - minSide * 0.08f),
                end = Offset(center.x, center.y + minSide * 0.06f),
                strokeWidth = minSide * 0.045f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = color,
                radius = minSide * 0.025f,
                center = Offset(center.x, center.y + minSide * 0.13f)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    repeat(10) { index ->
        val angle = Math.toRadians(index * 36.0 - 90.0)
        val radius = if (index % 2 == 0) outerRadius else innerRadius
        val point = Offset(
            x = center.x + cos(angle).toFloat() * radius,
            y = center.y + sin(angle).toFloat() * radius
        )
        if (index == 0) {
            path.moveTo(point.x, point.y)
        } else {
            path.lineTo(point.x, point.y)
        }
    }
    path.close()
    drawPath(path = path, color = color)
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

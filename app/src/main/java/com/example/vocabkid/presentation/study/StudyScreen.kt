package com.example.vocabkid.presentation.study

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.example.vocabkid.audio.rememberSoundEffectPlayer
import com.example.vocabkid.presentation.components.CategoryChip
import com.example.vocabkid.presentation.components.CategoryIllustrationImage
import com.example.vocabkid.presentation.components.EnglishTextSpeaker
import com.example.vocabkid.presentation.components.EmptyMessage
import com.example.vocabkid.presentation.components.KidTopBar
import com.example.vocabkid.presentation.components.SpeakerIconButton
import com.example.vocabkid.presentation.components.categoryThemeContainerColor
import com.example.vocabkid.presentation.components.categoryThemeContentColor
import com.example.vocabkid.presentation.components.categoryThemeIcon
import com.example.vocabkid.presentation.components.rememberEnglishTextSpeaker

@Composable
fun StudyScreen(
    viewModel: StudyViewModel,
    bottomContentPadding: Dp = 0.dp,
    onBackClick: (() -> Unit)? = null
) {
    val dueCards by viewModel.dueCards.collectAsStateWithLifecycle()
    val currentCard = dueCards.firstOrNull()
    val soundEffects = rememberSoundEffectPlayer()

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            KidTopBar(
                title = "Flashcard",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 12.dp)
                .padding(bottom = bottomContentPadding),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentCard == null) {
                EmptyMessage(
                    title = "Selesai untuk hari ini",
                    message = "Kamu bisa mencoba kuis ringan atau kembali lagi nanti."
                )
            } else {
                val speaker = rememberEnglishTextSpeaker()

                FlippableFlashcard(
                    category = currentCard.word.category,
                    englishWord = currentCard.word.englishWord,
                    indonesianMeaning = currentCard.word.indonesianMeaning,
                    exampleSentence = currentCard.word.exampleSentence,
                    remainingCards = dueCards.size,
                    showMeaning = viewModel.showMeaning,
                    speaker = speaker
                )

                if (!viewModel.showMeaning) {
                    Button(
                        onClick = {
                            soundEffects.playFlipCard()
                            viewModel.revealMeaning()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Lihat Arti",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ReviewButton(
                                text = "Ulangi",
                                onClick = { viewModel.review(currentCard.word.id, 0) },
                                modifier = Modifier.weight(1f),
                                enabled = !viewModel.isReviewing,
                                containerColor = MaterialTheme.colorScheme.error
                            )
                            ReviewButton(
                                text = "Sulit",
                                onClick = { viewModel.review(currentCard.word.id, 3) },
                                modifier = Modifier.weight(1f),
                                enabled = !viewModel.isReviewing,
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ReviewButton(
                                text = "Cukup",
                                onClick = { viewModel.review(currentCard.word.id, 4) },
                                modifier = Modifier.weight(1f),
                                enabled = !viewModel.isReviewing,
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                            ReviewButton(
                                text = "Mudah",
                                onClick = { viewModel.review(currentCard.word.id, 5) },
                                modifier = Modifier.weight(1f),
                                enabled = !viewModel.isReviewing,
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlippableFlashcard(
    category: String,
    englishWord: String,
    indonesianMeaning: String,
    exampleSentence: String,
    remainingCards: Int,
    showMeaning: Boolean,
    speaker: EnglishTextSpeaker
) {
    val density = LocalDensity.current
    val rotation by animateFloatAsState(
        targetValue = if (showMeaning) 180f else 0f,
        animationSpec = tween(durationMillis = 460),
        label = "flashcardFlip"
    )
    val isBack = rotation > 90f
    val categoryContainerColor = categoryThemeContainerColor(category)
    val categoryContentColor = categoryThemeContentColor(category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 390.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density.density
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = categoryContainerColor,
            contentColor = categoryContentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 390.dp)
                .graphicsLayer {
                    rotationY = if (isBack) 180f else 0f
                }
        ) {
            FlashcardCornerIllustrations(
                category = category,
                contentColor = categoryContentColor
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 390.dp)
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                FlashcardStatusHeader(
                    category = category,
                    remainingCards = remainingCards
                )
                if (isBack) {
                    FlashcardBackFace(
                        category = category,
                        englishWord = englishWord,
                        indonesianMeaning = indonesianMeaning,
                        exampleSentence = exampleSentence,
                        speaker = speaker,
                        contentColor = categoryContentColor
                    )
                } else {
                    FlashcardFrontFace(
                        category = category,
                        englishWord = englishWord,
                        speaker = speaker,
                        contentColor = categoryContentColor
                    )
                }
                Box(modifier = Modifier.heightIn(min = 22.dp))
            }
        }
    }
}

@Composable
private fun BoxScope.FlashcardCornerIllustrations(
    category: String,
    contentColor: Color
) {
    val primaryIcon = categoryThemeIcon(category)
    val secondaryIcon = flashcardAccentIconFor(category)
    val softTint = contentColor.copy(alpha = 0.06f)
    val softerTint = contentColor.copy(alpha = 0.04f)

    Icon(
        imageVector = primaryIcon,
        contentDescription = null,
        tint = softTint,
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 24.dp, y = (-16).dp)
            .graphicsLayer {
                rotationZ = 10f
                scaleX = 1.04f
                scaleY = 1.04f
            }
            .size(118.dp)
    )
    Icon(
        imageVector = secondaryIcon,
        contentDescription = null,
        tint = softerTint,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = (-18).dp, y = 22.dp)
            .graphicsLayer {
                rotationZ = -14f
                scaleX = 0.96f
                scaleY = 0.96f
            }
            .size(104.dp)
    )
    Icon(
        imageVector = primaryIcon,
        contentDescription = null,
        tint = contentColor.copy(alpha = 0.03f),
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .offset(x = (-18).dp, y = (-18).dp)
            .size(52.dp)
    )
}

private fun flashcardAccentIconFor(category: String): ImageVector {
    return when (category.trim().lowercase()) {
        "animal" -> Icons.Default.Pets
        "fruit" -> Icons.Default.LocalFlorist
        "vegetable" -> Icons.Default.Eco
        "food" -> Icons.Default.Restaurant
        "drink" -> Icons.Default.LocalDrink
        "school" -> Icons.Default.School
        "family" -> Icons.Default.FamilyRestroom
        "color" -> Icons.Default.Palette
        "number" -> Icons.Default.LooksOne
        "action" -> Icons.AutoMirrored.Filled.DirectionsRun
        "body" -> Icons.Default.AccessibilityNew
        "clothing" -> Icons.Default.Checkroom
        "home" -> Icons.Default.Home
        "object" -> Icons.Default.AutoAwesome
        "place" -> Icons.Default.Place
        "nature" -> Icons.Default.Eco
        "weather" -> Icons.Default.WbSunny
        "time" -> Icons.Default.AccessTime
        "feeling" -> Icons.Default.SentimentSatisfiedAlt
        "adjective" -> Icons.Default.AutoAwesome
        "transportation" -> Icons.Default.DirectionsCar
        "job" -> Icons.Default.Work
        "position" -> Icons.Default.OpenWith
        "expression" -> Icons.Default.ChatBubble
        else -> Icons.Default.Category
    }
}

@Composable
private fun FlashcardStatusHeader(
    category: String,
    remainingCards: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryChip(category = category)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Text(
                text = "$remainingCards kartu",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FlashcardFrontFace(
    category: String,
    englishWord: String,
    speaker: EnglishTextSpeaker,
    contentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            color = Color.White.copy(alpha = 0.5f)
        ) {
            CategoryIllustrationImage(
                category = category,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop,
                fallbackIconSize = 56.dp
            )
        }
        EnglishWordWithSpeaker(
            englishWord = englishWord,
            speaker = speaker,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
        )
    }
}

@Composable
private fun FlashcardBackFace(
    @Suppress("UNUSED_PARAMETER") category: String,
    englishWord: String,
    indonesianMeaning: String,
    exampleSentence: String,
    speaker: EnglishTextSpeaker,
    contentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EnglishWordWithSpeaker(
            englishWord = englishWord,
            speaker = speaker,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = contentColor,
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = contentColor.copy(alpha = 0.08f)
        ) {
            Text(
                text = indonesianMeaning,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = contentColor
            )
        }
        Text(
            text = "\"$exampleSentence\"",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = contentColor.copy(alpha = 0.8f),
            fontStyle = FontStyle.Italic
        )
    }
}

@Composable
private fun EnglishWordWithSpeaker(
    englishWord: String,
    speaker: EnglishTextSpeaker,
    style: TextStyle,
    fontWeight: FontWeight,
    color: Color
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = englishWord,
            style = style,
            fontWeight = fontWeight,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 44.dp)
        )
        SpeakerIconButton(
            text = englishWord,
            contentDescription = "Dengarkan kata $englishWord",
            speaker = speaker,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
private fun ReviewButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    containerColor: Color
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

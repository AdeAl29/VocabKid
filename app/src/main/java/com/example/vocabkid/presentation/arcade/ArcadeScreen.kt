package com.example.vocabkid.presentation.arcade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vocabkid.audio.rememberSoundEffectPlayer
import com.example.vocabkid.presentation.components.KidTopBar
import kotlin.math.cos
import kotlin.math.sin

// ── Category emoji mapping ──────────────────────────────────────────────────────

private fun categoryEmoji(category: String): String = when (category.lowercase()) {
    "fruit" -> "🍎"
    "food" -> "🍕"
    "drink" -> "🥤"
    "animal" -> "🐾"
    "family" -> "👨‍👩‍👧"
    "school" -> "🏫"
    "transportation" -> "🚗"
    "vegetable" -> "🥦"
    "color" -> "🎨"
    "number" -> "🔢"
    "body" -> "🦶"
    "clothing" -> "👕"
    "weather" -> "⛅"
    "nature" -> "🌿"
    "home" -> "🏠"
    "job" -> "👨‍🔧"
    "feeling" -> "😊"
    "action" -> "🏃"
    "time" -> "⏰"
    "place" -> "📍"
    "adjective" -> "✨"
    "expression" -> "💬"
    "object" -> "📦"
    "position" -> "📐"
    else -> "📚"
}

private fun categoryLabel(category: String): String = when (category.lowercase()) {
    "fruit" -> "Buah"
    "food" -> "Makanan"
    "drink" -> "Minuman"
    "animal" -> "Hewan"
    "family" -> "Keluarga"
    "school" -> "Sekolah"
    "transportation" -> "Kendaraan"
    "vegetable" -> "Sayuran"
    "color" -> "Warna"
    "number" -> "Angka"
    "body" -> "Tubuh"
    "clothing" -> "Pakaian"
    "weather" -> "Cuaca"
    "nature" -> "Alam"
    "home" -> "Rumah"
    "job" -> "Pekerjaan"
    "feeling" -> "Perasaan"
    "action" -> "Aksi"
    "time" -> "Waktu"
    "place" -> "Tempat"
    "adjective" -> "Sifat"
    "expression" -> "Ekspresi"
    "object" -> "Benda"
    "position" -> "Posisi"
    else -> category.replaceFirstChar { it.uppercase() }
}

// ── Main Screen ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcadeScreen(
    viewModel: ArcadeViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val soundEffects = rememberSoundEffectPlayer()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            KidTopBar(
                title = when (uiState.mode) {
                    ArcadeScreenMode.HUB -> "Taman Bermain Kata"
                    ArcadeScreenMode.CATEGORY_SELECT -> "Pilih Kategori"
                    ArcadeScreenMode.GAME -> "Memory Flip"
                    ArcadeScreenMode.RESULT -> "Hasil Permainan"
                },
                onBackClick = {
                    when (uiState.mode) {
                        ArcadeScreenMode.HUB -> onBackClick()
                        ArcadeScreenMode.CATEGORY_SELECT -> viewModel.navigateToHub()
                        ArcadeScreenMode.GAME -> viewModel.navigateToHub()
                        ArcadeScreenMode.RESULT -> viewModel.navigateToHub()
                    }
                }
            )
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = uiState.mode,
            transitionSpec = {
                (fadeIn(tween(220)) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(220)
                )) togetherWith (fadeOut(tween(180)) + scaleOut(
                    targetScale = 0.92f,
                    animationSpec = tween(180)
                ))
            },
            label = "arcadeContent",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { mode ->
            when (mode) {
                ArcadeScreenMode.HUB -> ArcadeHub(
                    onMemoryFlipClick = {
                        soundEffects.playSelect()
                        viewModel.navigateToCategorySelect()
                    }
                )
                ArcadeScreenMode.CATEGORY_SELECT -> CategorySelectScreen(
                    categories = uiState.availableCategories,
                    onStartGame = { category, difficulty ->
                        soundEffects.playSelect()
                        viewModel.selectCategoryAndDifficulty(category, difficulty)
                    }
                )
                ArcadeScreenMode.GAME -> MemoryFlipGameScreen(
                    uiState = uiState,
                    onCardClick = { index ->
                        soundEffects.playFlipCard()
                        viewModel.flipCard(index)
                    }
                )
                ArcadeScreenMode.RESULT -> GameResultScreen(
                    result = uiState.gameResult,
                    onPlayAgain = {
                        soundEffects.playSelect()
                        viewModel.playAgain()
                    },
                    onBackToHub = {
                        soundEffects.playSelect()
                        viewModel.navigateToHub()
                    }
                )
            }
        }
    }
}

// ── Hub Screen ──────────────────────────────────────────────────────────────────

@Composable
private fun ArcadeHub(onMemoryFlipClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "arcadeHub")
    val bob by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "bob"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "🕹️ Pilih Permainan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Belajar kosakata sambil bermain! Kumpulkan bintang di setiap permainan.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        // Memory Flip Game Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onMemoryFlipClick),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6C63FF),
                                Color(0xFF3F51B5),
                                Color(0xFF1A237E)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Animated card icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .graphicsLayer { translationY = bob }
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🃏", fontSize = 36.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Memory Flip",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Cocokkan Kartu Kosakata",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Balik kartu dan temukan pasangan kata Bahasa Inggris dengan artinya! Latih ingatan dan kosakatamu.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DifficultyChip("Mudah", Color(0xFF4CAF50))
                        DifficultyChip("Sedang", Color(0xFFFF9800))
                        DifficultyChip("Seru!", Color(0xFFF44336))
                    }
                }
            }
        }

        // Coming Soon placeholder
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔮", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Game baru segera hadir!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Word Builder, Catch the Word, dan lainnya…",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(label: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.25f),
        contentColor = Color.White
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ── Category Select Screen ──────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategorySelectScreen(
    categories: List<String>,
    onStartGame: (String?, GameDifficulty) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDifficulty by remember { mutableStateOf(GameDifficulty.EASY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Difficulty section
        Text(
            text = "📊 Pilih Tingkat Kesulitan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GameDifficulty.entries.forEach { difficulty ->
                val isSelected = selectedDifficulty == difficulty
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        when (difficulty) {
                            GameDifficulty.EASY -> Color(0xFF4CAF50)
                            GameDifficulty.MEDIUM -> Color(0xFFFF9800)
                            GameDifficulty.HARD -> Color(0xFFF44336)
                        }
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    animationSpec = tween(200),
                    label = "diffColor"
                )
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedDifficulty = difficulty },
                    shape = RoundedCornerShape(16.dp),
                    color = bgColor,
                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    shadowElevation = if (isSelected) 6.dp else 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = when (difficulty) {
                                GameDifficulty.EASY -> "😊"
                                GameDifficulty.MEDIUM -> "🤔"
                                GameDifficulty.HARD -> "🔥"
                            },
                            fontSize = 28.sp
                        )
                        Text(
                            text = difficulty.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${difficulty.pairs} pasang",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Category section
        Text(
            text = "📂 Pilih Kategori",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )

        // "Mix" option
        val isMixSelected = selectedCategory == null
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedCategory = null },
            shape = RoundedCornerShape(16.dp),
            color = if (isMixSelected) Color(0xFF6C63FF) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isMixSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            shadowElevation = if (isMixSelected) 6.dp else 1.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🎲", fontSize = 28.sp)
                Column {
                    Text(
                        text = "Campuran",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Kata acak dari semua kategori",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isMixSelected) Color.White.copy(alpha = 0.78f)
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF6C63FF)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    animationSpec = tween(200),
                    label = "catColor"
                )
                Surface(
                    modifier = Modifier.clickable { selectedCategory = category },
                    shape = RoundedCornerShape(14.dp),
                    color = bgColor,
                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    shadowElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = categoryEmoji(category), fontSize = 18.sp)
                        Text(
                            text = categoryLabel(category),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Start button
        Button(
            onClick = { onStartGame(selectedCategory, selectedDifficulty) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = "🎮  Mulai Main!",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

// ── Memory Flip Game Screen ─────────────────────────────────────────────────────

@Composable
private fun MemoryFlipGameScreen(
    uiState: ArcadeUiState,
    onCardClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Stats bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = formatTime(uiState.elapsedSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "Langkah: ${uiState.moves}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${uiState.matchedPairs}/${uiState.totalPairs} ✅",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }

        // Card grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(uiState.difficulty.columns),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(uiState.cards) { index, card ->
                MemoryCardItem(
                    card = card,
                    onClick = { onCardClick(index) }
                )
            }
        }
    }
}

@Composable
private fun MemoryCardItem(
    card: MemoryCard,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val matchScale = remember { Animatable(1f) }

    LaunchedEffect(card.isFlipped) {
        if (card.isFlipped) {
            scale.animateTo(0.92f, tween(80))
            scale.animateTo(1f, tween(140, easing = EaseOutBack))
        }
    }

    LaunchedEffect(card.isMatched) {
        if (card.isMatched) {
            matchScale.animateTo(1.12f, tween(120, easing = EaseOutBack))
            matchScale.animateTo(1f, tween(200))
        }
    }

    val cardColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> Color(0xFF4CAF50).copy(alpha = 0.18f)
            card.isFlipped -> Color(0xFF6C63FF).copy(alpha = 0.12f)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(200),
        label = "cardBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            card.isMatched -> Color(0xFF4CAF50)
            card.isFlipped -> Color(0xFF6C63FF)
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        },
        animationSpec = tween(200),
        label = "cardBorder"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .graphicsLayer {
                scaleX = scale.value * matchScale.value
                scaleY = scale.value * matchScale.value
            }
            .clickable(enabled = !card.isFlipped && !card.isMatched, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isFlipped || card.isMatched) 6.dp else 2.dp
        ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(
            width = if (card.isMatched) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (card.isFlipped || card.isMatched) {
                // Show content
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(6.dp)
                ) {
                    if (card.isImage) {
                        // Show emoji + Indonesian meaning
                        Text(
                            text = categoryEmoji(card.category),
                            fontSize = 24.sp
                        )
                        Text(
                            text = card.displayText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        // Show English word
                        Text(
                            text = "🇬🇧",
                            fontSize = 20.sp
                        )
                        Text(
                            text = card.displayText,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = Color(0xFF6C63FF)
                        )
                    }
                    if (card.isMatched) {
                        Text(text = "✅", fontSize = 14.sp)
                    }
                }
            } else {
                // Card back — hidden
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6C63FF),
                                    Color(0xFF3F51B5)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "❓", fontSize = 32.sp)
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}

// ── Game Result Screen ──────────────────────────────────────────────────────────

@Composable
private fun GameResultScreen(
    result: GameResult?,
    onPlayAgain: () -> Unit,
    onBackToHub: () -> Unit
) {
    if (result == null) return

    val titleScale = remember { Animatable(0f) }
    val starsScale = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        titleScale.animateTo(1f, tween(500, easing = EaseOutBounce))
        starsScale.animateTo(1f, tween(600, easing = EaseOutBack))
        contentAlpha.animateTo(1f, tween(400))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "resultParticles")
    val particleSpin by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "particleSpin"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Trophy
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    scaleX = titleScale.value
                    scaleY = titleScale.value
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Confetti ring
                val cx = size.width / 2
                val cy = size.height / 2
                val radius = size.minDimension / 2 * 1.4f
                val confettiColors = listOf(
                    Color(0xFFFF6B8B), Color(0xFFFFC928),
                    Color(0xFF6C63FF), Color(0xFF4CAF50),
                    Color(0xFF00BCD4), Color(0xFFFF9800)
                )
                for (i in 0 until 12) {
                    val angle = Math.toRadians((particleSpin + i * 30.0).toDouble())
                    val px = cx + cos(angle).toFloat() * radius
                    val py = cy + sin(angle).toFloat() * radius
                    drawCircle(
                        color = confettiColors[i % confettiColors.size].copy(alpha = 0.7f),
                        radius = if (i % 2 == 0) 5.dp.toPx() else 3.5f.dp.toPx(),
                        center = Offset(px, py)
                    )
                }
            }
            Text(text = "🏆", fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (result.stars) {
                3 -> "Luar Biasa! 🌟"
                2 -> "Hebat! 👏"
                else -> "Bagus! 👍"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.graphicsLayer {
                scaleX = titleScale.value
                scaleY = titleScale.value
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.graphicsLayer {
                scaleX = starsScale.value
                scaleY = starsScale.value
            }
        ) {
            repeat(3) { index ->
                Icon(
                    imageVector = if (index < result.stars) Icons.Filled.Star
                    else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (index < result.stars) Color(0xFFFFC928) else Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = contentAlpha.value },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatRow("🃏 Pasangan", "${result.totalPairs} pasang")
                StatRow("👆 Langkah", "${result.moves} langkah")
                StatRow("⏱️ Waktu", formatTime(result.timeSeconds))
                StatRow(
                    "📊 Efisiensi",
                    "${"%.0f".format(result.totalPairs.toFloat() / result.moves * 100)}%"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .graphicsLayer { alpha = contentAlpha.value },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Replay,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Main Lagi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackToHub,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .graphicsLayer { alpha = contentAlpha.value },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Kembali ke Arcade",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

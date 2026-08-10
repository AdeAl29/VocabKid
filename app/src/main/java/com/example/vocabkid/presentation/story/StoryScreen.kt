package com.example.vocabkid.presentation.story

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─── Color Palette ───────────────────────────────────────────────────────────

private val CorrectGreen  = Color(0xFF00C896)
private val WrongRed      = Color(0xFFFF5252)
private val StarGold      = Color(0xFFFFD700)
private val StarGoldDim   = Color(0xFFFFE082)

// ─── Root Screen ─────────────────────────────────────────────────────────────

@Composable
fun StoryScreen(
    viewModel: StoryViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    AnimatedContent(
        targetState = state.phase,
        transitionSpec = {
            (fadeIn(tween(400)) + scaleIn(tween(400), initialScale = 0.96f))
                .togetherWith(fadeOut(tween(250)) + scaleOut(tween(250), targetScale = 1.04f))
        },
        label = "storyPhaseTransition"
    ) { phase ->
        when (phase) {
            StoryPhase.CHAPTER_LIST -> ChapterListScreen(
                state = state,
                onChapterClick = { viewModel.startChapter(it) },
                onBackClick = onBackClick
            )
            StoryPhase.READING -> StoryPlayScreen(
                state = state,
                onNarrationDone = { viewModel.onNarrationDone() },
                onAdvance = { viewModel.advanceScene() },
                onBackClick = { viewModel.goToChapterList() }
            )
            StoryPhase.QUIZ -> QuizPlayScreen(
                state = state,
                onSelectOption = { viewModel.selectOption(it) },
                onAdvance = { viewModel.advanceScene() },
                onBackClick = { viewModel.goToChapterList() }
            )
            StoryPhase.SUMMARY -> SummaryScreen(
                state = state,
                onPlayAgain = { viewModel.restartChapter() },
                onBackToList = { viewModel.goToChapterList() }
            )
        }
    }
}

// ─── Chapter List ─────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChapterListScreen(
    state: StoryUiState,
    onChapterClick: (StoryChapter) -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Animated background with floating particles
        ChapterListBackground()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                // Epic hero header with canvas illustration
                ChapterListHero(onBackClick = onBackClick)
            }

            item {
                // Progress summary strip
                ChapterProgressStrip(
                    completed = state.completedChapterIds.size,
                    total = state.chapters.size
                )
                Spacer(Modifier.height(8.dp))
            }

            itemsIndexed(state.chapters) { index, chapter ->
                val isCompleted = chapter.id in state.completedChapterIds
                ChapterCard(
                    chapter = chapter,
                    index = index,
                    isCompleted = isCompleted,
                    isLocked = false,
                    onClick = { onChapterClick(chapter) }
                )
            }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun ChapterListBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bgAnim")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "bgT"
    )

    // Stable random particles
    val particles = remember {
        List(30) {
            Triple(
                Random.nextFloat(), // x
                Random.nextFloat(), // y
                Random.nextFloat()  // phase
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Soft gradient background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1A0533), Color(0xFF0D1B4A), Color(0xFF0A2E3D)),
                startY = 0f, endY = h
            )
        )

        // Animated aurora blobs
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x557C3AED), Color.Transparent),
                center = Offset(w * 0.2f, h * 0.15f),
                radius = w * 0.5f
            ),
            radius = w * 0.5f,
            center = Offset(w * 0.2f + w * 0.05f * sin(t.toDouble()).toFloat(), h * 0.15f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x44DB2777), Color.Transparent),
                center = Offset(w * 0.8f, h * 0.4f),
                radius = w * 0.4f
            ),
            radius = w * 0.4f,
            center = Offset(w * 0.8f + w * 0.04f * cos(t.toDouble()).toFloat(), h * 0.4f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x3306B6D4), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.7f),
                radius = w * 0.45f
            ),
            radius = w * 0.45f,
            center = Offset(w * 0.5f, h * 0.7f + h * 0.03f * sin((t + 1f).toDouble()).toFloat())
        )

        // Floating star particles
        particles.forEachIndexed { i, (px, py, phase) ->
            val animPhase = (t + phase * 6f) % (2 * PI).toFloat()
            val alpha = (sin(animPhase.toDouble()).toFloat() * 0.5f + 0.5f) * 0.7f
            val yOffset = sin((t * 0.5f + phase * 3f).toDouble()).toFloat() * h * 0.02f
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = if (i % 4 == 0) 2.5f else 1.5f,
                center = Offset(w * px, h * py + yOffset)
            )
        }
    }
}

@Composable
private fun ChapterListHero(onBackClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroAnim")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "heroT"
    )
    val bookBounce by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "bookBounce"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sky gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A0533), Color(0xFF3D0B6E), Color(0xFF6B1A9A)),
                    startY = 0f, endY = h
                )
            )

            // Stars
            listOf(
                Offset(w * 0.08f, h * 0.12f), Offset(w * 0.22f, h * 0.08f),
                Offset(w * 0.45f, h * 0.15f), Offset(w * 0.68f, h * 0.06f),
                Offset(w * 0.82f, h * 0.18f), Offset(w * 0.91f, h * 0.09f),
                Offset(w * 0.35f, h * 0.05f), Offset(w * 0.55f, h * 0.11f)
            ).forEachIndexed { i, center ->
                val starAlpha = (sin((t + i * 0.8f).toDouble()).toFloat() * 0.4f + 0.6f)
                drawCircle(Color.White.copy(alpha = starAlpha), 2.5f, center)
                // Twinkle cross
                if (i % 3 == 0) {
                    val crossAlpha = (sin((t * 2f + i).toDouble()).toFloat() * 0.3f + 0.3f)
                    drawLine(Color.White.copy(alpha = crossAlpha), center - Offset(5f, 0f), center + Offset(5f, 0f), 1f)
                    drawLine(Color.White.copy(alpha = crossAlpha), center - Offset(0f, 5f), center + Offset(0f, 5f), 1f)
                }
            }

            // Moon
            val moonCenter = Offset(w * 0.82f, h * 0.28f)
            drawCircle(Color(0xFFFFF1B8).copy(alpha = 0.18f), 48f, moonCenter)
            drawCircle(Color(0xFFFFF1B8), 32f, moonCenter)
            drawCircle(Color(0xFF6B1A9A), 29f, moonCenter + Offset(12f, -8f))

            // Floating clouds / magic wisps
            listOf(
                Offset(w * 0.15f, h * 0.42f),
                Offset(w * 0.65f, h * 0.35f)
            ).forEachIndexed { i, center ->
                val driftX = sin((t + i * 1.5f).toDouble()).toFloat() * 12f
                val driftY = cos((t * 0.7f + i).toDouble()).toFloat() * 5f
                drawWispCloud(center + Offset(driftX, driftY), Color(0x88C084FC))
            }

            // Giant glowing book illustration
            val bookCenter = Offset(w * 0.5f, h * 0.62f + bookBounce * 6f)
            val bookW = w * 0.55f
            val bookH = bookW * 0.65f
            // Book glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x88A855F7), Color.Transparent),
                    center = bookCenter, radius = bookW * 0.7f
                ),
                radius = bookW * 0.7f, center = bookCenter
            )
            // Left page
            drawRoundRect(
                color = Color(0xFFFFF8E7),
                topLeft = Offset(bookCenter.x - bookW / 2, bookCenter.y - bookH / 2),
                size = Size(bookW / 2, bookH),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Right page
            drawRoundRect(
                color = Color(0xFFFFF3CC),
                topLeft = Offset(bookCenter.x, bookCenter.y - bookH / 2),
                size = Size(bookW / 2, bookH),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Spine
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFA855F7), Color(0xFF7C3AED)),
                    startY = bookCenter.y - bookH / 2, endY = bookCenter.y + bookH / 2
                ),
                topLeft = Offset(bookCenter.x - 6f, bookCenter.y - bookH / 2),
                size = Size(12f, bookH)
            )
            // Text lines on left page
            listOf(0.3f, 0.45f, 0.6f, 0.75f).forEach { frac ->
                drawLine(
                    color = Color(0xFFB0A090).copy(alpha = 0.6f),
                    start = Offset(bookCenter.x - bookW * 0.42f, bookCenter.y - bookH / 2 + bookH * frac),
                    end = Offset(bookCenter.x - bookW * 0.06f, bookCenter.y - bookH / 2 + bookH * frac),
                    strokeWidth = 2.5f, cap = StrokeCap.Round
                )
            }
            // Text lines on right page
            listOf(0.3f, 0.45f, 0.6f, 0.75f).forEach { frac ->
                drawLine(
                    color = Color(0xFFB0A090).copy(alpha = 0.6f),
                    start = Offset(bookCenter.x + bookW * 0.06f, bookCenter.y - bookH / 2 + bookH * frac),
                    end = Offset(bookCenter.x + bookW * 0.42f, bookCenter.y - bookH / 2 + bookH * frac),
                    strokeWidth = 2.5f, cap = StrokeCap.Round
                )
            }
            // Bookmark ribbon
            drawRect(
                color = Color(0xFFEC4899),
                topLeft = Offset(bookCenter.x + bookW * 0.3f, bookCenter.y - bookH / 2),
                size = Size(10f, bookH * 0.55f)
            )

            // Floating magic sparkles around book
            listOf(
                Offset(bookCenter.x - bookW * 0.55f, bookCenter.y - bookH * 0.3f),
                Offset(bookCenter.x + bookW * 0.55f, bookCenter.y - bookH * 0.1f),
                Offset(bookCenter.x - bookW * 0.1f, bookCenter.y - bookH * 0.7f),
                Offset(bookCenter.x + bookW * 0.2f, bookCenter.y - bookH * 0.75f)
            ).forEachIndexed { i, sparkPos ->
                val sparkAlpha = (sin((t * 2f + i * 1.2f).toDouble()).toFloat() * 0.5f + 0.5f)
                val sparkScale = 0.6f + sparkAlpha * 0.8f
                drawSparkle(sparkPos + Offset(
                    sin((t + i).toDouble()).toFloat() * 8f,
                    cos((t * 0.8f + i).toDouble()).toFloat() * 6f
                ), sparkScale * 8f, Color(0xFFFFD700).copy(alpha = sparkAlpha * 0.9f))
            }
        }

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(12.dp)
                .size(44.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
        }

        // Title overlay at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "✨ Adventure Stories ✨",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Belajar kata-kata baru lewat cerita seru!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ChapterProgressStrip(completed: Int, total: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0x22FFFFFF)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🗺️", fontSize = 22.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Progres Petualangan",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(
                    "$completed dari $total chapter selesai",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            // Mini progress bar
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                val progress by animateFloatAsState(
                    targetValue = if (total > 0) completed.toFloat() / total else 0f,
                    animationSpec = tween(800, easing = EaseOutBack),
                    label = "stripProgress"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFFA855F7), Color(0xFFEC4899)))
                        )
                )
            }
        }
    }
}

@Composable
private fun ChapterCard(
    chapter: StoryChapter,
    index: Int,
    isCompleted: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay((index * 150L))
        visible = true
    }

    var pressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400, easing = EaseOutBack)) { it / 2 }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .scale(cardScale)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                pressed = true
                                tryAwaitRelease()
                                pressed = false
                            },
                            onTap = { if (!isLocked) onClick() }
                        )
                    },
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Chapter Canvas illustration background
                    ChapterCardIllustration(
                        chapterId = chapter.id,
                        gradientStart = Color(chapter.gradientStart),
                        gradientEnd = Color(chapter.gradientEnd),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )

                    // Bottom gradient overlay for text readability
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(chapter.gradientStart).copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Completed badge
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color(0xFF00C896), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Text("Selesai", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            "Chapter ${chapter.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f),
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(chapter.emoji, fontSize = 24.sp)
                            Column {
                                Text(
                                    chapter.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    chapter.titleId,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoBadge("📖 ${chapter.scenes.size} Scene")
                            InfoBadge("💬 ${chapter.vocabWords.size} Kata")
                        }
                    }

                    // Play arrow
                    if (!isLocked) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Main",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Black.copy(alpha = 0.3f)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// ─── Chapter Canvas Illustrations ────────────────────────────────────────────

@Composable
private fun ChapterCardIllustration(
    chapterId: Int,
    gradientStart: Color,
    gradientEnd: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chapterIllustAnim")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(6000 + chapterId * 1000, easing = LinearEasing)),
        label = "chapterT"
    )

    Canvas(modifier = modifier) {
        // Base gradient
        drawRect(brush = Brush.linearGradient(listOf(gradientStart, gradientEnd)))

        when (chapterId) {
            1 -> drawKittenForestScene(t)
            2 -> drawRiverCrossingScene(t)
            3 -> drawSecretGardenScene(t)
            else -> drawGenericAdventureScene(t, gradientStart, gradientEnd)
        }
    }
}

private fun DrawScope.drawKittenForestScene(t: Float) {
    val w = size.width
    val h = size.height

    // Sky with sunlight
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFF9A56), Color(0xFFFFD89B)),
            startY = 0f, endY = h
        )
    )

    // Sun glow
    val sunCenter = Offset(w * 0.82f, h * 0.22f)
    drawCircle(Color(0x44FFEE00), 55f, sunCenter)
    drawCircle(Color(0xFFFFD700), 32f, sunCenter)

    // Light rays
    repeat(6) { i ->
        val angle = (i * 60f + t * 10f) * PI.toFloat() / 180f
        drawLine(
            color = Color(0x33FFFF99),
            start = sunCenter,
            end = sunCenter + Offset(cos(angle.toDouble()).toFloat() * 80f, sin(angle.toDouble()).toFloat() * 80f),
            strokeWidth = 3f, cap = StrokeCap.Round
        )
    }

    // Hills
    drawPath(path = Path().apply {
        moveTo(0f, h * 0.65f)
        cubicTo(w * 0.25f, h * 0.52f, w * 0.5f, h * 0.6f, w * 0.75f, h * 0.54f)
        cubicTo(w * 0.88f, h * 0.5f, w, h * 0.58f, w, h * 0.65f)
        lineTo(w, h); lineTo(0f, h); close()
    }, color = Color(0xFF4CAF50))
    drawPath(path = Path().apply {
        moveTo(0f, h * 0.78f)
        cubicTo(w * 0.3f, h * 0.7f, w * 0.6f, h * 0.76f, w, h * 0.72f)
        lineTo(w, h); lineTo(0f, h); close()
    }, color = Color(0xFF388E3C))

    // Trees
    listOf(0.12f, 0.28f, 0.72f, 0.88f).forEachIndexed { i, xFrac ->
        val trunkH = h * (0.22f + (i % 2) * 0.05f)
        val treeX = w * xFrac
        val treeBaseY = h * 0.72f - trunkH * 0.15f

        // Trunk
        drawRect(
            color = Color(0xFF795548),
            topLeft = Offset(treeX - 5f, treeBaseY - trunkH * 0.4f),
            size = Size(10f, trunkH * 0.4f)
        )
        // Canopy layers
        listOf(0f, 0.12f, 0.22f).forEach { layerOffset ->
            val canopyY = treeBaseY - trunkH * 0.45f - layerOffset * h
            val canopyR = (trunkH * (0.35f - layerOffset * 0.5f)).coerceAtLeast(20f)
            drawCircle(
                color = if (i % 2 == 0) Color(0xFF66BB6A) else Color(0xFF43A047),
                radius = canopyR,
                center = Offset(treeX, canopyY)
            )
        }
    }

    // Cute kitten silhouette
    val kitX = w * 0.5f
    val kitY = h * 0.68f
    val kitSize = h * 0.14f
    // Body
    drawCircle(Color(0xFFFFA07A), kitSize * 0.55f, Offset(kitX, kitY))
    // Head
    drawCircle(Color(0xFFFFA07A), kitSize * 0.42f, Offset(kitX, kitY - kitSize * 0.65f))
    // Ears
    drawPath(path = Path().apply {
        moveTo(kitX - kitSize * 0.22f, kitY - kitSize * 0.85f)
        lineTo(kitX - kitSize * 0.35f, kitY - kitSize * 1.15f)
        lineTo(kitX - kitSize * 0.08f, kitY - kitSize * 0.88f)
        close()
    }, color = Color(0xFFFFA07A))
    drawPath(path = Path().apply {
        moveTo(kitX + kitSize * 0.22f, kitY - kitSize * 0.85f)
        lineTo(kitX + kitSize * 0.35f, kitY - kitSize * 1.15f)
        lineTo(kitX + kitSize * 0.08f, kitY - kitSize * 0.88f)
        close()
    }, color = Color(0xFFFFA07A))
    // Eyes
    drawCircle(Color(0xFF263238), kitSize * 0.07f, Offset(kitX - kitSize * 0.15f, kitY - kitSize * 0.68f))
    drawCircle(Color(0xFF263238), kitSize * 0.07f, Offset(kitX + kitSize * 0.15f, kitY - kitSize * 0.68f))
    // Nose
    drawCircle(Color(0xFFFF69B4), kitSize * 0.04f, Offset(kitX, kitY - kitSize * 0.57f))
    // Tail
    drawPath(path = Path().apply {
        moveTo(kitX + kitSize * 0.45f, kitY + kitSize * 0.1f)
        cubicTo(kitX + kitSize * 0.9f, kitY + kitSize * 0.3f,
            kitX + kitSize * 0.9f, kitY - kitSize * 0.4f + sin(t.toDouble()).toFloat() * 8f,
            kitX + kitSize * 0.6f, kitY - kitSize * 0.35f + sin(t.toDouble()).toFloat() * 6f)
    }, color = Color(0xFFFFA07A), style = Stroke(width = 6f, cap = StrokeCap.Round))

    // Floating leaves
    listOf(0.2f, 0.55f, 0.8f).forEachIndexed { i, xFrac ->
        val leafX = w * xFrac + sin((t + i * 2f).toDouble()).toFloat() * 15f
        val leafY = h * (0.35f + (i * 0.08f)) + cos((t * 0.7f + i).toDouble()).toFloat() * 10f
        val leafAngle = sin((t + i).toDouble()).toFloat() * 30f
        rotate(leafAngle, Offset(leafX, leafY)) {
            drawOval(Color(0xFF81C784).copy(alpha = 0.8f), Offset(leafX - 7f, leafY - 4f), Size(14f, 8f))
        }
    }
}

private fun DrawScope.drawRiverCrossingScene(t: Float) {
    val w = size.width
    val h = size.height

    // Sky
    drawRect(brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF87CEEB), Color(0xFF4AB8E8), Color(0xFF2196F3)),
        startY = 0f, endY = h * 0.55f
    ))

    // Green banks
    drawRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, h * 0.22f), size = Size(w * 0.28f, h * 0.78f))
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(w * 0.72f, h * 0.22f), size = Size(w * 0.28f, h * 0.78f))

    // River
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0)),
            startY = h * 0.25f, endY = h
        ),
        topLeft = Offset(w * 0.28f, h * 0.25f),
        size = Size(w * 0.44f, h * 0.75f)
    )

    // River ripples
    repeat(5) { i ->
        val rippleY = h * (0.38f + i * 0.1f) + sin((t + i * 0.8f).toDouble()).toFloat() * 4f
        drawLine(
            color = Color.White.copy(alpha = 0.35f),
            start = Offset(w * 0.3f, rippleY),
            end = Offset(w * 0.7f, rippleY + 4f),
            strokeWidth = 2.5f, cap = StrokeCap.Round
        )
    }

    // Sun
    val sunY = h * 0.12f
    drawCircle(Color(0x44FFEE00), 45f, Offset(w * 0.78f, sunY))
    drawCircle(Color(0xFFFFD700), 28f, Offset(w * 0.78f, sunY))

    // Clouds
    val cloudDrift = sin(t.toDouble()).toFloat() * 10f
    drawWispCloud(Offset(w * 0.18f + cloudDrift, h * 0.18f), Color.White.copy(alpha = 0.85f))
    drawWispCloud(Offset(w * 0.62f - cloudDrift * 0.5f, h * 0.12f), Color.White.copy(alpha = 0.7f))

    // Rope bridge
    val ropeY = h * 0.42f
    drawLine(
        color = Color(0xFF795548),
        start = Offset(w * 0.28f, ropeY),
        end = Offset(w * 0.72f, ropeY + 8f),
        strokeWidth = 3f, cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF795548),
        start = Offset(w * 0.28f, ropeY + 18f),
        end = Offset(w * 0.72f, ropeY + 26f),
        strokeWidth = 3f, cap = StrokeCap.Round
    )
    // Planks
    repeat(6) { i ->
        val plankX = w * 0.28f + (w * 0.44f / 7f) * (i + 0.5f)
        drawRect(
            color = Color(0xFFA1887F),
            topLeft = Offset(plankX - 8f, ropeY + 1f),
            size = Size(16f, 22f)
        )
    }

    // Small person silhouette crossing
    val personX = w * 0.5f + cos((t * 0.3f).toDouble()).toFloat() * 5f
    drawCircle(Color(0xFF37474F), 9f, Offset(personX, ropeY + 5f))
    drawLine(Color(0xFF37474F), Offset(personX, ropeY + 14f), Offset(personX, ropeY + 28f), 5f)
    drawLine(Color(0xFF37474F), Offset(personX - 8f, ropeY + 18f), Offset(personX + 8f, ropeY + 22f), 4f)

    // Treasure cave glimpse on right bank
    drawOval(Color(0xFF1A237E).copy(alpha = 0.6f), Offset(w * 0.76f, h * 0.35f), Size(w * 0.18f, h * 0.2f))
    // Sparkles
    listOf(Offset(w * 0.82f, h * 0.4f), Offset(w * 0.79f, h * 0.48f)).forEachIndexed { i, pos ->
        val alpha = (sin((t * 3f + i).toDouble()).toFloat() * 0.5f + 0.5f)
        drawSparkle(pos, 6f, Color(0xFFFFD700).copy(alpha = alpha))
    }
}

private fun DrawScope.drawSecretGardenScene(t: Float) {
    val w = size.width
    val h = size.height

    // Background gradient - warm purple
    drawRect(brush = Brush.verticalGradient(
        colors = listOf(Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFFFF6090)),
        startY = 0f, endY = h
    ))

    // Distant misty hills
    drawPath(path = Path().apply {
        moveTo(0f, h * 0.55f)
        cubicTo(w * 0.2f, h * 0.42f, w * 0.45f, h * 0.52f, w * 0.65f, h * 0.44f)
        cubicTo(w * 0.82f, h * 0.38f, w, h * 0.48f, w, h * 0.55f)
        lineTo(w, h); lineTo(0f, h); close()
    }, color = Color(0xFF7B1FA2).copy(alpha = 0.6f))

    // Green grass
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(0f, h * 0.7f), size = Size(w, h * 0.3f))

    // Old stone wall
    val wallY = h * 0.35f
    drawRect(color = Color(0xFF78909C), topLeft = Offset(0f, wallY), size = Size(w, h * 0.42f))
    // Brick pattern
    repeat(5) { row ->
        val isEven = row % 2 == 0
        repeat(8) { col ->
            val brickX = w / 8f * col + if (isEven) 0f else w / 16f
            val brickY = wallY + row * 14f
            drawRect(
                color = Color(0xFF546E7A),
                topLeft = Offset(brickX + 1f, brickY + 1f),
                size = Size(w / 8f - 2f, 12f)
            )
        }
    }

    // Wooden secret door
    val doorX = w * 0.38f
    val doorY = h * 0.34f
    val doorW = w * 0.24f
    val doorH = h * 0.38f
    // Door arch
    drawPath(path = Path().apply {
        moveTo(doorX, doorY + doorH * 0.45f)
        lineTo(doorX, doorY)
        cubicTo(doorX, doorY - doorH * 0.2f, doorX + doorW, doorY - doorH * 0.2f, doorX + doorW, doorY)
        lineTo(doorX + doorW, doorY + doorH * 0.45f)
        close()
    }, color = Color(0xFF8D6E63))
    drawRect(color = Color(0xFF8D6E63), topLeft = Offset(doorX, doorY + doorH * 0.35f), size = Size(doorW, doorH * 0.65f))
    // Door planks
    repeat(4) { i ->
        drawLine(color = Color(0xFF5D4037), Offset(doorX + 3f, doorY + doorH * 0.05f + i * doorH * 0.2f), Offset(doorX + doorW - 3f, doorY + doorH * 0.05f + i * doorH * 0.2f), 2f)
    }
    // Door handle
    drawCircle(Color(0xFFFFD700), 5f, Offset(doorX + doorW * 0.75f, doorY + doorH * 0.58f))

    // Colorful flowers along wall base
    val flowerColors = listOf(Color(0xFFFF6090), Color(0xFFFFD700), Color(0xFF9C27B0), Color(0xFFFF7043), Color(0xFF26C6DA))
    listOf(0.08f, 0.18f, 0.32f, 0.62f, 0.74f, 0.86f, 0.94f).forEachIndexed { idx, xFrac ->
        val fColor = flowerColors[idx % flowerColors.size]
        val fX = w * xFrac
        val fY = h * 0.67f + sin((t + idx * 0.7f).toDouble()).toFloat() * 3f
        // Stem
        drawLine(Color(0xFF388E3C), Offset(fX, h * 0.74f), Offset(fX, fY), 3f, cap = StrokeCap.Round)
        // Petals
        repeat(5) { p ->
            val angle = p * 72f * PI.toFloat() / 180f
            drawCircle(fColor.copy(alpha = 0.9f), 7f, Offset(fX + cos(angle.toDouble()).toFloat() * 9f, fY + sin(angle.toDouble()).toFloat() * 9f))
        }
        drawCircle(Color.White, 5f, Offset(fX, fY))
    }

    // Bee
    val beeX = w * 0.65f + cos((t * 1.5f).toDouble()).toFloat() * 30f
    val beeY = h * 0.5f + sin((t * 1.2f).toDouble()).toFloat() * 20f
    drawOval(Color(0xFFFFD700), Offset(beeX - 8f, beeY - 5f), Size(16f, 10f))
    drawLine(Color(0xFF263238), Offset(beeX - 5f, beeY - 2f), Offset(beeX + 5f, beeY - 2f), 2f)
    drawLine(Color(0xFF263238), Offset(beeX - 5f, beeY + 2f), Offset(beeX + 5f, beeY + 2f), 2f)
    // Bee wings
    drawOval(Color.White.copy(alpha = 0.7f), Offset(beeX - 4f, beeY - 10f), Size(12f, 8f))
    drawOval(Color.White.copy(alpha = 0.7f), Offset(beeX - 4f, beeY + 2f), Size(12f, 8f))

    // Magic glowing sign on door
    val signAlpha = (sin((t * 2f).toDouble()).toFloat() * 0.3f + 0.7f)
    drawRoundRect(color = Color(0xFFA5D6A7).copy(alpha = signAlpha * 0.9f),
        topLeft = Offset(doorX + doorW * 0.05f, doorY + doorH * 0.1f),
        size = Size(doorW * 0.9f, doorH * 0.18f),
        cornerRadius = CornerRadius(4f, 4f)
    )
}

private fun DrawScope.drawGenericAdventureScene(t: Float, gradStart: Color, gradEnd: Color) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(gradStart, gradEnd)))
    drawWispCloud(Offset(w * 0.25f + sin(t.toDouble()).toFloat() * 10f, h * 0.25f), Color.White.copy(0.7f))
    drawCircle(Color(0x44FFEE00), 50f, Offset(w * 0.8f, h * 0.2f))
    drawCircle(Color(0xFFFFD700), 30f, Offset(w * 0.8f, h * 0.2f))
    drawPath(path = Path().apply {
        moveTo(0f, h * 0.7f); cubicTo(w * 0.3f, h * 0.6f, w * 0.7f, h * 0.65f, w, h * 0.58f)
        lineTo(w, h); lineTo(0f, h); close()
    }, color = Color(0xFF4CAF50))
}

// ─── Canvas Helper Primitives ────────────────────────────────────────────────

private fun DrawScope.drawWispCloud(center: Offset, color: Color) {
    drawOval(color, Offset(center.x - 35f, center.y - 8f), Size(70f, 20f))
    drawCircle(color, 15f, center + Offset(-20f, -6f))
    drawCircle(color, 20f, center + Offset(0f, -12f))
    drawCircle(color, 16f, center + Offset(20f, -6f))
}

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    drawLine(color, center - Offset(size, 0f), center + Offset(size, 0f), size * 0.3f, cap = StrokeCap.Round)
    drawLine(color, center - Offset(0f, size), center + Offset(0f, size), size * 0.3f, cap = StrokeCap.Round)
    drawLine(color, center - Offset(size * 0.6f, size * 0.6f), center + Offset(size * 0.6f, size * 0.6f), size * 0.2f, cap = StrokeCap.Round)
    drawLine(color, center - Offset(-size * 0.6f, size * 0.6f), center + Offset(-size * 0.6f, size * 0.6f), size * 0.2f, cap = StrokeCap.Round)
}

// ─── Story Play (Narration) ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoryPlayScreen(
    state: StoryUiState,
    onNarrationDone: () -> Unit,
    onAdvance: () -> Unit,
    onBackClick: () -> Unit
) {
    state.currentScene ?: return
    val chapter = state.activeChapter ?: return

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A))) {
        // Atmospheric background
        StoryAtmosphereBackground(chapterId = chapter.id)

        Column(modifier = Modifier.fillMaxSize()) {
            // Cinematic scene illustration (tall, immersive)
            Box(modifier = Modifier.fillMaxWidth()) {
                AnimatedContent(
                    targetState = state.sceneIndex,
                    transitionSpec = {
                        fadeIn(tween(600)) togetherWith fadeOut(tween(400))
                    },
                    label = "sceneImageTransition"
                ) { idx ->
                    val displayScene = chapter.scenes.getOrNull(idx) ?: return@AnimatedContent
                    CinematicSceneIllustration(
                        chapterId = chapter.id,
                        sceneId = displayScene.id,
                        sceneIndex = idx,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                    )
                }

                // Top gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.TopCenter)
                        .background(Brush.verticalGradient(listOf(Color(0xFF0D1B2A), Color.Transparent)))
                )

                // Bottom gradient overlay  
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .align(Alignment.BottomCenter)
                        .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF0D1B2A))))
                )

                // Back button
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }

                // Chapter title + scene indicator
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        chapter.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Scene ${state.sceneIndex + 1} / ${chapter.scenes.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(0.75f)
                    )
                }
            }

            // Progress bar
            CinematicProgressBar(
                progress = state.progressFraction,
                gradientStart = Color(chapter.gradientStart),
                gradientEnd = Color(chapter.gradientEnd)
            )

            // Scrollable narration content
            AnimatedContent(
                targetState = state.sceneIndex,
                transitionSpec = {
                    (fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 })
                        .togetherWith(fadeOut(tween(250)))
                },
                label = "narrationTransition"
            ) { idx ->
                val displayScene = chapter.scenes.getOrNull(idx) ?: return@AnimatedContent
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Narrator card with typewriter
                    CinematicNarrationCard(
                        narration = displayScene.narration,
                        chapterId = chapter.id,
                        onDone = onNarrationDone
                    )

                    // Advance button
                    AnimatedVisibility(
                        visible = state.narrationDone,
                        enter = fadeIn(tween(400)) + slideInVertically(tween(400, easing = EaseOutBack)) { it / 2 }
                    ) {
                        Button(
                            onClick = onAdvance,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(chapter.gradientStart)
                            )
                        ) {
                            Text(
                                if (state.isLastScene) "🎉 Selesai Bab!" else "Lanjut →",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun StoryAtmosphereBackground(chapterId: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "atmoAnim")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "atmoT"
    )
    val particles = remember { List(20) { Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        particles.forEachIndexed { i, (px, py, phase) ->
            val animAlpha = (sin((t + phase * 5f).toDouble()).toFloat() * 0.3f + 0.3f)
            val yDrift = sin((t * 0.4f + phase * 3f).toDouble()).toFloat() * h * 0.015f
            val particleColor = when (chapterId) {
                1 -> Color(0xFFFFA07A).copy(alpha = animAlpha)
                2 -> Color(0xFF87CEEB).copy(alpha = animAlpha)
                3 -> Color(0xFFFF90C8).copy(alpha = animAlpha)
                else -> Color.White.copy(alpha = animAlpha)
            }
            drawCircle(particleColor, if (i % 3 == 0) 3f else 1.5f, Offset(w * px, h * py + yDrift))
        }
    }
}

@Composable
private fun CinematicProgressBar(progress: Float, gradientStart: Color, gradientEnd: Color) {
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "progressAnim"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .background(Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth(animProgress)
                .background(Brush.horizontalGradient(listOf(gradientStart, gradientEnd)))
        )
    }
}

@Composable
private fun CinematicNarrationCard(
    narration: String,
    chapterId: Int,
    onDone: () -> Unit
) {
    var displayedText by remember(narration) { mutableStateOf("") }
    var isDone by remember(narration) { mutableStateOf(false) }

    LaunchedEffect(narration) {
        displayedText = ""
        isDone = false
        for (i in narration.indices) {
            displayedText = narration.substring(0, i + 1)
            delay(16L)
        }
        isDone = true
        onDone()
    }

    val accentColor = when (chapterId) {
        1 -> Color(0xFFFF9A56)
        2 -> Color(0xFF2196F3)
        3 -> Color(0xFF9C27B0)
        else -> Color(0xFF6C63FF)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E2A3A),
        shadowElevation = 12.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Narrator label
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📖", fontSize = 18.sp)
                }
                Column {
                    Text(
                        "Narrator",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Text(
                        "Klik untuk skip animasi teks",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            // Divider
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(accentColor.copy(alpha = 0.2f)))
            Spacer(Modifier.height(12.dp))

            // Typewriter text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isDone) {
                        displayedText = narration
                        isDone = true
                        onDone()
                    }
            ) {
                Column {
                    Text(
                        text = displayedText,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 28.sp,
                        color = Color.White
                    )
                    if (!isDone) {
                        val blink = rememberInfiniteTransition(label = "cursor")
                        val cursorAlpha by blink.animateFloat(
                            initialValue = 1f, targetValue = 0f,
                            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
                            label = "cursorBlink"
                        )
                        Text(
                            "▌",
                            color = accentColor.copy(alpha = cursorAlpha),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

// ─── Scene Illustrations (Cinematic, per chapter+scene) ──────────────────────

@Composable
private fun CinematicSceneIllustration(
    chapterId: Int,
    @Suppress("UNUSED_PARAMETER") sceneId: Int,
    sceneIndex: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sceneAnim")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing)),
        label = "sceneT"
    )

    Canvas(modifier = modifier) {
        when (chapterId) {
            1 -> drawChapter1Scene(sceneIndex, t)
            2 -> drawChapter2Scene(sceneIndex, t)
            3 -> drawChapter3Scene(sceneIndex, t)
            else -> drawGenericScene(t)
        }
    }
}

private fun DrawScope.drawChapter1Scene(sceneIndex: Int, t: Float) {
    when (sceneIndex) {
        0 -> drawForestPath(t)
        1 -> drawLostKittenScene(t)
        2 -> drawChildHelpingCatScene(t)
        3 -> drawSearchingForestScene(t)
        4 -> drawHappyReunionScene(t)
        else -> drawForestPath(t)
    }
}

private fun DrawScope.drawChapter2Scene(sceneIndex: Int, t: Float) {
    when (sceneIndex) {
        0 -> drawWideRiverScene(t)
        1 -> drawBrokenBridgeScene(t)
        2 -> drawTeamworkScene(t)
        3 -> drawSwimmingRiverScene(t)
        4 -> drawTreasureCaveScene(t)
        else -> drawWideRiverScene(t)
    }
}

private fun DrawScope.drawChapter3Scene(sceneIndex: Int, t: Float) {
    when (sceneIndex) {
        0 -> drawGardenDoorScene(t)
        1 -> drawBloomingGardenScene(t)
        2 -> drawBeeFlowerScene(t)
        3 -> drawColorfulFieldScene(t)
        4 -> drawHappyGirlGardenScene(t)
        else -> drawGardenDoorScene(t)
    }
}

// Chapter 1 Scenes
private fun DrawScope.drawForestPath(t: Float) {
    val w = size.width; val h = size.height
    // Warm sky
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFFFFBB6A), Color(0xFFFF7A3D)), 0f, h * 0.45f))
    drawRect(color = Color(0xFF3E7B1F), topLeft = Offset(0f, h * 0.45f), size = Size(w, h * 0.55f))
    // Sun
    drawCircle(Color(0x55FFD700), 60f, Offset(w * 0.7f, h * 0.22f))
    drawCircle(Color(0xFFFFD700), 38f, Offset(w * 0.7f, h * 0.22f))
    // Light rays through trees
    repeat(5) { i ->
        val rayX = w * (0.1f + i * 0.2f)
        drawLine(Color(0x22FFD700), Offset(w * 0.7f, h * 0.22f), Offset(rayX, h * 0.75f), 4f + i)
    }
    // Trees
    listOf(0.05f, 0.18f, 0.72f, 0.85f, 0.95f).forEachIndexed { i, xFrac ->
        val tH = h * (0.35f + (i % 2) * 0.08f)
        drawTreeDetailed(Offset(w * xFrac, h * 0.72f - tH * 0.1f), tH, i % 2 == 0)
    }
    // Winding path
    drawPath(path = Path().apply {
        moveTo(w * 0.35f, h)
        cubicTo(w * 0.38f, h * 0.85f, w * 0.42f, h * 0.72f, w * 0.48f, h * 0.6f)
        cubicTo(w * 0.52f, h * 0.5f, w * 0.55f, h * 0.45f, w * 0.52f, h * 0.35f)
        lineTo(w * 0.58f, h * 0.35f)
        cubicTo(w * 0.62f, h * 0.45f, w * 0.62f, h * 0.5f, w * 0.58f, h * 0.6f)
        cubicTo(w * 0.55f, h * 0.72f, w * 0.6f, h * 0.85f, w * 0.65f, h)
        close()
    }, color = Color(0xFFC8A878))
    // Animated floating leaves
    listOf(0.3f, 0.55f, 0.75f).forEachIndexed { i, xFrac ->
        val leafX = w * xFrac + sin((t + i * 2f).toDouble()).toFloat() * 20f
        val leafY = h * 0.4f + cos((t * 0.6f + i * 1.5f).toDouble()).toFloat() * h * 0.06f
        rotate(sin((t + i).toDouble()).toFloat() * 45f, Offset(leafX, leafY)) {
            drawOval(Color(0xFF66BB6A).copy(alpha = 0.8f), Offset(leafX - 10f, leafY - 5f), Size(20f, 10f))
        }
    }
}

private fun DrawScope.drawLostKittenScene(t: Float) {
    val w = size.width; val h = size.height
    // Moody twilight forest
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF7B1FA2), Color(0xFF3E7B1F)), 0f, h))
    // Misty ground
    drawRect(color = Color(0xFF2E7D32), topLeft = Offset(0f, h * 0.6f), size = Size(w, h * 0.4f))
    // Dark trees
    listOf(0.05f, 0.15f, 0.78f, 0.88f, 0.96f).forEachIndexed { i, xFrac ->
        drawTreeDetailed(Offset(w * xFrac, h * 0.62f), h * 0.38f, i % 2 == 0)
    }
    // Lone tree in center
    drawTreeDetailed(Offset(w * 0.5f, h * 0.62f), h * 0.32f, false)
    // Lonely kitten under tree
    drawCuteKitten(center = Offset(w * 0.5f, h * 0.72f), size = h * 0.16f, animate = true, t = t)
    // Question marks floating
    listOf(-0.1f, 0f, 0.1f).forEachIndexed { i, offX ->
        val qAlpha = (sin((t * 1.5f + i).toDouble()).toFloat() * 0.4f + 0.6f)
        val qY = h * 0.5f + sin((t + i).toDouble()).toFloat() * 10f
        drawTextPlaceholder("?", Offset(w * 0.5f + offX * w, qY), Color(0xFFFFD700).copy(alpha = qAlpha), 22f)
    }
    // Moonlight
    drawCircle(Color(0x33FFFFFF), 80f, Offset(w * 0.78f, h * 0.15f))
    drawCircle(Color(0xFFFFF8DC), 40f, Offset(w * 0.78f, h * 0.15f))
}

private fun DrawScope.drawChildHelpingCatScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFFFF9A56), Color(0xFFFFD89B), Color(0xFF4CAF50)), 0f, h))
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(0f, h * 0.62f), size = Size(w, h * 0.38f))
    listOf(0.05f, 0.88f).forEach { x -> drawTreeDetailed(Offset(w * x, h * 0.62f), h * 0.3f, true) }
    // Child figure
    val childX = w * 0.42f
    val childY = h * 0.58f
    val childSize = h * 0.22f
    drawCircle(Color(0xFFFFCC80), childSize * 0.2f, Offset(childX, childY - childSize * 0.7f)) // head
    drawRoundRect(Color(0xFF42A5F5), Offset(childX - childSize * 0.12f, childY - childSize * 0.5f), Size(childSize * 0.24f, childSize * 0.45f), CornerRadius(4f)) // body
    drawLine(Color(0xFF37474F), Offset(childX, childY - childSize * 0.05f), Offset(childX, childY + childSize * 0.38f), childSize * 0.1f) // legs
    // Arm reaching toward kitten
    drawLine(Color(0xFFFFCC80), Offset(childX + childSize * 0.12f, childY - childSize * 0.35f), Offset(childX + childSize * 0.45f, childY - childSize * 0.18f), childSize * 0.07f)
    // Kitten nearby
    drawCuteKitten(Offset(w * 0.6f, h * 0.72f), h * 0.12f, false, t)
    // Heart between them
    val heartAlpha = (sin((t * 2f).toDouble()).toFloat() * 0.3f + 0.7f)
    val heartScale = 0.8f + sin((t * 2f).toDouble()).toFloat() * 0.2f
    drawHeart(Offset(w * 0.52f, h * 0.52f), 14f * heartScale, Color(0xFFFF6090).copy(alpha = heartAlpha))
}

private fun DrawScope.drawSearchingForestScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFF4CAF50)), 0f, h * 0.5f))
    drawRect(color = Color(0xFF2E7D32), topLeft = Offset(0f, h * 0.5f), size = Size(w, h * 0.5f))
    listOf(0.05f, 0.2f, 0.72f, 0.88f).forEachIndexed { i, x -> drawTreeDetailed(Offset(w * x, h * 0.58f), h * 0.28f, i % 2 == 0) }
    // Path winding through
    drawPath(path = Path().apply {
        moveTo(w * 0.15f, h); cubicTo(w * 0.3f, h * 0.8f, w * 0.5f, h * 0.7f, w * 0.65f, h * 0.5f)
        lineTo(w * 0.72f, h * 0.5f); cubicTo(w * 0.58f, h * 0.7f, w * 0.42f, h * 0.8f, w * 0.28f, h)
        close()
    }, color = Color(0xFFBCAAA4))
    // Child + kitten walking together
    val walkOffset = sin((t * 1.2f).toDouble()).toFloat() * 3f
    drawCircle(Color(0xFFFFCC80), h * 0.04f, Offset(w * 0.42f, h * 0.6f - walkOffset)) // head
    drawLine(Color(0xFF42A5F5), Offset(w * 0.42f, h * 0.64f - walkOffset), Offset(w * 0.42f, h * 0.72f - walkOffset * 0.5f), h * 0.025f) // body
    drawCuteKitten(Offset(w * 0.55f, h * 0.73f - walkOffset * 0.3f), h * 0.09f, true, t)
}

private fun DrawScope.drawHappyReunionScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFFFFCC02), Color(0xFFFF9800), Color(0xFF4CAF50)), 0f, h))
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(0f, h * 0.55f), size = Size(w, h * 0.45f))
    // Cozy house
    drawCozyHouse(Offset(w * 0.6f, h * 0.38f), h * 0.42f)
    // Old woman
    val womanX = w * 0.58f
    val womanY = h * 0.52f
    drawCircle(Color(0xFFD7CCC8), h * 0.04f, Offset(womanX, womanY - h * 0.06f))
    drawRoundRect(Color(0xFF7B1FA2), Offset(womanX - h * 0.03f, womanY - h * 0.02f), Size(h * 0.06f, h * 0.1f), CornerRadius(3f))
    // Kitten jumping up
    val jumpY = h * 0.5f + sin((t * 3f).toDouble()).toFloat() * h * 0.04f
    drawCuteKitten(Offset(w * 0.52f, jumpY), h * 0.09f, false, t)
    // Celebration hearts & sparkles
    listOf(0.35f, 0.45f, 0.65f, 0.75f).forEachIndexed { i, xFrac ->
        val heartY = h * (0.3f + (i % 2) * 0.08f) + sin((t * 2f + i).toDouble()).toFloat() * 8f
        val heartAlpha = (sin((t * 1.5f + i * 0.7f).toDouble()).toFloat() * 0.4f + 0.6f)
        drawHeart(Offset(w * xFrac, heartY), 10f, Color(0xFFFF6090).copy(alpha = heartAlpha))
    }
    // Stars
    repeat(8) { i ->
        val sx = w * (0.1f + i * 0.11f)
        val sy = h * (0.1f + (i % 3) * 0.07f)
        val alpha = (sin((t * 2f + i * 0.9f).toDouble()).toFloat() * 0.5f + 0.5f)
        drawSparkle(Offset(sx, sy), 8f, StarGold.copy(alpha = alpha))
    }
}

// Chapter 2 Scenes
private fun DrawScope.drawWideRiverScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF87CEEB), Color(0xFF4DB6E0)), 0f, h * 0.45f))
    drawRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, h * 0.4f), size = Size(w * 0.25f, h * 0.6f))
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(w * 0.75f, h * 0.4f), size = Size(w * 0.25f, h * 0.6f))
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF2196F3), Color(0xFF0D47A1)), h * 0.42f, h), topLeft = Offset(w * 0.25f, h * 0.42f), size = Size(w * 0.5f, h * 0.58f))
    repeat(6) { i ->
        val ry = h * (0.52f + i * 0.07f) + sin((t + i * 0.5f).toDouble()).toFloat() * 4f
        drawLine(Color.White.copy(0.3f), Offset(w * 0.26f, ry), Offset(w * 0.74f, ry + 3f), 2.5f, cap = StrokeCap.Round)
    }
    drawCircle(Color(0x44FFEE00), 52f, Offset(w * 0.75f, h * 0.15f)); drawCircle(Color(0xFFFFD700), 32f, Offset(w * 0.75f, h * 0.15f))
    drawWispCloud(Offset(w * 0.2f + sin(t.toDouble()).toFloat() * 8f, h * 0.2f), Color.White.copy(0.85f))
    // Reflection sparkle on water
    repeat(5) { i ->
        val rx = w * (0.3f + i * 0.08f)
        val ry = h * (0.56f + (i % 3) * 0.06f)
        val a = (sin((t * 3f + i).toDouble()).toFloat() * 0.5f + 0.5f)
        drawCircle(Color.White.copy(a * 0.7f), 2f, Offset(rx + sin((t + i).toDouble()).toFloat() * 5f, ry))
    }
}

private fun DrawScope.drawBrokenBridgeScene(t: Float) {
    val w = size.width; val h = size.height
    drawWideRiverScene(t)
    // Broken bridge
    drawLine(Color(0xFF795548), Offset(w * 0.25f, h * 0.5f), Offset(w * 0.43f, h * 0.52f), 5f)
    drawLine(Color(0xFF795548), Offset(w * 0.57f, h * 0.53f), Offset(w * 0.75f, h * 0.5f), 5f)
    repeat(3) { i -> drawRect(Color(0xFFA1887F), Offset(w * 0.27f + i * (w * 0.055f), h * 0.48f), Size(w * 0.04f, h * 0.08f)) }
    repeat(3) { i -> drawRect(Color(0xFFA1887F), Offset(w * 0.59f + i * (w * 0.055f), h * 0.49f), Size(w * 0.04f, h * 0.08f)) }
    // Gap with falling plank
    val plankFall = (sin((t * 0.8f).toDouble()).toFloat() * 0.1f + 0.15f)
    rotate(20f + plankFall * 30f, Offset(w * 0.5f, h * 0.52f)) {
        drawRect(Color(0xFF6D4C41), Offset(w * 0.47f, h * 0.5f), Size(w * 0.06f, h * 0.04f))
    }
    // Worried children silhouettes
    listOf(0.16f, 0.2f, 0.24f).forEach { xFrac ->
        drawCircle(Color(0xFF37474F), 8f, Offset(w * xFrac, h * 0.42f))
        drawLine(Color(0xFF37474F), Offset(w * xFrac, h * 0.5f), Offset(w * xFrac, h * 0.58f), 5f)
    }
}

private fun DrawScope.drawTeamworkScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFF4CAF50), Color(0xFF2E7D32)), 0f, h))
    // Kids working together carrying branches
    listOf(0.22f, 0.36f, 0.5f, 0.64f).forEachIndexed { i, xFrac ->
        val personY = h * 0.55f + sin((t + i * 0.5f).toDouble()).toFloat() * 3f
        drawCircle(Color(0xFFFFCC80), 10f, Offset(w * xFrac, personY - 26f))
        drawLine(Color(0xFF42A5F5 + (i * 0x111111).toLong().toInt()), Offset(w * xFrac, personY - 16f), Offset(w * xFrac, personY + 6f), 6f)
    }
    // Branch they're carrying together
    drawLine(Color(0xFF8D6E63), Offset(w * 0.22f, h * 0.5f), Offset(w * 0.64f, h * 0.51f), 8f, cap = StrokeCap.Round)
    // Team spirit sparkles
    repeat(6) { i ->
        val sx = w * (0.25f + i * 0.08f)
        val sy = h * 0.35f + sin((t * 2f + i).toDouble()).toFloat() * 12f
        drawSparkle(Offset(sx, sy), 7f, StarGold.copy((sin((t + i * 0.7f).toDouble()).toFloat() * 0.4f + 0.6f)))
    }
    // Sun + clouds for positive energy
    drawCircle(Color(0x44FFEE00), 48f, Offset(w * 0.78f, h * 0.14f)); drawCircle(Color(0xFFFFD700), 30f, Offset(w * 0.78f, h * 0.14f))
    drawWispCloud(Offset(w * 0.18f, h * 0.18f), Color.White.copy(0.75f))
}

private fun DrawScope.drawSwimmingRiverScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF87CEEB), Color(0xFF64B5F6)), 0f, h * 0.4f))
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF29B6F6), Color(0xFF0277BD)), h * 0.4f, h), topLeft = Offset(0f, h * 0.4f), size = Size(w, h * 0.6f))
    drawRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, h * 0.35f), size = Size(w * 0.18f, h * 0.2f))
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(w * 0.82f, h * 0.35f), size = Size(w * 0.18f, h * 0.2f))
    // Rope from bank to bank
    drawLine(Color(0xFFFF8F00), Offset(w * 0.18f, h * 0.42f), Offset(w * 0.82f, h * 0.44f + sin(t.toDouble()).toFloat() * 4f), 4f)
    // Swimming person
    val personX = w * 0.3f + sin((t * 0.5f).toDouble()).toFloat() * w * 0.08f
    val personY = h * 0.52f + sin((t * 2f).toDouble()).toFloat() * 3f
    drawCircle(Color(0xFFFFCC80), 10f, Offset(personX, personY - 5f)) // head
    drawOval(Color(0xFF26C6DA), Offset(personX - 14f, personY), Size(28f, 10f)) // body in water
    // Swimming ripples
    repeat(3) { i ->
        val rippleR = (10f + i * 8f) + sin((t * 2f + i).toDouble()).toFloat() * 3f
        drawCircle(Color.White.copy(0.25f - i * 0.07f), rippleR, Offset(personX, personY + 5f), style = Stroke(1.5f))
    }
    // Water ripples
    repeat(4) { i ->
        val ry = h * (0.5f + i * 0.08f) + sin((t + i * 0.6f).toDouble()).toFloat() * 3f
        drawLine(Color.White.copy(0.22f), Offset(w * 0.12f, ry), Offset(w * 0.88f, ry + 2f), 2f, cap = StrokeCap.Round)
    }
}

private fun DrawScope.drawTreasureCaveScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF0D47A1), Color(0xFF1565C0)), 0f, h * 0.45f))
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF29B6F6), Color(0xFF0288D1)), h * 0.45f, h), topLeft = Offset(0f, h * 0.45f), size = Size(w, h * 0.55f))
    // Rocky cliff with cave
    drawPath(path = Path().apply {
        moveTo(w * 0.45f, 0f); lineTo(w, 0f); lineTo(w, h * 0.8f)
        cubicTo(w * 0.85f, h * 0.75f, w * 0.7f, h * 0.7f, w * 0.55f, h * 0.65f)
        cubicTo(w * 0.5f, h * 0.5f, w * 0.48f, h * 0.3f, w * 0.45f, 0f)
        close()
    }, color = Color(0xFF546E7A))
    // Cave entrance
    drawOval(Color(0xFF1A237E), Offset(w * 0.62f, h * 0.28f), Size(w * 0.25f, h * 0.28f))
    // Gold glow from cave
    val glowAlpha = (sin((t * 2f).toDouble()).toFloat() * 0.25f + 0.5f)
    drawCircle(Color(0xFFFFD700).copy(alpha = glowAlpha), w * 0.2f, Offset(w * 0.745f, h * 0.42f))
    // Treasure sparkles
    listOf(Offset(w * 0.7f, h * 0.38f), Offset(w * 0.78f, h * 0.44f), Offset(w * 0.74f, h * 0.5f)).forEachIndexed { i, pos ->
        val a = (sin((t * 3f + i * 1.2f).toDouble()).toFloat() * 0.5f + 0.5f)
        drawSparkle(pos, 10f, Color(0xFFFFD700).copy(alpha = a))
    }
    // Celebrating kids on left bank
    listOf(0.08f, 0.16f, 0.24f, 0.32f).forEachIndexed { i, xFrac ->
        val jumpY = h * 0.42f + sin((t * 2f + i * 0.5f).toDouble()).toFloat() * h * 0.03f
        drawCircle(Color(0xFFFFCC80), 8f, Offset(w * xFrac, jumpY - 18f))
        drawLine(listOf(Color(0xFFEF9A9A), Color(0xFF42A5F5), Color(0xFFA5D6A7), Color(0xFFFFCC80))[i],
            Offset(w * xFrac, jumpY - 10f), Offset(w * xFrac, jumpY + 5f), 5f)
    }
}

// Chapter 3 Scenes
private fun DrawScope.drawGardenDoorScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF7B1FA2), Color(0xFFC2185B)), 0f, h))
    drawRect(color = Color(0xFF546E7A), topLeft = Offset(0f, h * 0.3f), size = Size(w, h * 0.7f))
    repeat(6) { row -> repeat(9) { col ->
        val isEven = row % 2 == 0
        drawRect(Color(0xFF455A64), Offset(w / 9f * col + if (isEven) 0f else w / 18f + 1f, h * 0.3f + row * 16f + 1f), Size(w / 9f - 2f, 14f))
    }}
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(0f, h * 0.72f), size = Size(w, h * 0.28f))
    // Wooden door
    val dX = w * 0.37f; val dY = h * 0.28f; val dW = w * 0.26f; val dH = h * 0.47f
    drawPath(path = Path().apply {
        moveTo(dX, dY + dH); lineTo(dX, dY + dH * 0.38f)
        cubicTo(dX, dY - dH * 0.15f, dX + dW, dY - dH * 0.15f, dX + dW, dY + dH * 0.38f)
        lineTo(dX + dW, dY + dH); close()
    }, color = Color(0xFF6D4C41))
    repeat(3) { i -> drawLine(Color(0xFF4E342E), Offset(dX + 4f, dY + dH * (0.25f + i * 0.22f)), Offset(dX + dW - 4f, dY + dH * (0.25f + i * 0.22f)), 2f) }
    // Glowing handle
    val handleGlow = sin((t * 2f).toDouble()).toFloat() * 0.3f + 0.7f
    drawCircle(Color(0x44FFD700).copy(alpha = handleGlow), 12f, Offset(dX + dW * 0.72f, dY + dH * 0.58f))
    drawCircle(Color(0xFFFFD700), 6f, Offset(dX + dW * 0.72f, dY + dH * 0.58f))
    // Ivy vines on wall
    listOf(0.12f, 0.82f).forEach { xFrac ->
        drawPath(path = Path().apply {
            moveTo(w * xFrac, h * 0.7f); cubicTo(w * xFrac + 12f, h * 0.55f, w * xFrac - 8f, h * 0.45f, w * xFrac + 5f, h * 0.32f)
        }, color = Color(0xFF388E3C).copy(0.7f), style = Stroke(4f, cap = StrokeCap.Round))
    }
    // Magic glow around door
    val doorGlow = sin((t * 1.5f).toDouble()).toFloat() * 0.2f + 0.4f
    drawPath(path = Path().apply {
        moveTo(dX - 5f, dY + dH + 5f); lineTo(dX - 5f, dY + dH * 0.38f)
        cubicTo(dX - 5f, dY - dH * 0.15f - 5f, dX + dW + 5f, dY - dH * 0.15f - 5f, dX + dW + 5f, dY + dH * 0.38f)
        lineTo(dX + dW + 5f, dY + dH + 5f)
    }, color = Color(0xFFFFD700).copy(alpha = doorGlow * 0.6f), style = Stroke(3f))
}

private fun DrawScope.drawBloomingGardenScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFFA5D6A7)), 0f, h * 0.5f))
    drawRect(color = Color(0xFF4CAF50), topLeft = Offset(0f, h * 0.5f), size = Size(w, h * 0.5f))
    drawCircle(Color(0x44FFEE00), 55f, Offset(w * 0.82f, h * 0.15f)); drawCircle(Color(0xFFFFD700), 34f, Offset(w * 0.82f, h * 0.15f))
    drawWispCloud(Offset(w * 0.2f, h * 0.22f), Color.White.copy(0.8f))
    val flowerDefs = listOf(
        Triple(0.15f, 0.55f, Color(0xFFFF6090)),
        Triple(0.28f, 0.48f, Color(0xFFFFD700)),
        Triple(0.42f, 0.52f, Color(0xFF9C27B0)),
        Triple(0.55f, 0.46f, Color(0xFFFF7043)),
        Triple(0.68f, 0.53f, Color(0xFF26C6DA)),
        Triple(0.8f, 0.49f, Color(0xFFFF6090)),
        Triple(0.22f, 0.62f, Color(0xFFFFD700)),
        Triple(0.5f, 0.6f, Color(0xFF9C27B0)),
        Triple(0.74f, 0.62f, Color(0xFFFF7043))
    )
    flowerDefs.forEachIndexed { i, (xFrac, yFrac, color) ->
        val fX = w * xFrac
        val fY = h * yFrac + sin((t * 0.8f + i * 0.5f).toDouble()).toFloat() * 4f
        drawLine(Color(0xFF2E7D32), Offset(fX, fY + 18f), Offset(fX, fY), 4f, cap = StrokeCap.Round)
        repeat(6) { p ->
            val ang = (p * 60f - 30f * sin((t + i).toDouble()).toFloat()) * PI.toFloat() / 180f
            drawCircle(color, 10f, Offset(fX + cos(ang.toDouble()).toFloat() * 12f, fY + sin(ang.toDouble()).toFloat() * 12f))
        }
        drawCircle(Color.White, 7f, Offset(fX, fY))
        drawCircle(Color(0xFFFFEB3B), 4f, Offset(fX, fY))
    }
}

private fun DrawScope.drawBeeFlowerScene(t: Float) {
    val w = size.width; val h = size.height
    drawBloomingGardenScene(t)
    // Multiple bees
    listOf(Triple(0.3f, 0.35f, 0f), Triple(0.65f, 0.28f, 1.5f), Triple(0.48f, 0.42f, 3f)).forEach { (xFrac, yFrac, phase) ->
        val bX = w * xFrac + cos((t * 1.5f + phase).toDouble()).toFloat() * 35f
        val bY = h * yFrac + sin((t * 1.2f + phase).toDouble()).toFloat() * 22f
        drawBeeIcon(Offset(bX, bY), h * 0.05f)
    }
    // Pollen particles
    repeat(12) { i ->
        val px = w * (0.15f + (i % 7) * 0.11f) + sin((t + i * 0.8f).toDouble()).toFloat() * 15f
        val py = h * (0.25f + (i % 4) * 0.1f) + cos((t * 0.6f + i).toDouble()).toFloat() * 10f
        drawCircle(Color(0xFFFFEB3B).copy(0.5f), 3f, Offset(px, py))
    }
}

private fun DrawScope.drawColorfulFieldScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFFFFE0B2), Color(0xFFFF8A65)), 0f, h * 0.42f))
    drawRect(color = Color(0xFF388E3C), topLeft = Offset(0f, h * 0.42f), size = Size(w, h * 0.58f))
    drawCircle(Color(0x44FFEE00), 50f, Offset(w * 0.15f, h * 0.18f)); drawCircle(Color(0xFFFFD700), 32f, Offset(w * 0.15f, h * 0.18f))
    // Dense field of colorful flowers
    val fieldColors = listOf(Color(0xFFFF6090), Color(0xFFFFD700), Color(0xFF9C27B0), Color(0xFFFF7043), Color(0xFF26C6DA), Color(0xFF66BB6A))
    repeat(24) { i ->
        val xFrac = 0.05f + (i % 8) * 0.12f
        val yFrac = 0.48f + (i / 8) * 0.12f
        val fX = w * xFrac + sin((t * 0.4f + i * 0.3f).toDouble()).toFloat() * 6f
        val fY = h * yFrac + cos((t * 0.3f + i * 0.5f).toDouble()).toFloat() * 4f
        val color = fieldColors[i % fieldColors.size]
        drawLine(Color(0xFF2E7D32), Offset(fX, fY + 14f), Offset(fX, fY), 3f, cap = StrokeCap.Round)
        repeat(5) { p ->
            val ang = p * 72f * PI.toFloat() / 180f
            drawCircle(color, 8f, Offset(fX + cos(ang.toDouble()).toFloat() * 9f, fY + sin(ang.toDouble()).toFloat() * 9f))
        }
        drawCircle(Color.White, 5f, Offset(fX, fY))
    }
    // Butterflies
    listOf(Triple(0.4f, 0.32f, 0f), Triple(0.72f, 0.38f, 2f)).forEach { (xFrac, yFrac, phase) ->
        val bX = w * xFrac + sin((t * 0.8f + phase).toDouble()).toFloat() * 40f
        val bY = h * yFrac + cos((t * 0.6f + phase).toDouble()).toFloat() * 20f
        drawOval(Color(0xFFFF6090).copy(0.85f), Offset(bX - 14f, bY - 8f), Size(12f, 16f))
        drawOval(Color(0xFFFF6090).copy(0.85f), Offset(bX + 2f, bY - 8f), Size(12f, 16f))
        drawOval(Color(0xFFFF90C8).copy(0.7f), Offset(bX - 10f, bY - 5f), Size(8f, 12f))
        drawOval(Color(0xFFFF90C8).copy(0.7f), Offset(bX + 2f, bY - 5f), Size(8f, 12f))
        drawLine(Color(0xFF263238), Offset(bX - 1f, bY - 8f), Offset(bX - 1f, bY + 8f), 2f)
    }
}

private fun DrawScope.drawHappyGirlGardenScene(t: Float) {
    val w = size.width; val h = size.height
    drawBloomingGardenScene(t)
    // Girl character (more detailed)
    val girlX = w * 0.5f
    val girlY = h * 0.55f
    val gSize = h * 0.25f
    // Dress
    drawPath(path = Path().apply {
        moveTo(girlX - gSize * 0.18f, girlY - gSize * 0.12f)
        lineTo(girlX - gSize * 0.28f, girlY + gSize * 0.35f)
        lineTo(girlX + gSize * 0.28f, girlY + gSize * 0.35f)
        lineTo(girlX + gSize * 0.18f, girlY - gSize * 0.12f); close()
    }, color = Color(0xFFE91E63))
    // Body
    drawRoundRect(Color(0xFFFFCC80), Offset(girlX - gSize * 0.1f, girlY - gSize * 0.52f), Size(gSize * 0.2f, gSize * 0.45f), CornerRadius(gSize * 0.04f))
    // Head
    drawCircle(Color(0xFFFFCC80), gSize * 0.22f, Offset(girlX, girlY - gSize * 0.65f))
    // Hair
    drawCircle(Color(0xFF5D4037), gSize * 0.25f, Offset(girlX, girlY - gSize * 0.72f))
    drawCircle(Color(0xFFFFCC80), gSize * 0.22f, Offset(girlX, girlY - gSize * 0.65f))
    // Eyes (happy)
    drawCircle(Color(0xFF263238), gSize * 0.04f, Offset(girlX - gSize * 0.07f, girlY - gSize * 0.68f))
    drawCircle(Color(0xFF263238), gSize * 0.04f, Offset(girlX + gSize * 0.07f, girlY - gSize * 0.68f))
    // Smile
    drawPath(path = Path().apply {
        moveTo(girlX - gSize * 0.06f, girlY - gSize * 0.58f)
        cubicTo(girlX - gSize * 0.02f, girlY - gSize * 0.54f, girlX + gSize * 0.02f, girlY - gSize * 0.54f, girlX + gSize * 0.06f, girlY - gSize * 0.58f)
    }, color = Color(0xFFFF6090), style = Stroke(2.5f, cap = StrokeCap.Round))
    // Arms raised in joy
    val armSway = sin((t * 1.5f).toDouble()).toFloat() * 12f
    drawLine(Color(0xFFFFCC80), Offset(girlX - gSize * 0.1f, girlY - gSize * 0.3f), Offset(girlX - gSize * 0.35f, girlY - gSize * 0.6f - armSway), gSize * 0.06f, cap = StrokeCap.Round)
    drawLine(Color(0xFFFFCC80), Offset(girlX + gSize * 0.1f, girlY - gSize * 0.3f), Offset(girlX + gSize * 0.35f, girlY - gSize * 0.6f + armSway), gSize * 0.06f, cap = StrokeCap.Round)
    // Confetti / flower petals celebration
    repeat(10) { i ->
        val angle = (t * 60f + i * 36f) * PI.toFloat() / 180f
        val dist = gSize * (0.6f + (i % 3) * 0.15f)
        val petX = girlX + cos(angle.toDouble()).toFloat() * dist
        val petY = girlY - gSize * 0.5f + sin(angle.toDouble()).toFloat() * dist * 0.5f
        drawCircle(listOf(Color(0xFFFF6090), Color(0xFFFFD700), Color(0xFF9C27B0))[i % 3], 5f, Offset(petX, petY))
    }
}

// ─── Drawing Helpers ─────────────────────────────────────────────────────────

private fun DrawScope.drawTreeDetailed(base: Offset, treeHeight: Float, lightVariant: Boolean) {
    val trunkW = treeHeight * 0.08f
    val trunkH = treeHeight * 0.4f
    drawRoundRect(
        color = if (lightVariant) Color(0xFF795548) else Color(0xFF5D4037),
        topLeft = Offset(base.x - trunkW / 2, base.y - trunkH),
        size = Size(trunkW, trunkH), cornerRadius = CornerRadius(trunkW * 0.3f)
    )
    val canopyY = base.y - trunkH
    listOf(0f, 0.12f, 0.22f).forEachIndexed { i, offset ->
        val r = (treeHeight * (0.32f - offset * 0.5f)).coerceAtLeast(18f)
        drawCircle(
            color = if (lightVariant) {
                listOf(Color(0xFF66BB6A), Color(0xFF43A047), Color(0xFF2E7D32))[i]
            } else {
                listOf(Color(0xFF388E3C), Color(0xFF2E7D32), Color(0xFF1B5E20))[i]
            },
            radius = r, center = Offset(base.x, canopyY - offset * treeHeight * 0.35f)
        )
    }
}

private fun DrawScope.drawCuteKitten(center: Offset, size: Float, animate: Boolean, t: Float) {
    // Body
    drawOval(Color(0xFFFFA07A), Offset(center.x - size * 0.35f, center.y - size * 0.2f), Size(size * 0.7f, size * 0.55f))
    // Head
    drawCircle(Color(0xFFFFA07A), size * 0.32f, Offset(center.x, center.y - size * 0.45f))
    // Ears
    drawPath(path = Path().apply {
        moveTo(center.x - size * 0.18f, center.y - size * 0.64f)
        lineTo(center.x - size * 0.3f, center.y - size * 0.85f)
        lineTo(center.x - size * 0.06f, center.y - size * 0.67f); close()
    }, color = Color(0xFFFFA07A))
    drawPath(path = Path().apply {
        moveTo(center.x + size * 0.18f, center.y - size * 0.64f)
        lineTo(center.x + size * 0.3f, center.y - size * 0.85f)
        lineTo(center.x + size * 0.06f, center.y - size * 0.67f); close()
    }, color = Color(0xFFFFA07A))
    // Inner ears
    drawPath(path = Path().apply {
        moveTo(center.x - size * 0.18f, center.y - size * 0.66f)
        lineTo(center.x - size * 0.26f, center.y - size * 0.8f)
        lineTo(center.x - size * 0.09f, center.y - size * 0.68f); close()
    }, color = Color(0xFFFF80AB))
    // Eyes
    drawCircle(Color(0xFF263238), size * 0.065f, Offset(center.x - size * 0.12f, center.y - size * 0.48f))
    drawCircle(Color(0xFF263238), size * 0.065f, Offset(center.x + size * 0.12f, center.y - size * 0.48f))
    drawCircle(Color.White, size * 0.025f, Offset(center.x - size * 0.11f, center.y - size * 0.49f))
    drawCircle(Color.White, size * 0.025f, Offset(center.x + size * 0.13f, center.y - size * 0.49f))
    // Nose
    drawPath(path = Path().apply {
        moveTo(center.x, center.y - size * 0.4f)
        lineTo(center.x - size * 0.04f, center.y - size * 0.36f)
        lineTo(center.x + size * 0.04f, center.y - size * 0.36f); close()
    }, color = Color(0xFFFF69B4))
    // Whiskers
    drawLine(Color(0xFF90A4AE), Offset(center.x - size * 0.04f, center.y - size * 0.38f), Offset(center.x - size * 0.3f, center.y - size * 0.42f), 1.5f)
    drawLine(Color(0xFF90A4AE), Offset(center.x - size * 0.04f, center.y - size * 0.37f), Offset(center.x - size * 0.3f, center.y - size * 0.35f), 1.5f)
    drawLine(Color(0xFF90A4AE), Offset(center.x + size * 0.04f, center.y - size * 0.38f), Offset(center.x + size * 0.3f, center.y - size * 0.42f), 1.5f)
    drawLine(Color(0xFF90A4AE), Offset(center.x + size * 0.04f, center.y - size * 0.37f), Offset(center.x + size * 0.3f, center.y - size * 0.35f), 1.5f)
    // Tail (animated if enabled)
    val tailSway = if (animate) sin(t.toDouble()).toFloat() * 20f else 0f
    drawPath(path = Path().apply {
        moveTo(center.x + size * 0.35f, center.y - size * 0.05f)
        cubicTo(center.x + size * 0.7f, center.y + size * 0.2f,
            center.x + size * 0.75f + tailSway, center.y - size * 0.3f,
            center.x + size * 0.55f + tailSway * 0.7f, center.y - size * 0.45f)
    }, color = Color(0xFFFFA07A), style = Stroke(size * 0.1f, cap = StrokeCap.Round))
}

private fun DrawScope.drawCozyHouse(topLeft: Offset, houseH: Float) {
    val houseW = houseH * 0.8f
    // Walls
    drawRoundRect(Color(0xFFFFE082), topLeft + Offset(0f, houseH * 0.35f), Size(houseW, houseH * 0.65f), CornerRadius(4f))
    // Roof
    drawPath(path = Path().apply {
        moveTo(topLeft.x - houseW * 0.1f, topLeft.y + houseH * 0.37f)
        lineTo(topLeft.x + houseW * 0.5f, topLeft.y)
        lineTo(topLeft.x + houseW * 1.1f, topLeft.y + houseH * 0.37f); close()
    }, color = Color(0xFFE57373))
    // Door
    drawRoundRect(Color(0xFF8D6E63), topLeft + Offset(houseW * 0.36f, houseH * 0.65f), Size(houseW * 0.28f, houseH * 0.35f), CornerRadius(6f, 6f))
    // Windows
    drawRoundRect(Color(0xFF81D4FA), topLeft + Offset(houseW * 0.1f, houseH * 0.46f), Size(houseW * 0.2f, houseH * 0.16f), CornerRadius(3f))
    drawRoundRect(Color(0xFF81D4FA), topLeft + Offset(houseW * 0.7f, houseH * 0.46f), Size(houseW * 0.2f, houseH * 0.16f), CornerRadius(3f))
    // Chimney
    drawRect(Color(0xFF8D6E63), topLeft + Offset(houseW * 0.65f, -houseH * 0.08f), Size(houseW * 0.1f, houseH * 0.22f))
}

private fun DrawScope.drawHeart(center: Offset, size: Float, color: Color) {
    drawPath(path = Path().apply {
        moveTo(center.x, center.y + size * 0.6f)
        cubicTo(center.x - size * 1.3f, center.y - size * 0.2f, center.x - size * 1.3f, center.y - size, center.x, center.y - size * 0.4f)
        cubicTo(center.x + size * 1.3f, center.y - size, center.x + size * 1.3f, center.y - size * 0.2f, center.x, center.y + size * 0.6f)
    }, color = color)
}

private fun DrawScope.drawBeeIcon(center: Offset, size: Float) {
    drawOval(Color(0xFFFFD700), Offset(center.x - size * 0.5f, center.y - size * 0.3f), Size(size, size * 0.6f))
    drawLine(Color(0xFF263238), Offset(center.x - size * 0.3f, center.y - size * 0.06f), Offset(center.x + size * 0.3f, center.y - size * 0.06f), size * 0.12f)
    drawLine(Color(0xFF263238), Offset(center.x - size * 0.3f, center.y + size * 0.1f), Offset(center.x + size * 0.3f, center.y + size * 0.1f), size * 0.12f)
    drawOval(Color.White.copy(0.75f), Offset(center.x - size * 0.4f, center.y - size * 0.5f), Size(size * 0.6f, size * 0.42f))
    drawOval(Color.White.copy(0.75f), Offset(center.x - size * 0.4f, center.y + size * 0.06f), Size(size * 0.6f, size * 0.42f))
}

@Suppress("UNUSED_PARAMETER")
private fun DrawScope.drawTextPlaceholder(text: String, center: Offset, color: Color, size: Float) {
    // Visual representation for emoji/character
    drawCircle(color.copy(alpha = 0.25f), size * 1.2f, center)
    drawCircle(color.copy(alpha = 0.6f), size * 0.5f, center)
}

private fun DrawScope.drawGenericScene(t: Float) {
    val w = size.width; val h = size.height
    drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF87CEEB), Color(0xFF4CAF50)), 0f, h))
    drawWispCloud(Offset(w * 0.3f + sin(t.toDouble()).toFloat() * 12f, h * 0.25f), Color.White.copy(0.85f))
    drawCircle(Color(0x44FFEE00), 50f, Offset(w * 0.8f, h * 0.2f)); drawCircle(Color(0xFFFFD700), 30f, Offset(w * 0.8f, h * 0.2f))
    drawPath(path = Path().apply { moveTo(0f, h * 0.68f); cubicTo(w * 0.3f, h * 0.58f, w * 0.7f, h * 0.64f, w, h * 0.6f); lineTo(w, h); lineTo(0f, h); close() }, color = Color(0xFF4CAF50))
}

// ─── Quiz Screen ──────────────────────────────────────────────────────────────

@Composable
private fun QuizPlayScreen(
    state: StoryUiState,
    onSelectOption: (Int) -> Unit,
    onAdvance: () -> Unit,
    onBackClick: () -> Unit
) {
    val scene = state.currentScene ?: return
    val quiz = scene.quiz ?: return
    val chapter = state.activeChapter ?: return

    LaunchedEffect(state.isAnswerRevealed) {
        if (state.isAnswerRevealed) {
            delay(2400)
            onAdvance()
        }
    }

    // Confetti state
    val confettiPieces = remember { mutableStateListOf<ConfettiPiece>() }
    LaunchedEffect(state.isAnswerRevealed) {
        if (state.isAnswerRevealed && state.selectedOptionIndex == quiz.correctIndex) {
            confettiPieces.clear()
            repeat(60) { i ->
                confettiPieces.add(ConfettiPiece(
                    x = Random.nextFloat(),
                    y = -0.05f - Random.nextFloat() * 0.2f,
                    color = listOf(Color(0xFFFF6090), Color(0xFFFFD700), Color(0xFF6C63FF), Color(0xFF00C896), Color(0xFFFF7043))[i % 5],
                    rotation = Random.nextFloat() * 360f,
                    speed = 0.003f + Random.nextFloat() * 0.005f,
                    wobble = Random.nextFloat() * 0.02f,
                    phase = Random.nextFloat() * (2 * PI).toFloat()
                ))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1B2A))) {
        // Atmosphere
        StoryAtmosphereBackground(chapterId = chapter.id)

        Column(modifier = Modifier.fillMaxSize()) {
            // Scene image header
            Box(modifier = Modifier.fillMaxWidth()) {
                CinematicSceneIllustration(
                    chapterId = chapter.id,
                    sceneId = scene.id,
                    sceneIndex = state.sceneIndex,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).align(Alignment.TopCenter)
                    .background(Brush.verticalGradient(listOf(Color(0xFF0D1B2A), Color.Transparent))))
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).align(Alignment.BottomCenter)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xFF0D1B2A)))))

                IconButton(onClick = onBackClick, modifier = Modifier.padding(12.dp).size(40.dp)
                    .background(Color.Black.copy(0.4f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }

                // Quiz badge
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                    .background(Color(0xFF6C63FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("🧠 Kuis", color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.ExtraBold)
                }
            }

            CinematicProgressBar(state.progressFraction, Color(chapter.gradientStart), Color(chapter.gradientEnd))

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Quiz question card
                QuizQuestionCard(question = quiz.question, chapter = chapter)

                // Options
                quiz.options.forEachIndexed { idx, option ->
                    EnhancedQuizOption(
                        text = option,
                        index = idx,
                        selectedIndex = state.selectedOptionIndex,
                        correctIndex = quiz.correctIndex,
                        isRevealed = state.isAnswerRevealed,
                        onSelect = { onSelectOption(idx) }
                    )
                }

                // Feedback card
                AnimatedVisibility(
                    visible = state.isAnswerRevealed,
                    enter = fadeIn(tween(350)) + scaleIn(tween(350, easing = EaseOutBack), 0.85f) + slideInVertically(tween(350)) { it / 3 }
                ) {
                    val isCorrect = state.selectedOptionIndex == quiz.correctIndex
                    QuizFeedbackCard(isCorrect = isCorrect, explanation = quiz.explanation)
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // Confetti overlay
        if (confettiPieces.isNotEmpty()) {
            ConfettiCanvas(pieces = confettiPieces)
        }
    }
}

data class ConfettiPiece(
    val x: Float,
    val y: Float,
    val color: Color,
    val rotation: Float,
    val speed: Float,
    val wobble: Float,
    val phase: Float
)

@Composable
private fun ConfettiCanvas(pieces: List<ConfettiPiece>) {
    val infiniteTransition = rememberInfiniteTransition(label = "confettiAnim")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 200f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "confettiT"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        pieces.forEach { piece ->
            val currentY = (piece.y + t * piece.speed) % 1.1f
            if (currentY < 0) return@forEach
            val currentX = piece.x + sin((t * 2f + piece.phase).toDouble()).toFloat() * piece.wobble
            val rot = (piece.rotation + t * 80f) % 360f
            val alpha = (1f - currentY.coerceIn(0.8f, 1.1f) / 1.1f * 3f).coerceIn(0f, 1f)

            translate(w * currentX, h * currentY) {
                rotate(rot) {
                    drawRect(
                        color = piece.color.copy(alpha = alpha),
                        topLeft = Offset(-5f, -3f),
                        size = Size(10f, 6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizQuestionCard(question: String, chapter: StoryChapter) {
    val infiniteTransition = rememberInfiniteTransition(label = "quizAnim")
    val questionBounce by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "questionBounce"
    )
    val accentColor = Color(chapter.gradientStart)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E2A3A)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .scale(0.9f + questionBounce * 0.1f)
                        .background(accentColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("❓", fontSize = 22.sp)
                }
                Column {
                    Text("Pertanyaan", style = MaterialTheme.typography.labelSmall, color = accentColor, fontWeight = FontWeight.Bold)
                    Text("Pilih jawaban yang benar!", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.5f))
                }
            }
            Spacer(Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(accentColor.copy(0.2f)))
            Spacer(Modifier.height(12.dp))
            Text(
                text = question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 26.sp
            )
        }
    }
}

@Composable
private fun EnhancedQuizOption(
    text: String,
    index: Int,
    selectedIndex: Int,
    correctIndex: Int,
    isRevealed: Boolean,
    onSelect: () -> Unit
) {
    val isSelected = selectedIndex == index
    val isCorrect = index == correctIndex

    val bgTargetColor = when {
        !isRevealed -> Color(0xFF1E2A3A)
        isCorrect -> CorrectGreen.copy(alpha = 0.25f)
        isSelected && !isCorrect -> WrongRed.copy(alpha = 0.25f)
        else -> Color(0xFF1E2A3A)
    }
    val bgColor by animateColorAsState(bgTargetColor, tween(350), label = "optionBg")

    val borderColor = when {
        !isRevealed && isSelected -> Color(0xFF6C63FF)
        isRevealed && isCorrect -> CorrectGreen
        isRevealed && isSelected && !isCorrect -> WrongRed
        else -> Color.White.copy(alpha = 0.12f)
    }

    val scaleAnim = remember { Animatable(1f) }
    LaunchedEffect(isSelected) {
        if (isSelected && !isRevealed) {
            scaleAnim.animateTo(0.93f, tween(70))
            scaleAnim.animateTo(1.05f, spring(Spring.DampingRatioMediumBouncy))
            scaleAnim.animateTo(1f, tween(120))
        }
    }
    // Shake on wrong
    val shakeAnim = remember { Animatable(0f) }
    LaunchedEffect(isRevealed) {
        if (isRevealed && isSelected && !isCorrect) {
            repeat(4) {
                shakeAnim.animateTo(8f, tween(60))
                shakeAnim.animateTo(-8f, tween(60))
            }
            shakeAnim.animateTo(0f, tween(80))
        }
    }

    val letters = listOf("A", "B", "C", "D")
    val letterBg = when {
        isRevealed && isCorrect -> CorrectGreen
        isRevealed && isSelected && !isCorrect -> WrongRed
        isSelected -> Color(0xFF6C63FF)
        else -> Color.White.copy(alpha = 0.12f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim.value)
            .offset { IntOffset(shakeAnim.value.toInt(), 0) }
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (isSelected || (isRevealed && isCorrect)) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = !isRevealed) { onSelect() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier.size(36.dp).background(letterBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    letters.getOrElse(index) { "$index" },
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected || (isRevealed && isCorrect)) Color.White else Color.White.copy(0.5f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            AnimatedVisibility(visible = isRevealed && isCorrect, enter = scaleIn(spring(Spring.DampingRatioLowBouncy))) {
                Text("✓", color = CorrectGreen, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            AnimatedVisibility(visible = isRevealed && isSelected && !isCorrect, enter = scaleIn(tween(200))) {
                Text("✗", color = WrongRed, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
        }
    }
}

@Composable
private fun QuizFeedbackCard(isCorrect: Boolean, explanation: String) {
    @Suppress("UNUSED_VARIABLE")
    val bgColor = if (isCorrect) CorrectGreen.copy(alpha = 0.15f) else WrongRed.copy(alpha = 0.12f)
    val borderColor = if (isCorrect) CorrectGreen.copy(alpha = 0.4f) else WrongRed.copy(alpha = 0.3f)
    val accentColor = if (isCorrect) CorrectGreen else WrongRed

    Surface(
        modifier = Modifier.fillMaxWidth().border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF1E2A3A)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier.size(44.dp).background(accentColor.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isCorrect) "✅" else "💡", fontSize = 22.sp)
            }
            Column {
                Text(
                    if (isCorrect) "Benar sekali! 🎉" else "Hampir benar!",
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    style = MaterialTheme.typography.titleSmall
                )
                if (explanation.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Text(explanation, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.8f), lineHeight = 20.sp)
                }
            }
        }
    }
}

// ─── Summary Screen ───────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SummaryScreen(
    state: StoryUiState,
    onPlayAgain: () -> Unit,
    onBackToList: () -> Unit
) {
    val chapter = state.activeChapter ?: return
    val stars = state.starCount

    // Confetti
    val confettiPieces = remember {
        List(80) { i ->
            ConfettiPiece(
                x = Random.nextFloat(),
                y = -0.1f - Random.nextFloat() * 0.5f,
                color = listOf(Color(0xFFFF6090), Color(0xFFFFD700), Color(0xFF6C63FF), Color(0xFF00C896), Color(0xFFFF7043), Color(0xFF81C784))[i % 6],
                rotation = Random.nextFloat() * 360f,
                speed = 0.002f + Random.nextFloat() * 0.004f,
                wobble = Random.nextFloat() * 0.025f,
                phase = Random.nextFloat() * (2 * PI).toFloat()
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        SummaryBackground(chapterId = chapter.id)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Trophy / achievement header
            SummaryHeroCard(chapter = chapter, stars = stars)

            // Animated stars row
            AnimatedStarsRow(stars = stars)

            // Score panel
            ScorePanel(correctCount = state.correctCount, totalQuizzes = state.totalQuizzes, stars = stars)

            // Vocab words learned
            if (chapter.vocabWords.isNotEmpty()) {
                VocabGallery(vocabWords = chapter.vocabWords)
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBackToList,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2A3A))
                ) {
                    Text("Chapter Lain", fontWeight = FontWeight.Bold, color = Color.White)
                }
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(chapter.gradientStart))
                ) {
                    Icon(Icons.Default.Replay, null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Main Lagi", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Confetti overlay
        ConfettiCanvas(pieces = confettiPieces)
    }
}

@Composable
private fun SummaryBackground(@Suppress("UNUSED_PARAMETER") chapterId: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "summaryBg")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "summaryT"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height
        drawRect(brush = Brush.verticalGradient(listOf(Color(0xFF0D0D1A), Color(0xFF1A0533), Color(0xFF0D1B2A)), 0f, h))
        // Animated glow orbs
        listOf(
            Triple(0.2f, 0.15f, Color(0x446C63FF)),
            Triple(0.8f, 0.35f, Color(0x44FF6090)),
            Triple(0.5f, 0.7f, Color(0x4400C896))
        ).forEachIndexed { i, (xFrac, yFrac, color) ->
            val drift = sin((t + i * 2f).toDouble()).toFloat()
            drawCircle(color, w * 0.4f, Offset(w * xFrac + drift * 20f, h * yFrac + drift * 15f))
        }
        // Star field
        repeat(40) { i ->
            val sx = w * ((i * 0.0317f) % 1f)
            val sy = h * ((i * 0.0509f) % 1f)
            val alpha = (sin((t * 1.5f + i * 0.7f).toDouble()).toFloat() * 0.4f + 0.5f)
            drawCircle(Color.White.copy(alpha = alpha), if (i % 5 == 0) 2.5f else 1.5f, Offset(sx, sy))
        }
    }
}

@Composable
private fun SummaryHeroCard(chapter: StoryChapter, @Suppress("UNUSED_PARAMETER") stars: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroCard")
    val t by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing)),
        label = "heroT"
    )
    @Suppress("UNUSED_VARIABLE")
    val emojiScale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "emojiScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
    ) {
        // Canvas background
        Canvas(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            val w = size.width; val h = size.height
            drawRect(brush = Brush.linearGradient(listOf(Color(chapter.gradientStart), Color(chapter.gradientEnd))))
            // Decorative circles
            drawCircle(Color.White.copy(0.1f), w * 0.5f, Offset(-w * 0.1f, -h * 0.2f))
            drawCircle(Color.White.copy(0.08f), w * 0.4f, Offset(w * 1.1f, h * 0.8f))
            // Sparkles
            repeat(8) { i ->
                val angle = (i * 45f + t * 20f) * PI.toFloat() / 180f
                val dist = w * 0.35f + sin((t + i).toDouble()).toFloat() * 10f
                val alpha = (sin((t * 2f + i * 0.8f).toDouble()).toFloat() * 0.4f + 0.6f)
                drawSparkle(Offset(w * 0.5f + cos(angle.toDouble()).toFloat() * dist, h * 0.5f + sin(angle.toDouble()).toFloat() * dist * 0.5f), 8f, Color.White.copy(alpha))
            }
            // Trophy illustration
            val trophyCenter = Offset(w * 0.5f, h * 0.5f)
            val tSize = h * 0.55f
            // Trophy cup
            drawPath(path = Path().apply {
                moveTo(trophyCenter.x - tSize * 0.3f, trophyCenter.y - tSize * 0.45f)
                lineTo(trophyCenter.x - tSize * 0.35f, trophyCenter.y + tSize * 0.08f)
                cubicTo(trophyCenter.x - tSize * 0.35f, trophyCenter.y + tSize * 0.25f, trophyCenter.x + tSize * 0.35f, trophyCenter.y + tSize * 0.25f, trophyCenter.x + tSize * 0.35f, trophyCenter.y + tSize * 0.08f)
                lineTo(trophyCenter.x + tSize * 0.3f, trophyCenter.y - tSize * 0.45f)
                close()
            }, color = Color(0xFFFFD700))
            // Trophy handles
            drawPath(path = Path().apply {
                moveTo(trophyCenter.x - tSize * 0.35f, trophyCenter.y - tSize * 0.25f)
                cubicTo(trophyCenter.x - tSize * 0.6f, trophyCenter.y - tSize * 0.2f, trophyCenter.x - tSize * 0.6f, trophyCenter.y + tSize * 0.05f, trophyCenter.x - tSize * 0.35f, trophyCenter.y + tSize * 0.05f)
            }, color = Color(0xFFFFC107), style = Stroke(tSize * 0.06f, cap = StrokeCap.Round))
            drawPath(path = Path().apply {
                moveTo(trophyCenter.x + tSize * 0.35f, trophyCenter.y - tSize * 0.25f)
                cubicTo(trophyCenter.x + tSize * 0.6f, trophyCenter.y - tSize * 0.2f, trophyCenter.x + tSize * 0.6f, trophyCenter.y + tSize * 0.05f, trophyCenter.x + tSize * 0.35f, trophyCenter.y + tSize * 0.05f)
            }, color = Color(0xFFFFC107), style = Stroke(tSize * 0.06f, cap = StrokeCap.Round))
            // Trophy base
            drawRoundRect(Color(0xFFE6AD00), Offset(trophyCenter.x - tSize * 0.15f, trophyCenter.y + tSize * 0.25f), Size(tSize * 0.3f, tSize * 0.12f), CornerRadius(4f))
            drawRoundRect(Color(0xFFE6AD00), Offset(trophyCenter.x - tSize * 0.22f, trophyCenter.y + tSize * 0.37f), Size(tSize * 0.44f, tSize * 0.1f), CornerRadius(6f))
            // Trophy shine
            drawLine(Color.White.copy(0.4f), Offset(trophyCenter.x - tSize * 0.15f, trophyCenter.y - tSize * 0.35f), Offset(trophyCenter.x - tSize * 0.1f, trophyCenter.y + tSize * 0.12f), tSize * 0.04f, cap = StrokeCap.Round)
        }

        // Text overlay
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Chapter Selesai!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                "${chapter.emoji} ${chapter.title}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(0.85f)
            )
        }
    }
}

@Composable
private fun AnimatedStarsRow(stars: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        (1..3).forEach { i ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { delay(i * 300L); visible = true }

            val scale by animateFloatAsState(
                targetValue = if (visible) 1f else 0f,
                animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMedium),
                label = "starScale$i"
            )
            val infiniteTransition = rememberInfiniteTransition(label = "starGlow$i")
            val glow by infiniteTransition.animateFloat(
                initialValue = 0.9f, targetValue = 1.1f,
                animationSpec = infiniteRepeatable(tween(1200 + i * 200, easing = EaseInOutSine), RepeatMode.Reverse),
                label = "starGlow${i}Anim"
            )

            val isEarned = i <= stars
            Box(
                modifier = Modifier
                    .size(if (i == 2) 70.dp else 56.dp)
                    .scale(scale * if (isEarned) glow else 1f),
                contentAlignment = Alignment.Center
            ) {
                if (isEarned) {
                    // Glow behind earned star
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(StarGold.copy(alpha = 0.3f), size.minDimension * 0.55f, center)
                    }
                }
                Icon(
                    imageVector = if (isEarned) Icons.Default.Star else Icons.Outlined.StarOutline,
                    contentDescription = null,
                    tint = if (isEarned) StarGold else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(if (i == 2) 56.dp else 44.dp)
                )
            }
        }
    }
}

@Composable
private fun ScorePanel(correctCount: Int, totalQuizzes: Int, stars: Int) {
    val accuracy = if (totalQuizzes > 0) correctCount.toFloat() / totalQuizzes else 1f
    val animAccuracy by animateFloatAsState(
        targetValue = accuracy,
        animationSpec = tween(1200, easing = EaseOutBack),
        label = "accuracyAnim"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E2A3A)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("📊 Hasil Kuis", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Spacer(Modifier.height(16.dp))

            // Score display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$correctCount / $totalQuizzes",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CorrectGreen
                    )
                    Text("Jawaban Benar", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.6f))
                }

                // Circular accuracy
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 8f
                        drawArc(Color.White.copy(0.1f), 0f, 360f, false, style = Stroke(strokeW))
                        drawArc(
                            brush = Brush.sweepGradient(listOf(CorrectGreen, Color(0xFF00E5A0))),
                            startAngle = -90f,
                            sweepAngle = animAccuracy * 360f,
                            useCenter = false,
                            style = Stroke(strokeW, cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        "${(accuracy * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                // Achievement label
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        when (stars) {
                            3 -> "⭐⭐⭐\nPerfect!"
                            2 -> "⭐⭐\nHebat!"
                            1 -> "⭐\nBagus!"
                            else -> "💪\nCoba Lagi"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (stars > 0) StarGold else WrongRed,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VocabGallery(vocabWords: List<VocabWord>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF1E2A3A)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("📚", fontSize = 20.sp)
                Text(
                    "Kata yang Dipelajari",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(14.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                vocabWords.forEachIndexed { idx, word ->
                    AnimatedVocabChip(word = word, index = idx)
                }
            }
        }
    }
}

@Composable
private fun AnimatedVocabChip(word: VocabWord, index: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 100L + 300L); visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "chipScale$index"
    )

    Surface(
        modifier = Modifier.scale(scale),
        shape = RoundedCornerShape(50),
        color = Color(0xFF2A3A50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                word.english,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF81D4FA)
            )
            Text(
                "=",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(0.4f)
            )
            Text(
                word.indonesian,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFA5D6A7),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

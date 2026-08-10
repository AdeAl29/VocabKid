package com.example.vocabkid.presentation.pronunciation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutBack
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─── Colors ──────────────────────────────────────────────────────────────────
private val CorrectGreen  = Color(0xFF00C896)
private val IncorrectRed  = Color(0xFFFF5252)
private val GoldStreak    = Color(0xFFFFD700)
private val MicActiveRed  = Color(0xFFFF3D71)
private val DeepBg        = Color(0xFF080D1A)
private val CardBg        = Color(0xFF111827)
private val AccentPurple  = Color(0xFF7C3AED)
private val AccentCyan    = Color(0xFF06B6D4)

// ─── Category → canvas illustration mapper ───────────────────────────────────
private fun categoryColor(category: String): Pair<Color, Color> = when (category.lowercase()) {
    "animals"  -> Pair(Color(0xFF059669), Color(0xFF34D399))
    "fruits"   -> Pair(Color(0xFFDC2626), Color(0xFFFB7185))
    "vegetables" -> Pair(Color(0xFF16A34A), Color(0xFF86EFAC))
    "food", "food & drink" -> Pair(Color(0xFFD97706), Color(0xFFFBBF24))
    "school"   -> Pair(Color(0xFF2563EB), Color(0xFF60A5FA))
    "family"   -> Pair(Color(0xFFDB2777), Color(0xFFF472B6))
    "colors"   -> Pair(Color(0xFF7C3AED), Color(0xFFA78BFA))
    "body"     -> Pair(Color(0xFFEA580C), Color(0xFFFB923C))
    "home"     -> Pair(Color(0xFF0891B2), Color(0xFF22D3EE))
    "nature"   -> Pair(Color(0xFF15803D), Color(0xFF4ADE80))
    "transportation" -> Pair(Color(0xFF1D4ED8), Color(0xFF93C5FD))
    "places"   -> Pair(Color(0xFF9333EA), Color(0xFFC084FC))
    else       -> Pair(Color(0xFF4F46E5), Color(0xFF818CF8))
}

// ─── Root Composable ─────────────────────────────────────────────────────────
@Composable
fun PronunciationScreen(
    viewModel: PronunciationViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasPermission = isGranted }

    // TTS
    var ttsEngine by remember { mutableStateOf<android.speech.tts.TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    DisposableEffect(Unit) {
        var engine: android.speech.tts.TextToSpeech? = null
        engine = android.speech.tts.TextToSpeech(context.applicationContext) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                val result = engine?.setLanguage(java.util.Locale.US)
                ttsReady = result != android.speech.tts.TextToSpeech.LANG_MISSING_DATA &&
                        result != android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
                engine?.setSpeechRate(0.85f)
            }
        }
        ttsEngine = engine
        onDispose { engine.stop(); engine.shutdown(); ttsEngine = null; ttsReady = false }
    }
    val speakWord = { word: String ->
        if (ttsReady) ttsEngine?.speak(word, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, "pronounce_$word")
    }

    // Speech Recognizer
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) SpeechRecognizer.createSpeechRecognizer(context)
        else null
    }
    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { viewModel.setListeningState(false) }
            override fun onError(error: Int) { viewModel.onSpeechError() }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) viewModel.onSpeechResult(matches[0])
                else viewModel.onSpeechError()
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) viewModel.onSpeechPartialResult(matches[0])
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer?.setRecognitionListener(listener)
        onDispose { speechRecognizer?.destroy() }
    }
    val startListening = {
        if (hasPermission && speechRecognizer != null) {
            viewModel.setListeningState(true)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
            }
            speechRecognizer.startListening(intent)
        } else if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    val stopListening = { speechRecognizer?.stopListening(); viewModel.setListeningState(false) }

    // Full-screen dark themed layout
    Box(modifier = Modifier.fillMaxSize().background(DeepBg)) {
        // Animated cosmic background
        PronunciationBackground()

        when {
            state.isLoading -> PronunciationLoadingScreen()
            state.currentWord == null -> PronunciationEmptyScreen(onBackClick)
            else -> PronunciationMainContent(
                state = state,
                hasPermission = hasPermission,
                ttsReady = ttsReady,
                onBackClick = onBackClick,
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                onStartListening = startListening,
                onStopListening = stopListening,
                onNextWord = { viewModel.nextWord() },
                onRepeatWord = { viewModel.repeatWord() },
                onSpeakWord = speakWord
            )
        }
    }
}

// ─── Animated Background ─────────────────────────────────────────────────────
@Composable
private fun PronunciationBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "pronBg")
    val t by infiniteTransition.animateFloat(
        0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(18000, easing = LinearEasing)), label = "pronBgT"
    )
    val particles = remember { List(35) { Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width; val h = size.height

        // Deep space gradient
        drawRect(brush = Brush.verticalGradient(
            listOf(Color(0xFF080D1A), Color(0xFF0F1729), Color(0xFF080D1A)), 0f, h
        ))

        // Aurora blobs
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x337C3AED), Color.Transparent), Offset(w * 0.15f, h * 0.2f), w * 0.55f),
            radius = w * 0.55f,
            center = Offset(w * 0.15f + sin(t.toDouble()).toFloat() * 20f, h * 0.2f + cos((t * 0.7f).toDouble()).toFloat() * 12f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x2206B6D4), Color.Transparent), Offset(w * 0.85f, h * 0.5f), w * 0.45f),
            radius = w * 0.45f,
            center = Offset(w * 0.85f + cos((t * 0.8f).toDouble()).toFloat() * 18f, h * 0.5f + sin(t.toDouble()).toFloat() * 10f)
        )
        drawCircle(
            brush = Brush.radialGradient(listOf(Color(0x22FF3D71), Color.Transparent), Offset(w * 0.5f, h * 0.8f), w * 0.4f),
            radius = w * 0.4f,
            center = Offset(w * 0.5f, h * 0.8f + sin((t * 0.5f).toDouble()).toFloat() * 15f)
        )

        // Stars
        particles.forEachIndexed { i, (px, py, phase) ->
            val alpha = (sin((t + phase * 5f).toDouble()).toFloat() * 0.35f + 0.45f)
            val yDrift = sin((t * 0.3f + phase * 2f).toDouble()).toFloat() * h * 0.01f
            drawCircle(Color.White.copy(alpha = alpha), if (i % 4 == 0) 2.2f else 1.2f, Offset(w * px, h * py + yDrift))
        }

        // Sound wave decoration at bottom
        repeat(3) { wave ->
            val waveY = h * (0.88f + wave * 0.04f)
            val waveAmp = h * 0.01f * (3 - wave)
            val waveAlpha = (0.06f - wave * 0.018f)
            drawPath(path = Path().apply {
                moveTo(0f, waveY)
                var x = 0f
                while (x <= w) {
                    val y = waveY + sin((x / w * 4f * PI + t + wave * 0.8f).toFloat()).toFloat() * waveAmp
                    lineTo(x, y)
                    x += 4f
                }
                lineTo(w, h); lineTo(0f, h); close()
            }, color = AccentCyan.copy(alpha = waveAlpha))
        }
    }
}

// ─── Loading Screen ───────────────────────────────────────────────────────────
@Composable
private fun PronunciationLoadingScreen() {
    val infiniteTransition = rememberInfiniteTransition(label = "loadingAnim")
    val pulseScale by infiniteTransition.animateFloat(
        0.9f, 1.1f,
        infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "loadingPulse"
    )
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(
                modifier = Modifier.size(90.dp).scale(pulseScale)
                    .background(AccentPurple.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(44.dp), strokeWidth = 3.dp)
            }
            Text("Memuat kata...", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(0.7f))
        }
    }
}

// ─── Empty State Screen ───────────────────────────────────────────────────────
@Composable
private fun PronunciationEmptyScreen(onBackClick: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("📚", fontSize = 64.sp)
            Text("Belum ada kata\nuntuk dipelajari", style = MaterialTheme.typography.titleLarge, color = Color.White, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text("Tambah kosakata di menu Study dulu!", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.5f), textAlign = TextAlign.Center)
            Button(onClick = onBackClick, shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)) {
                Text("Kembali", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─── Main Content ─────────────────────────────────────────────────────────────
@Composable
private fun PronunciationMainContent(
    state: PronunciationUiState,
    hasPermission: Boolean,
    ttsReady: Boolean,
    onBackClick: () -> Unit,
    onRequestPermission: () -> Unit,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onNextWord: () -> Unit,
    onRepeatWord: () -> Unit,
    onSpeakWord: (String) -> Unit
) {
    val word = state.currentWord?.word ?: return
    val (gradStart, gradEnd) = categoryColor(word.category)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Cinematic Word Hero Header ────────────────────────────────────────
        WordHeroHeader(
            englishWord = word.englishWord,
            category = word.category,
            gradStart = gradStart,
            gradEnd = gradEnd,
            wordIndex = state.wordIndex,
            totalWords = state.totalWords,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Stats Row ─────────────────────────────────────────────────────
            PremiumStatsRow(state = state)

            // ── Word Pronunciation Card ───────────────────────────────────────
            AnimatedContent(
                targetState = word.englishWord,
                transitionSpec = {
                    (fadeIn(tween(400)) + scaleIn(tween(400), 0.92f))
                        .togetherWith(fadeOut(tween(250)) + scaleOut(tween(250), 1.06f))
                },
                label = "wordCardTransition"
            ) { currentWord ->
                PremiumWordCard(
                    englishWord = currentWord,
                    indonesianMeaning = word.indonesianMeaning,
                    ttsReady = ttsReady,
                    result = state.result,
                    gradStart = gradStart,
                    gradEnd = gradEnd,
                    onSpeak = { onSpeakWord(currentWord) }
                )
            }

            // ── Result Feedback ───────────────────────────────────────────────
            PremiumResultBanner(state = state)

            // ── Waveform when listening ───────────────────────────────────────
            AnimatedVisibility(
                visible = state.isListening,
                enter = fadeIn(tween(250)) + scaleIn(tween(250)),
                exit = fadeOut(tween(200)) + scaleOut(tween(200))
            ) {
                PremiumWaveform(gradStart = gradStart, gradEnd = gradEnd)
            }

            // ── Microphone Area ───────────────────────────────────────────────
            if (!hasPermission) {
                PermissionRequestCard(onRequestPermission = onRequestPermission)
            } else {
                PremiumMicArea(
                    state = state,
                    gradStart = gradStart,
                    gradEnd = gradEnd,
                    onStartListening = onStartListening,
                    onStopListening = onStopListening,
                    onNextWord = onNextWord,
                    onRepeatWord = onRepeatWord
                )
            }

            // ── Phonetic tips ─────────────────────────────────────────────────
            PhoneticTipsCard(word = word.englishWord)

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ─── Word Hero Header with Canvas Illustration ───────────────────────────────
@Composable
private fun WordHeroHeader(
    @Suppress("UNUSED_PARAMETER") englishWord: String,
    category: String,
    gradStart: Color,
    gradEnd: Color,
    wordIndex: Int,
    totalWords: Int,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "heroAnim")
    val t by infiniteTransition.animateFloat(
        0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(8000, easing = LinearEasing)), label = "heroT"
    )

    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
        // Canvas illustration
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height

            // Gradient base
            drawRect(brush = Brush.linearGradient(listOf(
                gradStart.copy(alpha = 0.9f), gradEnd.copy(alpha = 0.7f),
                DeepBg
            ), Offset(0f, 0f), Offset(w, h)))

            // Concentric sound rings (visual metaphor for pronunciation)
            val center = Offset(w * 0.5f, h * 0.5f)
            repeat(5) { i ->
                val progress = ((t / (2 * PI).toFloat() + i * 0.2f) % 1f)
                val radius = w * 0.15f + progress * w * 0.45f
                val alpha = (1f - progress) * 0.35f
                drawCircle(gradEnd.copy(alpha = alpha), radius, center, style = Stroke(3f - progress * 2f))
            }

            // Mic icon canvas illustration
            val micCenter = Offset(w * 0.5f, h * 0.48f)
            val micSize = h * 0.28f
            // Mic body
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(Color.White.copy(0.95f), Color.White.copy(0.7f)),
                    micCenter.y - micSize * 0.5f, micCenter.y + micSize * 0.4f),
                topLeft = Offset(micCenter.x - micSize * 0.22f, micCenter.y - micSize * 0.5f),
                size = Size(micSize * 0.44f, micSize * 0.9f),
                cornerRadius = CornerRadius(micSize * 0.22f, micSize * 0.22f)
            )
            // Mic grill lines
            repeat(4) { i ->
                val lineY = micCenter.y - micSize * 0.2f + i * micSize * 0.13f
                drawLine(
                    gradStart.copy(0.3f),
                    Offset(micCenter.x - micSize * 0.12f, lineY),
                    Offset(micCenter.x + micSize * 0.12f, lineY),
                    2.5f, cap = StrokeCap.Round
                )
            }
            // Stand / neck
            drawLine(Color.White.copy(0.85f), Offset(micCenter.x, micCenter.y + micSize * 0.4f),
                Offset(micCenter.x, micCenter.y + micSize * 0.6f), micSize * 0.06f, cap = StrokeCap.Round)
            // Base
            drawLine(Color.White.copy(0.85f),
                Offset(micCenter.x - micSize * 0.22f, micCenter.y + micSize * 0.6f),
                Offset(micCenter.x + micSize * 0.22f, micCenter.y + micSize * 0.6f),
                micSize * 0.06f, cap = StrokeCap.Round)
            // Curved arm around mic
            drawPath(path = Path().apply {
                moveTo(micCenter.x - micSize * 0.35f, micCenter.y + micSize * 0.1f)
                cubicTo(micCenter.x - micSize * 0.6f, micCenter.y + micSize * 0.1f,
                    micCenter.x - micSize * 0.6f, micCenter.y - micSize * 0.5f,
                    micCenter.x - micSize * 0.35f, micCenter.y - micSize * 0.5f)
            }, color = Color.White.copy(0.5f), style = Stroke(micSize * 0.045f, cap = StrokeCap.Round))
            drawPath(path = Path().apply {
                moveTo(micCenter.x + micSize * 0.35f, micCenter.y + micSize * 0.1f)
                cubicTo(micCenter.x + micSize * 0.6f, micCenter.y + micSize * 0.1f,
                    micCenter.x + micSize * 0.6f, micCenter.y - micSize * 0.5f,
                    micCenter.x + micSize * 0.35f, micCenter.y - micSize * 0.5f)
            }, color = Color.White.copy(0.5f), style = Stroke(micSize * 0.045f, cap = StrokeCap.Round))

            // Floating music notes / sound particles
            listOf(
                Triple(0.15f, 0.3f, 0f), Triple(0.78f, 0.35f, 1.5f),
                Triple(0.08f, 0.6f, 2.8f), Triple(0.88f, 0.55f, 0.9f)
            ).forEach { (xFrac, yFrac, phase) ->
                val noteX = w * xFrac + sin((t + phase).toDouble()).toFloat() * 12f
                val noteY = h * yFrac + cos((t * 0.8f + phase).toDouble()).toFloat() * 10f
                val alpha = (sin((t * 1.5f + phase).toDouble()).toFloat() * 0.35f + 0.55f)
                // Note head
                drawCircle(Color.White.copy(alpha), 7f, Offset(noteX, noteY))
                // Note stem
                drawLine(Color.White.copy(alpha * 0.8f),
                    Offset(noteX + 6f, noteY), Offset(noteX + 6f, noteY - 18f), 2.5f, cap = StrokeCap.Round)
                // Flag
                drawPath(path = Path().apply {
                    moveTo(noteX + 6f, noteY - 18f)
                    cubicTo(noteX + 14f, noteY - 14f, noteX + 14f, noteY - 10f, noteX + 6f, noteY - 8f)
                }, color = Color.White.copy(alpha * 0.7f), style = Stroke(2f, cap = StrokeCap.Round))
            }

            // Top & bottom fade
            drawRect(brush = Brush.verticalGradient(listOf(DeepBg.copy(0.9f), Color.Transparent), 0f, h * 0.22f))
            drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, DeepBg), h * 0.75f, h),
                topLeft = Offset(0f, h * 0.75f), size = Size(w, h * 0.25f))
        }

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.padding(12.dp).size(42.dp)
                .background(Color.Black.copy(0.4f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }

        // Progress indicator top-right
        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(14.dp)
                .background(Color.Black.copy(0.4f), RoundedCornerShape(14.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$wordIndex / $totalWords",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("kata", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.6f))
        }

        // Bottom labels
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎤 Latihan Pengucapan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White)
            Text(category.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(0.65f),
                letterSpacing = 2.sp)
        }
    }
}

// ─── Premium Stats Row ────────────────────────────────────────────────────────
@Composable
private fun PremiumStatsRow(state: PronunciationUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Accuracy
        val accuracy = if (state.sessionTotal > 0)
            (state.sessionCorrect * 100 / state.sessionTotal) else 0
        StatChip(
            icon = "✅",
            value = "${state.sessionCorrect}/${state.sessionTotal}",
            label = "Benar",
            bgColor = CorrectGreen.copy(0.15f),
            accentColor = CorrectGreen,
            modifier = Modifier.weight(1f)
        )
        StatChip(
            icon = "📊",
            value = "$accuracy%",
            label = "Akurasi",
            bgColor = AccentCyan.copy(0.12f),
            accentColor = AccentCyan,
            modifier = Modifier.weight(1f)
        )
        // Streak (animated if active)
        val streakScale by animateFloatAsState(
            targetValue = if (state.streak >= 2) 1f else 0f,
            animationSpec = spring(Spring.DampingRatioMediumBouncy),
            label = "streakScale"
        )
        if (state.streak >= 2) {
            StatChip(
                icon = "🔥",
                value = "${state.streak}",
                label = "Streak",
                bgColor = GoldStreak.copy(0.15f),
                accentColor = GoldStreak,
                modifier = Modifier.weight(1f).scale(streakScale)
            )
        }
    }
}

@Composable
private fun StatChip(
    icon: String, value: String, label: String,
    @Suppress("UNUSED_PARAMETER") bgColor: Color, accentColor: Color, modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = CardBg) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(icon, fontSize = 16.sp)
            Text(value, style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold, color = accentColor)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(0.5f))
        }
    }
}

// ─── Premium Word Card ────────────────────────────────────────────────────────
@Composable
private fun PremiumWordCard(
    englishWord: String,
    indonesianMeaning: String,
    ttsReady: Boolean,
    result: PronunciationResult,
    gradStart: Color,
    gradEnd: Color,
    onSpeak: () -> Unit
) {
    var isSpeaking by remember { mutableStateOf(false) }
    val speakerScale by animateFloatAsState(
        targetValue = if (isSpeaking) 1.3f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "speakerScale",
        finishedListener = { isSpeaking = false }
    )

    val borderColor = when (result) {
        PronunciationResult.CORRECT -> CorrectGreen
        PronunciationResult.INCORRECT -> IncorrectRed
        else -> gradStart.copy(alpha = 0.35f)
    }
    val cardBorderWidth = if (result != PronunciationResult.IDLE) 2.dp else 1.dp

    Surface(
        modifier = Modifier.fillMaxWidth().border(cardBorderWidth, borderColor, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = CardBg
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            // Result icon floating above word
            AnimatedVisibility(
                visible = result != PronunciationResult.IDLE,
                enter = scaleIn(spring(Spring.DampingRatioLowBouncy)) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                val emoji = if (result == PronunciationResult.CORRECT) "✅" else "❌"
                Text(emoji, fontSize = 36.sp, modifier = Modifier.padding(bottom = 8.dp))
            }

            // Gradient accent line
            Box(modifier = Modifier.width(60.dp).height(3.dp).clip(RoundedCornerShape(2.dp))
                .background(Brush.horizontalGradient(listOf(gradStart, gradEnd))))
            Spacer(Modifier.height(14.dp))

            // Word
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = englishWord,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                // Animated TTS button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(speakerScale)
                        .background(gradStart.copy(0.2f), CircleShape)
                        .clickable(enabled = ttsReady) { isSpeaking = true; onSpeak() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, null,
                        tint = gradStart, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth(0.5f).height(1.dp)
                .background(Color.White.copy(0.1f)))
            Spacer(Modifier.height(10.dp))

            // Indonesian meaning
            Text(
                text = indonesianMeaning,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(0.65f),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "🎙 Ucapkan kata di atas dengan jelas",
                style = MaterialTheme.typography.bodySmall,
                color = gradEnd.copy(0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─── Premium Result Banner ────────────────────────────────────────────────────
@Composable
private fun PremiumResultBanner(state: PronunciationUiState) {
    AnimatedContent(
        targetState = Triple(state.result, state.recognizedText, state.streak),
        transitionSpec = {
            (fadeIn(tween(300)) + scaleIn(tween(300, easing = EaseOutBack), 0.85f))
                .togetherWith(fadeOut(tween(150)))
        },
        label = "resultBanner"
    ) { (result, recognizedText, streak) ->
        when (result) {
            PronunciationResult.CORRECT -> {
                // Shake anim on correct (celebration bounce)
                val bounceAnim = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    repeat(2) {
                        bounceAnim.animateTo(-6f, tween(80))
                        bounceAnim.animateTo(6f, tween(80))
                    }
                    bounceAnim.animateTo(0f, tween(100))
                }
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .offset { IntOffset(0, bounceAnim.value.toInt()) }
                        .border(1.5.dp, CorrectGreen.copy(0.5f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = CorrectGreen.copy(0.12f)
                ) {
                    Row(modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(46.dp).background(CorrectGreen.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center) {
                            Text("✅", fontSize = 22.sp)
                        }
                        Column {
                            Text(
                                if (streak >= 3) "Mantap! 🔥 $streak streak berturut!" else "Benar! Pengucapan bagus!",
                                fontWeight = FontWeight.ExtraBold, color = CorrectGreen,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (recognizedText.isNotEmpty()) {
                                Text("Kamu bilang: \"$recognizedText\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CorrectGreen.copy(0.75f))
                            }
                        }
                    }
                }
            }
            PronunciationResult.INCORRECT -> {
                // Shake on incorrect
                val shakeAnim = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    repeat(4) {
                        shakeAnim.animateTo(10f, tween(60))
                        shakeAnim.animateTo(-10f, tween(60))
                    }
                    shakeAnim.animateTo(0f, tween(80))
                }
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .offset { IntOffset(shakeAnim.value.toInt(), 0) }
                        .border(1.5.dp, IncorrectRed.copy(0.45f), RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    color = IncorrectRed.copy(0.1f)
                ) {
                    Row(modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.size(46.dp).background(IncorrectRed.copy(0.18f), CircleShape),
                            contentAlignment = Alignment.Center) {
                            Text("🤔", fontSize = 22.sp)
                        }
                        Column {
                            Text("Hampir! Coba lagi ya 💪",
                                fontWeight = FontWeight.ExtraBold, color = IncorrectRed,
                                style = MaterialTheme.typography.titleSmall)
                            if (recognizedText.isNotEmpty() && recognizedText != "Coba lagi...") {
                                Text("Kamu bilang: \"$recognizedText\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = IncorrectRed.copy(0.75f))
                            }
                        }
                    }
                }
            }
            PronunciationResult.IDLE -> {
                if (state.isListening && state.recognizedText.isNotEmpty()) {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                        color = AccentPurple.copy(0.12f)) {
                        Text("\"${state.recognizedText}\"",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(0.8f), textAlign = TextAlign.Center,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                } else if (!state.isListening) {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                        color = CardBg) {
                        Text("Tekan 🎙 dan ucapkan kata tersebut",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(0.4f), textAlign = TextAlign.Center)
                    }
                } else {
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
                        color = AccentPurple.copy(0.1f)) {
                        val dots = rememberInfiniteTransition(label = "dotsAnim")
                        val dotPhase by dots.animateFloat(0f, 3f,
                            infiniteRepeatable(tween(900, easing = LinearEasing)), label = "dotPhase")
                        val dotStr = ".".repeat(dotPhase.toInt() + 1)
                        Text("Mendengarkan$dotStr",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = AccentPurple, textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ─── Premium Waveform Visualizer ─────────────────────────────────────────────
@Composable
private fun PremiumWaveform(gradStart: Color, gradEnd: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val barCount = 24
    val bars = List(barCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(300 + i * 40, easing = FastOutSlowInEasing),
                RepeatMode.Reverse
            ),
            label = "bar_$i"
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
            val barWidth = size.width / (barCount * 2f - 1f)
            val maxH = size.height * 0.88f
            bars.forEachIndexed { idx, heightFraction ->
                val h = maxH * heightFraction.value
                val x = idx * (barWidth * 2f) + barWidth / 2f
                val frac = idx.toFloat() / barCount
                val barColor = androidx.compose.ui.graphics.lerp(gradStart, gradEnd, frac)
                drawLine(
                    brush = Brush.verticalGradient(
                        listOf(barColor, barColor.copy(alpha = 0.3f)),
                        (size.height - h) / 2f, (size.height + h) / 2f
                    ),
                    start = Offset(x, (size.height - h) / 2f),
                    end = Offset(x, (size.height + h) / 2f),
                    strokeWidth = barWidth * 0.75f,
                    cap = StrokeCap.Round
                )
            }
        }
        // Listening label
        Text("🎤 Mendengarkan...",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(0.5f),
            modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp))
    }
}

// ─── Premium Mic Area ─────────────────────────────────────────────────────────
@Composable
private fun PremiumMicArea(
    state: PronunciationUiState,
    gradStart: Color,
    gradEnd: Color,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onNextWord: () -> Unit,
    onRepeatWord: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val pulseScale by infiniteTransition.animateFloat(
        1f, if (state.isListening) 1.22f else 1f,
        infiniteRepeatable(tween(650, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "micPulse"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        0.2f, if (state.isListening) 0.55f else 0f,
        infiniteRepeatable(tween(650, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    val micColor = if (state.isListening) MicActiveRed else gradStart
    val micIcon  = if (state.isListening) Icons.Default.MicOff else Icons.Default.Mic

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mic button with rings
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
            // Outermost ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = Offset(size.width / 2, size.height / 2)
                // Pulsing rings
                repeat(3) { i ->
                    val ringRadius = size.minDimension * (0.38f + i * 0.08f) * pulseScale
                    val ringAlpha = pulseAlpha * (1f - i * 0.28f)
                    drawCircle(micColor.copy(alpha = ringAlpha), ringRadius, c, style = Stroke(3f - i))
                }
                // Glow fill
                drawCircle(micColor.copy(alpha = pulseAlpha * 0.4f), size.minDimension * 0.4f * pulseScale, c)
            }
            // Actual button
            Button(
                onClick = { if (state.isListening) onStopListening() else onStartListening() },
                modifier = Modifier.size(86.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = micColor),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
            ) {
                Icon(micIcon, null, modifier = Modifier.size(40.dp), tint = Color.White)
            }
        }

        // Label
        Text(
            if (state.isListening) "Sentuh untuk berhenti" else "Sentuh & ucapkan",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(0.55f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Action buttons after result
        AnimatedVisibility(
            visible = state.result != PronunciationResult.IDLE,
            enter = fadeIn(tween(300)) + slideInVertically(tween(300, easing = EaseOutBack)) { it / 2 },
            exit = fadeOut(tween(200)) + slideOutVertically { it / 2 }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Repeat
                Surface(
                    modifier = Modifier.clickable { onRepeatWord() },
                    shape = RoundedCornerShape(16.dp),
                    color = CardBg
                ) {
                    Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Refresh, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(18.dp))
                        Text("Ulangi", color = Color.White.copy(0.7f), fontWeight = FontWeight.Bold)
                    }
                }
                // Next
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (state.result == PronunciationResult.CORRECT)
                                Brush.horizontalGradient(listOf(CorrectGreen, Color(0xFF00E5A0)))
                            else
                                Brush.horizontalGradient(listOf(gradStart, gradEnd))
                        )
                        .clickable { onNextWord() }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (state.result == PronunciationResult.CORRECT) "Lanjut" else "Lewati",
                            color = Color.White, fontWeight = FontWeight.ExtraBold
                        )
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ─── Permission Request Card ──────────────────────────────────────────────────
@Composable
private fun PermissionRequestCard(onRequestPermission: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onRequestPermission() },
        shape = RoundedCornerShape(20.dp),
        color = AccentPurple.copy(0.15f)
    ) {
        Row(modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(Modifier.size(52.dp).background(AccentPurple.copy(0.25f), CircleShape),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Mic, null, tint = AccentPurple, modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Butuh Izin Mikrofon", fontWeight = FontWeight.ExtraBold,
                    color = Color.White, style = MaterialTheme.typography.titleSmall)
                Text("Ketuk untuk memberi izin akses mikrofon",
                    style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.6f))
            }
        }
    }
}

// ─── Phonetic Tips Card ───────────────────────────────────────────────────────
@Composable
private fun PhoneticTipsCard(word: String) {
    val tips = phoneticTip(word)
    if (tips.isEmpty()) return

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = CardBg) {
        Row(modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(36.dp).background(AccentCyan.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center) {
                Text("💡", fontSize = 16.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Tips Pengucapan", style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold, color = AccentCyan)
                Text(tips, style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(0.7f), lineHeight = 20.sp)
            }
        }
    }
}

private fun phoneticTip(word: String): String = when (word.lowercase().trim()) {
    "cat"      -> "Ucapkan: /kæt/ — 'k-æ-t', 'a' seperti suara 'é' pendek."
    "dog"      -> "Ucapkan: /dɔːɡ/ — 'do-g', 'o' panjang seperti 'oh'."
    "bird"     -> "Ucapkan: /bɜːrd/ — 'b-erd', 'ir' seperti suara 'er'."
    "apple"    -> "Ucapkan: /ˈæpl/ — 'æ-pəl', 'a' seperti 'é' pendek, akhiran ringan."
    "banana"   -> "Ucapkan: /bəˈnɑːnə/ — 'bə-NA-nə', tekanan di suku kata ke-2."
    "orange"   -> "Ucapkan: /ˈɒrɪndʒ/ — 'O-rinj', 'ge' dibaca 'j'."
    "water"    -> "Ucapkan: /ˈwɔːtər/ — 'WO-ter', 'a' panjang."
    "school"   -> "Ucapkan: /skuːl/ — 'skool', 'oo' panjang."
    "book"     -> "Ucapkan: /bʊk/ — 'buk', 'oo' pendek seperti 'u'."
    "house"    -> "Ucapkan: /haʊs/ — 'haws', 'ou' seperti 'au'."
    "tree"     -> "Ucapkan: /triː/ — 'tree', 'ee' panjang."
    "flower"   -> "Ucapkan: /ˈflaʊər/ — 'FLOW-er', 'ow' seperti 'au'."
    "elephant" -> "Ucapkan: /ˈelɪfənt/ — 'EL-ə-fənt', 3 suku kata, tekanan di depan."
    "chicken"  -> "Ucapkan: /ˈtʃɪkɪn/ — 'CHIK-in', 'ch' seperti 'c' dalam 'coklat'."
    "bread"    -> "Ucapkan: /bred/ — 'bred', 'ea' dibaca 'e' pendek."
    else       -> ""
}

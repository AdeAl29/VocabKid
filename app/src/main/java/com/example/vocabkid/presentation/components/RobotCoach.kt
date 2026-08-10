package com.example.vocabkid.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private enum class RobotPersona {
    ENGLISH,
    INDONESIA
}

private data class RobotPalette(
    val accent: Color,
    val accentDark: Color,
    val accentLight: Color,
    val antenna: Color
)

private fun robotPaletteFor(persona: RobotPersona): RobotPalette {
    return when (persona) {
        RobotPersona.ENGLISH -> RobotPalette(
            accent = Color(0xFF0D8CFF),
            accentDark = Color(0xFF005DB8),
            accentLight = Color(0xFF88F0FF),
            antenna = Color(0xFFFFC928)
        )
        RobotPersona.INDONESIA -> RobotPalette(
            accent = Color(0xFF2EB872),
            accentDark = Color(0xFF087A45),
            accentLight = Color(0xFF9CF5C9),
            antenna = Color(0xFFFFC928)
        )
    }
}

@Composable
fun RobotCoach(
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onPronunciationClick: () -> Unit,
    onStoryClick: () -> Unit,
    onConversationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var persona by remember { mutableStateOf(RobotPersona.ENGLISH) }
    val messages = when (persona) {
        RobotPersona.ENGLISH -> englishRobotMessages
        RobotPersona.INDONESIA -> indonesianRobotMessages
    }
    var messageIndex by remember { mutableStateOf(Random.nextInt(messages.size)) }
    var showBubble by remember { mutableStateOf(false) }
    var isMenuOpen by remember { mutableStateOf(false) }
    val transition = rememberInfiniteTransition(label = "robotCoachIdle")
    val bobOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "robotCoachBob"
    )

    LaunchedEffect(persona) {
        messageIndex = Random.nextInt(messages.size)
    }

    LaunchedEffect(isMenuOpen, persona) {
        if (isMenuOpen) {
            showBubble = false
            return@LaunchedEffect
        }

        delay(900)
        showBubble = true
        while (true) {
            delay(4300)
            showBubble = false
            delay(2600)
            messageIndex = (messageIndex + Random.nextInt(1, messages.size)) %
                messages.size
            showBubble = true
        }
    }

    Row(
        modifier = modifier.widthIn(max = 282.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        AnimatedVisibility(
            visible = showBubble && !isMenuOpen,
            enter = fadeIn(animationSpec = tween(180)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(180)),
            exit = fadeOut(animationSpec = tween(140)) +
                scaleOut(targetScale = 0.92f, animationSpec = tween(140))
        ) {
            RobotSpeechBubble(message = messages[messageIndex])
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .offset(y = bobOffset.dp)
                .size(86.dp),
            contentAlignment = Alignment.Center
        ) {
            RobotAvatar(
                persona = persona,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { isMenuOpen = !isMenuOpen }
            )
            RobotAssistiveMenu(
                visible = isMenuOpen,
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = onToggleDarkTheme,
                onPronunciationClick = {
                    isMenuOpen = false
                    onPronunciationClick()
                },
                onStoryClick = {
                    isMenuOpen = false
                    onStoryClick()
                },
                onConversationClick = {
                    isMenuOpen = false
                    onConversationClick()
                },
                persona = persona,
                onSwitchPersona = {
                    persona = if (persona == RobotPersona.ENGLISH) {
                        RobotPersona.INDONESIA
                    } else {
                        RobotPersona.ENGLISH
                    }
                },
            )
        }
    }
}

@Composable
private fun RobotAssistiveMenu(
    visible: Boolean,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onPronunciationClick: () -> Unit,
    onStoryClick: () -> Unit,
    onConversationClick: () -> Unit,
    persona: RobotPersona,
    onSwitchPersona: () -> Unit
) {
    if (!visible) return

    val density = LocalDensity.current
    val popupOffset = with(density) {
        IntOffset(x = (-98).dp.roundToPx(), y = (-58).dp.roundToPx())
    }

    Popup(
        alignment = Alignment.TopStart,
        offset = popupOffset,
        properties = PopupProperties(
            focusable = false,
            clippingEnabled = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(130)) +
                scaleIn(initialScale = 0.62f, animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(120)) +
                scaleOut(targetScale = 0.62f, animationSpec = tween(150))
        ) {
            Box(modifier = Modifier.size(width = 158.dp, height = 202.dp)) {
                val arcCenterX = 137.dp
                val arcCenterY = 101.dp
                val arcRadius = 78.dp
                val buttonSize = 42.dp

                RobotMenuButton(
                    icon = Icons.Default.ChatBubble,
                    contentDescription = "Buka percakapan",
                    onClick = onConversationClick,
                    modifier = Modifier.arcOffset(
                        centerX = arcCenterX,
                        centerY = arcCenterY,
                        radius = arcRadius,
                        angleDegrees = 270.0,
                        buttonSize = buttonSize
                    )
                )
                RobotMenuButton(
                    icon = Icons.Default.AutoStories,
                    contentDescription = "Buka Story Mode",
                    onClick = onStoryClick,
                    modifier = Modifier.arcOffset(
                        centerX = arcCenterX,
                        centerY = arcCenterY,
                        radius = arcRadius,
                        angleDegrees = 225.0,
                        buttonSize = buttonSize
                    )
                )
                RobotMenuButton(
                    icon = Icons.Default.Mic,
                    contentDescription = "Latihan Pengucapan",
                    onClick = onPronunciationClick,
                    modifier = Modifier.arcOffset(
                        centerX = arcCenterX,
                        centerY = arcCenterY,
                        radius = arcRadius,
                        angleDegrees = 180.0,
                        buttonSize = buttonSize
                    )
                )
                RobotMenuButton(
                    icon = Icons.Default.SwapHoriz,
                    contentDescription = if (persona == RobotPersona.ENGLISH) {
                        "Ganti ke robot Bahasa Indonesia"
                    } else {
                        "Ganti ke robot English"
                    },
                    onClick = onSwitchPersona,
                    modifier = Modifier.arcOffset(
                        centerX = arcCenterX,
                        centerY = arcCenterY,
                        radius = arcRadius,
                        angleDegrees = 135.0,
                        buttonSize = buttonSize
                    )
                )
                RobotMenuButton(
                    icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = if (isDarkTheme) {
                        "Ubah ke mode terang"
                    } else {
                        "Ubah ke mode gelap"
                    },
                    onClick = onToggleDarkTheme,
                    modifier = Modifier.arcOffset(
                        centerX = arcCenterX,
                        centerY = arcCenterY,
                        radius = arcRadius,
                        angleDegrees = 90.0,
                        buttonSize = buttonSize
                    )
                )
            }
        }
    }
}

private fun Modifier.arcOffset(
    centerX: Dp,
    centerY: Dp,
    radius: Dp,
    angleDegrees: Double,
    buttonSize: Dp
): Modifier {
    val angleRadians = Math.toRadians(angleDegrees)
    val x = centerX + (radius.value * cos(angleRadians)).toFloat().dp - (buttonSize / 2)
    val y = centerY + (radius.value * sin(angleRadians)).toFloat().dp - (buttonSize / 2)
    return offset(x = x, y = y)
}

@Composable
private fun RobotMenuButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(42.dp),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shadowElevation = 4.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
private fun RobotSpeechBubble(
    message: String
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 124.dp, max = 176.dp)
                .shadow(2.dp, RoundedCornerShape(8.dp))
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .padding(top = 18.dp)
                .offset(x = (-5).dp)
                .size(10.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
private fun RobotAvatar(
    persona: RobotPersona,
    modifier: Modifier = Modifier
) {
    val palette = robotPaletteFor(persona)
    Canvas(modifier = modifier) {
        drawRobot(palette)
    }
}

private fun DrawScope.drawRobot(palette: RobotPalette) {
    val w = size.width
    val h = size.height
    val white = Color(0xFFF9FBFF)
    val darkBlue = Color(0xFF082B4D)
    val navy = Color(0xFF102D4C)
    val orange = Color(0xFFFF9F1A)
    val outline = Color(0xFFB8C5D8)

    drawLine(
        color = navy,
        start = Offset(w * 0.54f, h * 0.13f),
        end = Offset(w * 0.58f, h * 0.02f),
        strokeWidth = w * 0.055f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = palette.antenna,
        radius = w * 0.095f,
        center = Offset(w * 0.6f, h * 0.02f)
    )
    drawCircle(color = orange, radius = w * 0.065f, center = Offset(w * 0.6f, h * 0.02f))

    drawRoundRect(
        color = white,
        topLeft = Offset(w * 0.16f, h * 0.15f),
        size = Size(w * 0.68f, h * 0.43f),
        cornerRadius = CornerRadius(w * 0.19f, w * 0.19f)
    )
    drawRoundRect(
        color = outline,
        topLeft = Offset(w * 0.16f, h * 0.15f),
        size = Size(w * 0.68f, h * 0.43f),
        cornerRadius = CornerRadius(w * 0.19f, w * 0.19f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = w * 0.018f)
    )
    drawRoundRect(
        color = palette.accent,
        topLeft = Offset(w * 0.31f, h * 0.1f),
        size = Size(w * 0.38f, h * 0.12f),
        cornerRadius = CornerRadius(w * 0.09f, w * 0.09f)
    )
    drawCircle(color = palette.accent, radius = w * 0.07f, center = Offset(w * 0.15f, h * 0.36f))
    drawCircle(color = palette.accent, radius = w * 0.07f, center = Offset(w * 0.85f, h * 0.36f))
    drawCircle(color = orange, radius = w * 0.04f, center = Offset(w * 0.85f, h * 0.36f))

    drawRoundRect(
        color = darkBlue,
        topLeft = Offset(w * 0.24f, h * 0.22f),
        size = Size(w * 0.52f, h * 0.28f),
        cornerRadius = CornerRadius(w * 0.11f, w * 0.11f)
    )
    drawRoundRect(
        color = palette.accentLight,
        topLeft = Offset(w * 0.31f, h * 0.27f),
        size = Size(w * 0.12f, h * 0.03f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
    )
    drawRoundRect(
        color = palette.accentLight,
        topLeft = Offset(w * 0.58f, h * 0.27f),
        size = Size(w * 0.12f, h * 0.03f),
        cornerRadius = CornerRadius(w * 0.02f, w * 0.02f)
    )
    drawCircle(color = white, radius = w * 0.09f, center = Offset(w * 0.38f, h * 0.38f))
    drawCircle(color = white, radius = w * 0.09f, center = Offset(w * 0.63f, h * 0.38f))
    drawCircle(color = Color.Black, radius = w * 0.052f, center = Offset(w * 0.39f, h * 0.39f))
    drawCircle(color = Color.Black, radius = w * 0.052f, center = Offset(w * 0.62f, h * 0.39f))
    drawCircle(color = white, radius = w * 0.018f, center = Offset(w * 0.36f, h * 0.36f))
    drawCircle(color = white, radius = w * 0.018f, center = Offset(w * 0.59f, h * 0.36f))

    val smile = Path().apply {
        moveTo(w * 0.47f, h * 0.45f)
        quadraticBezierTo(w * 0.5f, h * 0.51f, w * 0.56f, h * 0.45f)
        quadraticBezierTo(w * 0.52f, h * 0.57f, w * 0.47f, h * 0.45f)
    }
    drawPath(color = Color(0xFFFF6B8B), path = smile)

    drawRoundRect(
        color = white,
        topLeft = Offset(w * 0.31f, h * 0.58f),
        size = Size(w * 0.38f, h * 0.29f),
        cornerRadius = CornerRadius(w * 0.12f, w * 0.12f)
    )
    drawRoundRect(
        color = palette.accent,
        topLeft = Offset(w * 0.38f, h * 0.64f),
        size = Size(w * 0.24f, h * 0.13f),
        cornerRadius = CornerRadius(w * 0.05f, w * 0.05f)
    )
    drawStar(
        center = Offset(w * 0.5f, h * 0.705f),
        outerRadius = w * 0.055f,
        innerRadius = w * 0.026f,
        color = palette.antenna
    )

    drawLine(
        color = navy,
        start = Offset(w * 0.34f, h * 0.63f),
        end = Offset(w * 0.17f, h * 0.54f),
        strokeWidth = w * 0.07f,
        cap = StrokeCap.Round
    )
    drawCircle(color = white, radius = w * 0.09f, center = Offset(w * 0.13f, h * 0.51f))
    drawLine(
        color = navy,
        start = Offset(w * 0.68f, h * 0.64f),
        end = Offset(w * 0.81f, h * 0.73f),
        strokeWidth = w * 0.07f,
        cap = StrokeCap.Round
    )
    drawCircle(color = white, radius = w * 0.085f, center = Offset(w * 0.84f, h * 0.76f))
}

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    repeat(10) { index ->
        val angle = Math.toRadians((index * 36.0) - 90.0)
        val radius = if (index % 2 == 0) outerRadius else innerRadius
        val point = Offset(
            x = center.x + (kotlin.math.cos(angle) * radius).toFloat(),
            y = center.y + (kotlin.math.sin(angle) * radius).toFloat()
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

private val englishRobotMessages = listOf(
    "Try five words first.",
    "Small practice, big memory.",
    "Say the word out loud.",
    "Review before you forget.",
    "One card at a time.",
    "Mistakes help you learn.",
    "Your brain likes repeats.",
    "Read, say, remember.",
    "Choose an easy start.",
    "A short quiz is enough.",
    "Keep your streak warm.",
    "New words need visits.",
    "Good learners retry.",
    "You are getting better.",
    "Practice makes words stick.",
    "Listen to the word.",
    "Guess before checking.",
    "Slow is still progress.",
    "Three minutes can help.",
    "Hard words need love.",
    "Review the tricky ones.",
    "Your memory is growing.",
    "Start with Animal words.",
    "Try a Food word today.",
    "Color words are fun.",
    "School words are useful.",
    "Say it like a song.",
    "A wrong answer is data.",
    "Focus on one category.",
    "Quiz time can be quick.",
    "Repeat with confidence.",
    "Read the example sentence.",
    "English grows by practice.",
    "Little steps count.",
    "You can do this.",
    "Make the word yours.",
    "Point, read, speak.",
    "Fresh words first.",
    "Old words stay strong.",
    "Try again with a smile.",
    "Learning is a journey.",
    "Memory loves timing.",
    "Five words, then rest.",
    "Strong recall starts small.",
    "Say the meaning too.",
    "Check your progress.",
    "Let difficult words return.",
    "Every review matters.",
    "Your effort is working.",
    "Today is a good day.",
    "Read slowly and clearly.",
    "Think before tapping.",
    "Match word and meaning.",
    "Use the word in a sentence.",
    "A tiny habit wins.",
    "Keep going, learner.",
    "English can be playful.",
    "One correct answer rocks.",
    "Try the speaker button.",
    "Listen, then repeat.",
    "Practice with your voice.",
    "Words become friends.",
    "Learning needs curiosity.",
    "Pick a quick session.",
    "Review today's words.",
    "Mastery takes repeats.",
    "You are building skill.",
    "Be brave with quizzes.",
    "A guess is a start.",
    "Notice similar words.",
    "Celebrate small wins.",
    "Your vocabulary is growing.",
    "Keep your pace calm.",
    "Try one more card.",
    "Remember with examples.",
    "Speak English softly.",
    "Make learning light.",
    "A little daily works.",
    "Strong words come back.",
    "Review makes recall fast.",
    "Choose the best answer.",
    "Learn with patience.",
    "Good job showing up.",
    "Your next word waits.",
    "Read the choices carefully.",
    "Think in English first.",
    "Tiny reviews are powerful.",
    "You are closer now.",
    "Practice before playing.",
    "Make mistakes friendly.",
    "Try a new category.",
    "Your memory likes rhythm.",
    "Repeat the hard sound.",
    "Words are tiny tools.",
    "Today, learn one more.",
    "Keep your mind curious.",
    "Quiz yourself kindly.",
    "Every word has a story.",
    "Start now, finish proud.",
    "Learning English is fun."
)

private val indonesianRobotMessages = listOf(
    "Ayo mulai dari lima kata.",
    "Sedikit latihan itu cukup.",
    "Ucapkan katanya pelan-pelan.",
    "Coba tebak dulu artinya.",
    "Kata sulit perlu diulang.",
    "Salah itu bagian belajar.",
    "Otak suka pengulangan.",
    "Baca, ucapkan, ingat.",
    "Mulai dari yang mudah.",
    "Kuis singkat juga oke.",
    "Jaga ritme belajarmu.",
    "Kata baru perlu disapa.",
    "Coba lagi dengan santai.",
    "Kamu makin jago.",
    "Latihan bikin kata nempel.",
    "Dengarkan bunyi katanya.",
    "Ingat dulu sebelum lihat.",
    "Pelan juga tetap maju.",
    "Tiga menit sangat berguna.",
    "Kata sulit jangan ditakuti.",
    "Latih yang sering salah.",
    "Memorimu sedang tumbuh.",
    "Coba kategori Animal.",
    "Hari ini coba Food.",
    "Warna itu seru dipelajari.",
    "Kata School sering dipakai.",
    "Ucapkan seperti bernyanyi.",
    "Jawaban salah itu petunjuk.",
    "Fokus satu kategori dulu.",
    "Kuis cepat boleh dicoba.",
    "Ulangi dengan percaya diri.",
    "Baca contoh kalimatnya.",
    "Bahasa Inggris bisa seru.",
    "Langkah kecil tetap berarti.",
    "Kamu pasti bisa.",
    "Jadikan kata itu milikmu.",
    "Lihat, baca, ucapkan.",
    "Kata baru dulu yuk.",
    "Kata lama tetap dijaga.",
    "Coba lagi sambil senyum.",
    "Belajar itu perjalanan.",
    "Waktu ulang itu penting.",
    "Lima kata lalu istirahat.",
    "Ingatan kuat dimulai kecil.",
    "Sebutkan juga artinya.",
    "Cek progresmu sebentar.",
    "Biarkan kata sulit kembali.",
    "Setiap review berarti.",
    "Usahamu bekerja.",
    "Hari ini waktu yang pas.",
    "Baca perlahan dan jelas.",
    "Pikir dulu sebelum pilih.",
    "Cocokkan kata dan arti.",
    "Buat kalimat sederhana.",
    "Kebiasaan kecil menang.",
    "Lanjut terus ya.",
    "Belajar bisa ringan.",
    "Satu jawaban benar keren.",
    "Coba tombol speaker.",
    "Dengar lalu tirukan.",
    "Latihan pakai suara.",
    "Kata bisa jadi teman.",
    "Belajar perlu rasa ingin tahu.",
    "Pilih sesi singkat.",
    "Ulangi kata hari ini.",
    "Menguasai butuh ulang.",
    "Kamu sedang membangun skill.",
    "Berani coba kuis.",
    "Menebak itu awal belajar.",
    "Perhatikan kata yang mirip.",
    "Rayakan kemajuan kecil.",
    "Kosakatamu bertambah.",
    "Jaga tempo tetap santai.",
    "Coba satu kartu lagi.",
    "Ingat lewat contoh.",
    "Ucapkan bahasa Inggrisnya.",
    "Belajar dibuat ringan saja.",
    "Sedikit tiap hari manjur.",
    "Kata kuat akan kembali.",
    "Review bikin ingatan cepat.",
    "Pilih jawaban terbaik.",
    "Belajar dengan sabar.",
    "Keren, kamu hadir lagi.",
    "Kata berikutnya menunggu.",
    "Baca pilihan dengan teliti.",
    "Pikirkan artinya dulu.",
    "Review kecil itu kuat.",
    "Kamu makin dekat.",
    "Latihan dulu sebentar.",
    "Berteman dengan kesalahan.",
    "Coba kategori baru.",
    "Memori suka irama.",
    "Ulangi bunyi yang sulit.",
    "Kata adalah alat kecil.",
    "Hari ini tambah satu kata.",
    "Tetap penasaran ya.",
    "Kuis dengan santai.",
    "Setiap kata punya cerita.",
    "Mulai sekarang, selesai bangga.",
    "Belajar Inggris itu menyenangkan."
)

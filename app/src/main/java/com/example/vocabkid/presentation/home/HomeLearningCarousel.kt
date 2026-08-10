package com.example.vocabkid.presentation.home

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HomeLearningCarousel(
    dueToday: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4_800)
            currentIndex = (currentIndex + 1) % homeLearningScenes.size
        }
    }

    val containerColor = if (isDarkTheme) {
        Color(0xE6172438)
    } else {
        Color(0xFDFEFFFE)
    }
    val titleColor = if (isDarkTheme) Color(0xFFF7FAFF) else Color(0xFF17212B)
    val meaningColor = if (isDarkTheme) Color(0xFFD8E5F0) else Color(0xFF465761)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(268.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = titleColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Crossfade(
            targetState = currentIndex,
            animationSpec = tween(durationMillis = 520),
            label = "homeLearningScene"
        ) { index ->
            val scene = homeLearningScenes[index]

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(158.dp)
                ) {
                    LearningSceneArtwork(
                        scene = scene,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxSize()
                    )

                    LearningDueBadge(
                        dueToday = dueToday,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    )

                    SceneIndicators(
                        selectedIndex = index,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = scene.englishSentence,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = titleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = scene.indonesianMeaning,
                        style = MaterialTheme.typography.bodyMedium,
                        color = meaningColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun LearningDueBadge(
    dueToday: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val badgeColor = if (isDarkTheme) Color(0xD9111A2A) else Color(0xEFFFFFFF)
    val textColor = if (isDarkTheme) Color(0xFFF7FAFF) else Color(0xFF16333D)
    val text = if (dueToday > 0) {
        "$dueToday kata siap diulang"
    } else {
        "Latihan hari ini selesai"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = badgeColor,
        contentColor = textColor,
        shadowElevation = 1.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SceneIndicators(
    selectedIndex: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        homeLearningScenes.forEachIndexed { index, _ ->
            val active = index == selectedIndex
            val color = when {
                active && isDarkTheme -> Color.White
                active -> Color(0xFF173E4B)
                isDarkTheme -> Color.White.copy(alpha = 0.34f)
                else -> Color(0xFF173E4B).copy(alpha = 0.28f)
            }
            Box(
                modifier = Modifier
                    .size(if (active) 7.dp else 5.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun LearningSceneArtwork(
    scene: LearningScene,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = if (isDarkTheme) {
                    listOf(scene.nightTop, scene.nightBottom)
                } else {
                    listOf(scene.dayTop, scene.dayBottom)
                },
                startY = 0f,
                endY = size.height
            )
        )

        when (scene.type) {
            LearningSceneType.Airplane -> drawAirplaneScene(isDarkTheme)
            LearningSceneType.SchoolBus -> drawSchoolBusScene(isDarkTheme)
            LearningSceneType.RainWindow -> drawRainWindowScene(isDarkTheme)
            LearningSceneType.BookDesk -> drawBookDeskScene(isDarkTheme)
            LearningSceneType.AppleBasket -> drawAppleBasketScene(isDarkTheme)
            LearningSceneType.KitePark -> drawKiteParkScene(isDarkTheme)
            LearningSceneType.BoatRiver -> drawBoatRiverScene(isDarkTheme)
            LearningSceneType.RocketNight -> drawRocketNightScene()
            LearningSceneType.BreakfastTable -> drawBreakfastTableScene(isDarkTheme)
            LearningSceneType.FlowerGarden -> drawFlowerGardenScene(isDarkTheme)
        }
    }
}

private fun DrawScope.drawAirplaneScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension

    if (!isDarkTheme) {
        drawSun(center = Offset(w * 0.83f, h * 0.2f), radius = minSide * 0.12f)
    } else {
        drawMoon(center = Offset(w * 0.84f, h * 0.2f), radius = minSide * 0.1f)
    }

    drawCloud(Offset(w * 0.18f, h * 0.35f), minSide / 360f, Color.White.copy(alpha = 0.82f))
    drawCloud(Offset(w * 0.62f, h * 0.22f), minSide / 460f, Color.White.copy(alpha = 0.62f))
    drawHill(top = h * 0.82f, color = if (isDarkTheme) Color(0xFF16485B) else Color(0xFF7AD881))

    val planeCenter = Offset(w * 0.52f, h * 0.47f)
    drawLine(
        color = Color.White.copy(alpha = if (isDarkTheme) 0.28f else 0.62f),
        start = Offset(planeCenter.x - w * 0.34f, planeCenter.y + minSide * 0.03f),
        end = Offset(planeCenter.x - minSide * 0.12f, planeCenter.y + minSide * 0.015f),
        strokeWidth = minSide * 0.012f,
        cap = StrokeCap.Round
    )
    drawRoundRect(
        color = Color(0xFFFFF8F0),
        topLeft = Offset(planeCenter.x - minSide * 0.22f, planeCenter.y - minSide * 0.035f),
        size = Size(minSide * 0.44f, minSide * 0.07f),
        cornerRadius = CornerRadius(minSide * 0.04f, minSide * 0.04f)
    )
    drawCircle(
        color = Color(0xFF74D9FF),
        radius = minSide * 0.018f,
        center = Offset(planeCenter.x + minSide * 0.11f, planeCenter.y - minSide * 0.002f)
    )
    drawPath(
        path = Path().apply {
            moveTo(planeCenter.x - minSide * 0.02f, planeCenter.y)
            lineTo(planeCenter.x - minSide * 0.16f, planeCenter.y + minSide * 0.13f)
            lineTo(planeCenter.x + minSide * 0.07f, planeCenter.y + minSide * 0.025f)
            close()
        },
        color = Color(0xFFFFC857)
    )
    drawPath(
        path = Path().apply {
            moveTo(planeCenter.x - minSide * 0.16f, planeCenter.y - minSide * 0.015f)
            lineTo(planeCenter.x - minSide * 0.25f, planeCenter.y - minSide * 0.11f)
            lineTo(planeCenter.x - minSide * 0.12f, planeCenter.y + minSide * 0.005f)
            close()
        },
        color = Color(0xFFEF6F6C)
    )
}

private fun DrawScope.drawSchoolBusScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawSun(center = Offset(w * 0.76f, h * 0.22f), radius = minSide * 0.1f)
    drawHill(top = h * 0.72f, color = if (isDarkTheme) Color(0xFF1D4962) else Color(0xFF87D67A))
    drawRoundRect(
        color = if (isDarkTheme) Color(0xFF2E344A) else Color(0xFF59616B),
        topLeft = Offset(0f, h * 0.78f),
        size = Size(w, h * 0.24f),
        cornerRadius = CornerRadius(0f, 0f)
    )
    drawLine(
        color = Color.White.copy(alpha = 0.58f),
        start = Offset(w * 0.08f, h * 0.89f),
        end = Offset(w * 0.92f, h * 0.89f),
        strokeWidth = minSide * 0.01f,
        cap = StrokeCap.Round
    )

    val busX = w * 0.2f
    val busY = h * 0.45f
    drawRoundRect(
        color = Color(0xFFFFC83D),
        topLeft = Offset(busX, busY),
        size = Size(w * 0.58f, h * 0.26f),
        cornerRadius = CornerRadius(minSide * 0.04f, minSide * 0.04f)
    )
    drawRoundRect(
        color = Color(0xFFFFE089),
        topLeft = Offset(busX + w * 0.08f, busY + h * 0.045f),
        size = Size(w * 0.34f, h * 0.075f),
        cornerRadius = CornerRadius(minSide * 0.012f, minSide * 0.012f)
    )
    drawRoundRect(
        color = Color(0xFF4EC3E0),
        topLeft = Offset(busX + w * 0.44f, busY + h * 0.045f),
        size = Size(w * 0.08f, h * 0.075f),
        cornerRadius = CornerRadius(minSide * 0.012f, minSide * 0.012f)
    )
    drawWheel(Offset(busX + w * 0.14f, busY + h * 0.26f), minSide * 0.052f)
    drawWheel(Offset(busX + w * 0.46f, busY + h * 0.26f), minSide * 0.052f)
}

private fun DrawScope.drawRainWindowScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    val wallColor = if (isDarkTheme) Color(0xFF182239) else Color(0xFFFFD7DF)
    val windowColor = if (isDarkTheme) Color(0xFF0B1834) else Color(0xFF7EDBFF)

    drawRect(color = wallColor.copy(alpha = 0.28f), size = size)
    drawRoundRect(
        color = windowColor,
        topLeft = Offset(w * 0.24f, h * 0.16f),
        size = Size(w * 0.52f, h * 0.58f),
        cornerRadius = CornerRadius(minSide * 0.045f, minSide * 0.045f)
    )
    drawRoundRect(
        color = Color.White.copy(alpha = 0.24f),
        topLeft = Offset(w * 0.24f, h * 0.16f),
        size = Size(w * 0.52f, h * 0.58f),
        cornerRadius = CornerRadius(minSide * 0.045f, minSide * 0.045f),
        style = Stroke(width = minSide * 0.012f)
    )
    drawLine(Color.White.copy(alpha = 0.58f), Offset(w * 0.5f, h * 0.17f), Offset(w * 0.5f, h * 0.74f), minSide * 0.01f)
    drawLine(Color.White.copy(alpha = 0.58f), Offset(w * 0.25f, h * 0.45f), Offset(w * 0.75f, h * 0.45f), minSide * 0.01f)

    listOf(0.32f, 0.41f, 0.57f, 0.66f).forEachIndexed { index, x ->
        val y = if (index % 2 == 0) 0.27f else 0.34f
        drawLine(
            color = Color(0xFFD9F8FF).copy(alpha = 0.82f),
            start = Offset(w * x, h * y),
            end = Offset(w * (x - 0.02f), h * (y + 0.13f)),
            strokeWidth = minSide * 0.01f,
            cap = StrokeCap.Round
        )
    }
    drawRoundRect(
        color = if (isDarkTheme) Color(0xFF5C3D52) else Color(0xFFFFB6C6),
        topLeft = Offset(0f, h * 0.78f),
        size = Size(w, h * 0.22f),
        cornerRadius = CornerRadius(0f, 0f)
    )
}

private fun DrawScope.drawBookDeskScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawCloud(Offset(w * 0.23f, h * 0.2f), minSide / 470f, Color.White.copy(alpha = 0.5f))
    drawRoundRect(
        color = if (isDarkTheme) Color(0xFF5C3A29) else Color(0xFFCB8A52),
        topLeft = Offset(0f, h * 0.68f),
        size = Size(w, h * 0.34f),
        cornerRadius = CornerRadius(0f, 0f)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.18f, h * 0.42f)
            cubicTo(w * 0.31f, h * 0.34f, w * 0.42f, h * 0.5f, w * 0.5f, h * 0.48f)
            lineTo(w * 0.5f, h * 0.76f)
            cubicTo(w * 0.37f, h * 0.73f, w * 0.25f, h * 0.68f, w * 0.14f, h * 0.76f)
            close()
        },
        color = Color(0xFFFFF8E7)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.5f, h * 0.48f)
            cubicTo(w * 0.58f, h * 0.5f, w * 0.69f, h * 0.34f, w * 0.82f, h * 0.42f)
            lineTo(w * 0.86f, h * 0.76f)
            cubicTo(w * 0.75f, h * 0.68f, w * 0.63f, h * 0.73f, w * 0.5f, h * 0.76f)
            close()
        },
        color = Color(0xFFFFF3D0)
    )
    drawLine(Color(0xFFFFC857), Offset(w * 0.5f, h * 0.48f), Offset(w * 0.5f, h * 0.76f), minSide * 0.012f)
    drawLine(Color(0xFFEF6F6C), Offset(w * 0.68f, h * 0.34f), Offset(w * 0.82f, h * 0.64f), minSide * 0.026f, cap = StrokeCap.Round)
    drawCircle(Color(0xFFFFE3A3), minSide * 0.026f, Offset(w * 0.84f, h * 0.67f))
}

private fun DrawScope.drawAppleBasketScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawRoundRect(
        color = if (isDarkTheme) Color(0xFF4B352D) else Color(0xFFD8955B),
        topLeft = Offset(0f, h * 0.74f),
        size = Size(w, h * 0.28f),
        cornerRadius = CornerRadius(0f, 0f)
    )
    val basketTop = Offset(w * 0.25f, h * 0.48f)
    drawOval(Color(0xFFA96836), basketTop, Size(w * 0.5f, h * 0.28f))
    drawOval(Color(0xFFE1A55D), Offset(w * 0.28f, h * 0.51f), Size(w * 0.44f, h * 0.12f))
    drawArcHandle(center = Offset(w * 0.5f, h * 0.48f), radius = minSide * 0.19f, isDarkTheme = isDarkTheme)
    listOf(
        Offset(w * 0.38f, h * 0.45f),
        Offset(w * 0.5f, h * 0.4f),
        Offset(w * 0.62f, h * 0.46f)
    ).forEachIndexed { index, center ->
        drawCircle(Color(0xFFE6463A), minSide * 0.055f, center)
        drawCircle(Color.White.copy(alpha = 0.34f), minSide * 0.013f, center + Offset(-minSide * 0.018f, -minSide * 0.016f))
        drawLine(
            color = Color(0xFF2D7D44),
            start = center + Offset(minSide * 0.012f, -minSide * 0.052f),
            end = center + Offset(minSide * (0.045f + index * 0.004f), -minSide * 0.078f),
            strokeWidth = minSide * 0.009f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawKiteParkScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawSun(center = Offset(w * 0.82f, h * 0.2f), radius = minSide * 0.095f)
    drawCloud(Offset(w * 0.18f, h * 0.27f), minSide / 430f, Color.White.copy(alpha = 0.64f))
    drawHill(top = h * 0.72f, color = if (isDarkTheme) Color(0xFF214F50) else Color(0xFF68CB73))
    val kiteCenter = Offset(w * 0.48f, h * 0.32f)
    drawPath(
        path = Path().apply {
            moveTo(kiteCenter.x, kiteCenter.y - minSide * 0.14f)
            lineTo(kiteCenter.x + minSide * 0.12f, kiteCenter.y)
            lineTo(kiteCenter.x, kiteCenter.y + minSide * 0.15f)
            lineTo(kiteCenter.x - minSide * 0.12f, kiteCenter.y)
            close()
        },
        color = Color(0xFFFF6F61)
    )
    drawPath(
        path = Path().apply {
            moveTo(kiteCenter.x, kiteCenter.y - minSide * 0.14f)
            lineTo(kiteCenter.x + minSide * 0.12f, kiteCenter.y)
            lineTo(kiteCenter.x, kiteCenter.y)
            close()
        },
        color = Color(0xFFFFD166)
    )
    drawLine(Color.White.copy(alpha = 0.7f), kiteCenter, Offset(w * 0.28f, h * 0.78f), minSide * 0.007f, cap = StrokeCap.Round)
    drawLine(Color(0xFFFFD166), Offset(kiteCenter.x, kiteCenter.y + minSide * 0.15f), Offset(kiteCenter.x + minSide * 0.06f, kiteCenter.y + minSide * 0.22f), minSide * 0.009f, cap = StrokeCap.Round)
}

private fun DrawScope.drawBoatRiverScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    if (isDarkTheme) {
        drawMoon(center = Offset(w * 0.82f, h * 0.18f), radius = minSide * 0.095f)
    } else {
        drawSun(center = Offset(w * 0.82f, h * 0.18f), radius = minSide * 0.095f)
    }
    drawRect(
        color = if (isDarkTheme) Color(0xFF0E4E68) else Color(0xFF4AB8E8),
        topLeft = Offset(0f, h * 0.58f),
        size = Size(w, h * 0.44f)
    )
    repeat(4) { row ->
        val y = h * (0.66f + row * 0.08f)
        drawLine(
            color = Color.White.copy(alpha = if (isDarkTheme) 0.22f else 0.42f),
            start = Offset(w * 0.12f, y),
            end = Offset(w * 0.88f, y + minSide * 0.015f),
            strokeWidth = minSide * 0.008f,
            cap = StrokeCap.Round
        )
    }
    drawPath(
        path = Path().apply {
            moveTo(w * 0.28f, h * 0.58f)
            lineTo(w * 0.72f, h * 0.58f)
            lineTo(w * 0.62f, h * 0.72f)
            lineTo(w * 0.38f, h * 0.72f)
            close()
        },
        color = Color(0xFF8A4F2D)
    )
    drawLine(Color(0xFF52311F), Offset(w * 0.5f, h * 0.3f), Offset(w * 0.5f, h * 0.59f), minSide * 0.012f)
    drawPath(
        path = Path().apply {
            moveTo(w * 0.51f, h * 0.31f)
            lineTo(w * 0.51f, h * 0.57f)
            lineTo(w * 0.68f, h * 0.57f)
            close()
        },
        color = Color(0xFFFFF2B8)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.49f, h * 0.34f)
            lineTo(w * 0.49f, h * 0.57f)
            lineTo(w * 0.34f, h * 0.57f)
            close()
        },
        color = Color(0xFFFF8A80)
    )
}

private fun DrawScope.drawRocketNightScene() {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawMoon(center = Offset(w * 0.8f, h * 0.2f), radius = minSide * 0.08f)
    listOf(
        Offset(w * 0.16f, h * 0.18f),
        Offset(w * 0.28f, h * 0.34f),
        Offset(w * 0.44f, h * 0.16f),
        Offset(w * 0.65f, h * 0.34f),
        Offset(w * 0.88f, h * 0.42f)
    ).forEach { center ->
        drawCircle(Color.White.copy(alpha = 0.82f), minSide * 0.01f, center)
    }
    val rocketCenter = Offset(w * 0.48f, h * 0.5f)
    drawPath(
        path = Path().apply {
            moveTo(rocketCenter.x, rocketCenter.y - minSide * 0.24f)
            cubicTo(rocketCenter.x + minSide * 0.13f, rocketCenter.y - minSide * 0.12f, rocketCenter.x + minSide * 0.12f, rocketCenter.y + minSide * 0.15f, rocketCenter.x, rocketCenter.y + minSide * 0.2f)
            cubicTo(rocketCenter.x - minSide * 0.12f, rocketCenter.y + minSide * 0.15f, rocketCenter.x - minSide * 0.13f, rocketCenter.y - minSide * 0.12f, rocketCenter.x, rocketCenter.y - minSide * 0.24f)
            close()
        },
        color = Color(0xFFF5F8FF)
    )
    drawCircle(Color(0xFF72DDF7), minSide * 0.04f, Offset(rocketCenter.x, rocketCenter.y - minSide * 0.06f))
    drawPath(
        path = Path().apply {
            moveTo(rocketCenter.x - minSide * 0.08f, rocketCenter.y + minSide * 0.1f)
            lineTo(rocketCenter.x - minSide * 0.18f, rocketCenter.y + minSide * 0.22f)
            lineTo(rocketCenter.x - minSide * 0.04f, rocketCenter.y + minSide * 0.18f)
            close()
        },
        color = Color(0xFFFF6F61)
    )
    drawPath(
        path = Path().apply {
            moveTo(rocketCenter.x + minSide * 0.08f, rocketCenter.y + minSide * 0.1f)
            lineTo(rocketCenter.x + minSide * 0.18f, rocketCenter.y + minSide * 0.22f)
            lineTo(rocketCenter.x + minSide * 0.04f, rocketCenter.y + minSide * 0.18f)
            close()
        },
        color = Color(0xFFFF6F61)
    )
    drawPath(
        path = Path().apply {
            moveTo(rocketCenter.x, rocketCenter.y + minSide * 0.2f)
            lineTo(rocketCenter.x - minSide * 0.045f, rocketCenter.y + minSide * 0.34f)
            lineTo(rocketCenter.x, rocketCenter.y + minSide * 0.29f)
            lineTo(rocketCenter.x + minSide * 0.045f, rocketCenter.y + minSide * 0.34f)
            close()
        },
        color = Color(0xFFFFC857)
    )
}

private fun DrawScope.drawBreakfastTableScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawSun(center = Offset(w * 0.18f, h * 0.2f), radius = minSide * 0.095f)
    drawRoundRect(
        color = if (isDarkTheme) Color(0xFF59382C) else Color(0xFFC98555),
        topLeft = Offset(0f, h * 0.64f),
        size = Size(w, h * 0.38f),
        cornerRadius = CornerRadius(0f, 0f)
    )
    drawOval(Color(0xFFFFFAF0), Offset(w * 0.31f, h * 0.47f), Size(w * 0.34f, h * 0.24f))
    drawOval(Color(0xFFFFD166), Offset(w * 0.39f, h * 0.52f), Size(w * 0.18f, h * 0.12f))
    drawRoundRect(
        color = Color(0xFF7ED6DF),
        topLeft = Offset(w * 0.68f, h * 0.47f),
        size = Size(w * 0.13f, h * 0.19f),
        cornerRadius = CornerRadius(minSide * 0.025f, minSide * 0.025f)
    )
    drawCircle(Color(0xFF7ED6DF), minSide * 0.035f, Offset(w * 0.82f, h * 0.56f), style = Stroke(minSide * 0.014f))
    drawRoundRect(
        color = Color(0xFFE8A14A),
        topLeft = Offset(w * 0.17f, h * 0.5f),
        size = Size(w * 0.16f, h * 0.16f),
        cornerRadius = CornerRadius(minSide * 0.025f, minSide * 0.025f)
    )
}

private fun DrawScope.drawFlowerGardenScene(isDarkTheme: Boolean) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    drawCloud(Offset(w * 0.24f, h * 0.22f), minSide / 430f, Color.White.copy(alpha = 0.68f))
    drawSun(center = Offset(w * 0.82f, h * 0.18f), radius = minSide * 0.09f)
    drawHill(top = h * 0.72f, color = if (isDarkTheme) Color(0xFF215545) else Color(0xFF75D16A))
    listOf(0.23f, 0.38f, 0.54f, 0.71f).forEachIndexed { index, x ->
        val stemTop = h * (0.56f + (index % 2) * 0.04f)
        drawLine(
            color = Color(0xFF2B8A4B),
            start = Offset(w * x, h * 0.78f),
            end = Offset(w * x, stemTop),
            strokeWidth = minSide * 0.014f,
            cap = StrokeCap.Round
        )
        val petalColor = listOf(
            Color(0xFFFF6F91),
            Color(0xFFFFC857),
            Color(0xFF8B7CFF),
            Color(0xFFFF8A5B)
        )[index]
        repeat(6) { petal ->
            val angleX = if (petal % 2 == 0) 0.028f else -0.028f
            val angleY = (petal - 2) * 0.012f
            drawCircle(
                color = petalColor,
                radius = minSide * 0.025f,
                center = Offset(w * x + w * angleX, stemTop + h * angleY)
            )
        }
        drawCircle(Color(0xFF7A4F00), minSide * 0.021f, Offset(w * x, stemTop))
    }
}

private fun DrawScope.drawSun(center: Offset, radius: Float) {
    drawCircle(Color(0xFFFFE68A).copy(alpha = 0.42f), radius * 1.55f, center)
    drawCircle(Color(0xFFFFC857), radius, center)
}

private fun DrawScope.drawMoon(center: Offset, radius: Float) {
    drawCircle(Color(0xFFFFF1B8).copy(alpha = 0.22f), radius * 1.7f, center)
    drawCircle(Color(0xFFFFF1B8), radius, center)
    drawCircle(Color(0xFF1A2540), radius * 0.92f, center + Offset(radius * 0.45f, -radius * 0.22f))
}

private fun DrawScope.drawCloud(center: Offset, scale: Float, color: Color) {
    drawOval(
        color = color,
        topLeft = Offset(center.x - 60f * scale, center.y - 12f * scale),
        size = Size(120f * scale, 30f * scale)
    )
    drawCircle(color, 21f * scale, center + Offset(-34f * scale, -7f * scale))
    drawCircle(color, 29f * scale, center + Offset(-2f * scale, -18f * scale))
    drawCircle(color, 23f * scale, center + Offset(33f * scale, -8f * scale))
}

private fun DrawScope.drawHill(top: Float, color: Color) {
    drawPath(
        path = Path().apply {
            moveTo(0f, top)
            cubicTo(size.width * 0.2f, top - size.height * 0.08f, size.width * 0.38f, top + size.height * 0.04f, size.width * 0.56f, top - size.height * 0.02f)
            cubicTo(size.width * 0.74f, top - size.height * 0.08f, size.width * 0.9f, top + size.height * 0.05f, size.width, top)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        },
        color = color
    )
}

private fun DrawScope.drawWheel(center: Offset, radius: Float) {
    drawCircle(Color(0xFF263238), radius, center)
    drawCircle(Color(0xFFECEFF1), radius * 0.48f, center)
}

private fun DrawScope.drawArcHandle(center: Offset, radius: Float, isDarkTheme: Boolean) {
    val color = if (isDarkTheme) Color(0xFFB77742) else Color(0xFF8F562E)
    val path = Path().apply {
        moveTo(center.x - radius, center.y + radius * 0.35f)
        cubicTo(center.x - radius * 0.72f, center.y - radius, center.x + radius * 0.72f, center.y - radius, center.x + radius, center.y + radius * 0.35f)
    }
    drawPath(path = path, color = color, style = Stroke(width = radius * 0.12f, cap = StrokeCap.Round))
}

private data class LearningScene(
    val type: LearningSceneType,
    val englishSentence: String,
    val indonesianMeaning: String,
    val dayTop: Color,
    val dayBottom: Color,
    val nightTop: Color,
    val nightBottom: Color
)

private enum class LearningSceneType {
    Airplane,
    SchoolBus,
    RainWindow,
    BookDesk,
    AppleBasket,
    KitePark,
    BoatRiver,
    RocketNight,
    BreakfastTable,
    FlowerGarden
}

private val homeLearningScenes = listOf(
    LearningScene(
        type = LearningSceneType.Airplane,
        englishSentence = "The airplane flies in the sky.",
        indonesianMeaning = "Pesawat itu terbang di langit.",
        dayTop = Color(0xFF77D9FF),
        dayBottom = Color(0xFFFFE6A7),
        nightTop = Color(0xFF10234E),
        nightBottom = Color(0xFF1C4D66)
    ),
    LearningScene(
        type = LearningSceneType.SchoolBus,
        englishSentence = "The bus takes the students to school.",
        indonesianMeaning = "Bus itu mengantar siswa ke sekolah.",
        dayTop = Color(0xFFFFD36E),
        dayBottom = Color(0xFFB6F2A5),
        nightTop = Color(0xFF1E2B4F),
        nightBottom = Color(0xFF32445C)
    ),
    LearningScene(
        type = LearningSceneType.RainWindow,
        englishSentence = "The rain falls on the window.",
        indonesianMeaning = "Hujan turun di jendela.",
        dayTop = Color(0xFFBDEBFF),
        dayBottom = Color(0xFFFFC8D8),
        nightTop = Color(0xFF16213D),
        nightBottom = Color(0xFF36425B)
    ),
    LearningScene(
        type = LearningSceneType.BookDesk,
        englishSentence = "The book is open on the desk.",
        indonesianMeaning = "Buku itu terbuka di atas meja.",
        dayTop = Color(0xFFFFE6A8),
        dayBottom = Color(0xFFFFC5A5),
        nightTop = Color(0xFF24304F),
        nightBottom = Color(0xFF5A3A42)
    ),
    LearningScene(
        type = LearningSceneType.AppleBasket,
        englishSentence = "The apples are in the basket.",
        indonesianMeaning = "Apel-apel itu ada di dalam keranjang.",
        dayTop = Color(0xFFFFF3B0),
        dayBottom = Color(0xFFFFC0A8),
        nightTop = Color(0xFF2B2545),
        nightBottom = Color(0xFF5E3E4A)
    ),
    LearningScene(
        type = LearningSceneType.KitePark,
        englishSentence = "The kite dances above the field.",
        indonesianMeaning = "Layang-layang itu menari di atas lapangan.",
        dayTop = Color(0xFF8FE4FF),
        dayBottom = Color(0xFFDFF59F),
        nightTop = Color(0xFF13274D),
        nightBottom = Color(0xFF265B5C)
    ),
    LearningScene(
        type = LearningSceneType.BoatRiver,
        englishSentence = "The boat moves across the river.",
        indonesianMeaning = "Perahu itu bergerak menyeberangi sungai.",
        dayTop = Color(0xFF87DFFF),
        dayBottom = Color(0xFF75C8F2),
        nightTop = Color(0xFF102342),
        nightBottom = Color(0xFF0E4E68)
    ),
    LearningScene(
        type = LearningSceneType.RocketNight,
        englishSentence = "The rocket goes up into space.",
        indonesianMeaning = "Roket itu naik ke luar angkasa.",
        dayTop = Color(0xFF748BFF),
        dayBottom = Color(0xFFFFB2C5),
        nightTop = Color(0xFF090E2C),
        nightBottom = Color(0xFF273A76)
    ),
    LearningScene(
        type = LearningSceneType.BreakfastTable,
        englishSentence = "The family eats breakfast together.",
        indonesianMeaning = "Keluarga itu sarapan bersama.",
        dayTop = Color(0xFFFFE08A),
        dayBottom = Color(0xFFFFC49A),
        nightTop = Color(0xFF26304B),
        nightBottom = Color(0xFF5B3B3A)
    ),
    LearningScene(
        type = LearningSceneType.FlowerGarden,
        englishSentence = "The flowers grow in the garden.",
        indonesianMeaning = "Bunga-bunga itu tumbuh di taman.",
        dayTop = Color(0xFFA8EEFF),
        dayBottom = Color(0xFFC8F5A5),
        nightTop = Color(0xFF143054),
        nightBottom = Color(0xFF245C4E)
    )
)

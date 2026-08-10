package com.example.vocabkid.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs

@Composable
fun SkyThemeBackground(
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skyBackground")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cloudDrift"
    )
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starTwinkle"
    )

    Box(modifier = modifier.fillMaxSize()) {
        val veilColor = MaterialTheme.colorScheme.surface
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (isDarkTheme) {
                drawNightSky(twinkle = twinkle, veilColor = veilColor)
            } else {
                drawDaySky(drift = drift, veilColor = veilColor)
            }
        }
        content()
    }
}

private fun DrawScope.drawDaySky(
    drift: Float,
    veilColor: Color
) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF65CFFF),
                Color(0xFFAEEBFF),
                Color(0xFFFFF1B8)
            ),
            startY = 0f,
            endY = size.height
        )
    )

    val minSide = size.minDimension
    drawCircle(
        color = Color(0xFFFFD95B),
        radius = minSide * 0.105f,
        center = Offset(size.width * 0.82f, size.height * 0.13f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.34f),
        radius = minSide * 0.15f,
        center = Offset(size.width * 0.82f, size.height * 0.13f)
    )

    val wrapWidth = size.width * 1.22f
    val driftOffset = drift * size.width * 0.28f
    dayClouds.forEachIndexed { index, cloud ->
        val x = ((cloud.x * size.width + driftOffset + index * 37f) % wrapWidth) - size.width * 0.11f
        drawCloud(
            center = Offset(x, cloud.y * size.height),
            scale = cloud.scale * minSide / 390f,
            color = Color.White.copy(alpha = cloud.alpha)
        )
    }

    drawRollingHill(
        top = size.height * 0.82f,
        firstColor = Color(0xFF84D88C).copy(alpha = 0.92f),
        secondColor = Color(0xFF51C8A7).copy(alpha = 0.76f)
    )

    drawRect(
        color = veilColor.copy(alpha = 0.10f),
        size = size
    )
}

private fun DrawScope.drawNightSky(
    twinkle: Float,
    veilColor: Color
) {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF08142F),
                Color(0xFF172456),
                Color(0xFF135E74)
            ),
            startY = 0f,
            endY = size.height
        )
    )

    val minSide = size.minDimension
    val moonCenter = Offset(size.width * 0.78f, size.height * 0.14f)
    drawCircle(
        color = Color(0xFFFFF0B5),
        radius = minSide * 0.075f,
        center = moonCenter
    )
    drawCircle(
        color = Color(0xFF08142F),
        radius = minSide * 0.073f,
        center = moonCenter + Offset(minSide * 0.034f, -minSide * 0.018f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.12f),
        radius = minSide * 0.13f,
        center = moonCenter
    )

    nightStars.forEachIndexed { index, star ->
        val phase = (twinkle + index * 0.17f) % 1f
        val pulse = 1f - abs(phase * 2f - 1f)
        drawCircle(
            color = Color.White.copy(alpha = 0.34f + pulse * 0.58f),
            radius = minSide * star.radius,
            center = Offset(star.x * size.width, star.y * size.height)
        )
    }

    drawNightCloud(
        center = Offset(size.width * 0.18f, size.height * 0.24f),
        scale = minSide / 390f,
        color = Color(0xFF9AD8FF).copy(alpha = 0.12f)
    )
    drawNightCloud(
        center = Offset(size.width * 0.58f, size.height * 0.31f),
        scale = minSide / 470f,
        color = Color(0xFFC7EDFF).copy(alpha = 0.10f)
    )

    drawRollingHill(
        top = size.height * 0.84f,
        firstColor = Color(0xFF0D3E4D).copy(alpha = 0.92f),
        secondColor = Color(0xFF082D38).copy(alpha = 0.82f)
    )

    drawRect(
        color = veilColor.copy(alpha = 0.06f),
        size = size
    )
}

private fun DrawScope.drawCloud(
    center: Offset,
    scale: Float,
    color: Color
) {
    drawOval(
        color = color,
        topLeft = Offset(center.x - 60f * scale, center.y - 13f * scale),
        size = Size(122f * scale, 30f * scale)
    )
    drawCircle(color = color, radius = 22f * scale, center = center + Offset(-34f * scale, -8f * scale))
    drawCircle(color = color, radius = 30f * scale, center = center + Offset(-4f * scale, -18f * scale))
    drawCircle(color = color, radius = 24f * scale, center = center + Offset(34f * scale, -9f * scale))
}

private fun DrawScope.drawNightCloud(
    center: Offset,
    scale: Float,
    color: Color
) {
    drawOval(
        color = color,
        topLeft = Offset(center.x - 78f * scale, center.y - 14f * scale),
        size = Size(156f * scale, 32f * scale)
    )
    drawCircle(color = color, radius = 22f * scale, center = center + Offset(-42f * scale, -10f * scale))
    drawCircle(color = color, radius = 28f * scale, center = center + Offset(0f, -16f * scale))
    drawCircle(color = color, radius = 20f * scale, center = center + Offset(42f * scale, -8f * scale))
}

private fun DrawScope.drawRollingHill(
    top: Float,
    firstColor: Color,
    secondColor: Color
) {
    val backHill = Path().apply {
        moveTo(0f, top + size.height * 0.04f)
        cubicTo(size.width * 0.18f, top - size.height * 0.05f, size.width * 0.34f, top + size.height * 0.06f, size.width * 0.52f, top)
        cubicTo(size.width * 0.72f, top - size.height * 0.08f, size.width * 0.86f, top + size.height * 0.04f, size.width, top - size.height * 0.02f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    val frontHill = Path().apply {
        moveTo(0f, top + size.height * 0.1f)
        cubicTo(size.width * 0.2f, top + size.height * 0.02f, size.width * 0.42f, top + size.height * 0.13f, size.width * 0.62f, top + size.height * 0.05f)
        cubicTo(size.width * 0.78f, top - size.height * 0.02f, size.width * 0.92f, top + size.height * 0.1f, size.width, top + size.height * 0.04f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }

    drawPath(path = backHill, color = firstColor)
    drawPath(path = frontHill, color = secondColor)
}

private data class SkyDot(
    val x: Float,
    val y: Float,
    val radius: Float
)

private data class CloudSpec(
    val x: Float,
    val y: Float,
    val scale: Float,
    val alpha: Float
)

private val dayClouds = listOf(
    CloudSpec(x = 0.12f, y = 0.17f, scale = 0.92f, alpha = 0.82f),
    CloudSpec(x = 0.46f, y = 0.11f, scale = 0.70f, alpha = 0.72f),
    CloudSpec(x = 0.78f, y = 0.28f, scale = 1.08f, alpha = 0.68f),
    CloudSpec(x = 0.22f, y = 0.43f, scale = 0.76f, alpha = 0.55f)
)

private val nightStars = listOf(
    SkyDot(0.10f, 0.10f, 0.0048f),
    SkyDot(0.18f, 0.22f, 0.0033f),
    SkyDot(0.28f, 0.14f, 0.0042f),
    SkyDot(0.39f, 0.07f, 0.0034f),
    SkyDot(0.47f, 0.24f, 0.0047f),
    SkyDot(0.63f, 0.13f, 0.0035f),
    SkyDot(0.69f, 0.33f, 0.0042f),
    SkyDot(0.86f, 0.25f, 0.0035f),
    SkyDot(0.92f, 0.09f, 0.0046f),
    SkyDot(0.13f, 0.36f, 0.0038f),
    SkyDot(0.33f, 0.34f, 0.0031f),
    SkyDot(0.54f, 0.43f, 0.0038f),
    SkyDot(0.74f, 0.48f, 0.0032f),
    SkyDot(0.88f, 0.40f, 0.004f)
)

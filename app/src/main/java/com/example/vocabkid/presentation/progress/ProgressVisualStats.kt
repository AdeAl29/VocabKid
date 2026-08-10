package com.example.vocabkid.presentation.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProgressVisualStatCard(
    type: ProgressStatVisual,
    value: String,
    label: String,
    supportingText: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val colors = type.colors(isDark)
    val cardModifier = if (onClick != null) {
        modifier
            .fillMaxWidth()
            .height(124.dp)
            .clickable(onClick = onClick)
    } else {
        modifier
            .fillMaxWidth()
            .height(124.dp)
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.top, colors.bottom),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                )

                when (type) {
                    ProgressStatVisual.Total -> drawTotalWordsIllustration(colors)
                    ProgressStatVisual.Mastered -> drawMasteredIllustration(colors)
                    ProgressStatVisual.Due -> drawDueIllustration(colors)
                    ProgressStatVisual.Accuracy -> drawAccuracyIllustration(colors)
                    ProgressStatVisual.Reviews -> drawReviewsIllustration(colors)
                    ProgressStatVisual.Focus -> drawFocusIllustration(colors)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = if (isDark) 0.44f else 0.34f),
                                Color.Black.copy(alpha = if (isDark) 0.70f else 0.58f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.86f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

enum class ProgressStatVisual {
    Total,
    Mastered,
    Due,
    Accuracy,
    Reviews,
    Focus
}

private data class ProgressVisualColors(
    val top: Color,
    val bottom: Color,
    val accent: Color,
    val accentDark: Color,
    val soft: Color
)

private fun ProgressStatVisual.colors(isDark: Boolean): ProgressVisualColors {
    return when (this) {
        ProgressStatVisual.Total -> if (isDark) {
            ProgressVisualColors(Color(0xFF20376B), Color(0xFF132142), Color(0xFFAFC2FF), Color(0xFF5268D9), Color(0xFFF0F3FF))
        } else {
            ProgressVisualColors(Color(0xFFC8D5FF), Color(0xFFE9F8FF), Color(0xFF4E6BE6), Color(0xFF2E4198), Color.White)
        }
        ProgressStatVisual.Mastered -> if (isDark) {
            ProgressVisualColors(Color(0xFF293A63), Color(0xFF16213E), Color(0xFFFFD166), Color(0xFFB47B14), Color(0xFFFFF0BF))
        } else {
            ProgressVisualColors(Color(0xFFFFEAA5), Color(0xFFFFC9DF), Color(0xFFFFB300), Color(0xFFC27400), Color.White)
        }
        ProgressStatVisual.Due -> if (isDark) {
            ProgressVisualColors(Color(0xFF193D55), Color(0xFF102639), Color(0xFF7AD7FF), Color(0xFF2B7EA8), Color(0xFFE8F7FF))
        } else {
            ProgressVisualColors(Color(0xFF9BE7FF), Color(0xFFFFE6A7), Color(0xFF1687D9), Color(0xFF0E5B91), Color.White)
        }
        ProgressStatVisual.Accuracy -> if (isDark) {
            ProgressVisualColors(Color(0xFF1D4A48), Color(0xFF102D31), Color(0xFF7ADBD0), Color(0xFF20877B), Color(0xFFE5FFFB))
        } else {
            ProgressVisualColors(Color(0xFFBDF7E8), Color(0xFFDFF7A6), Color(0xFF13A085), Color(0xFF09695B), Color.White)
        }
        ProgressStatVisual.Reviews -> if (isDark) {
            ProgressVisualColors(Color(0xFF304653), Color(0xFF172733), Color(0xFFFFC782), Color(0xFFD8892C), Color(0xFFFFF2DE))
        } else {
            ProgressVisualColors(Color(0xFFFFD89C), Color(0xFFFFF1BE), Color(0xFFE57C23), Color(0xFF9B4B12), Color.White)
        }
        ProgressStatVisual.Focus -> if (isDark) {
            ProgressVisualColors(Color(0xFF543044), Color(0xFF2A1A2C), Color(0xFFFF9AA5), Color(0xFFE3566A), Color(0xFFFFEFF2))
        } else {
            ProgressVisualColors(Color(0xFFFFB8C6), Color(0xFFFFE6A7), Color(0xFFE94862), Color(0xFF9B2438), Color.White)
        }
    }
}

private fun DrawScope.drawTotalWordsIllustration(colors: ProgressVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension

    drawPath(
        path = Path().apply {
            moveTo(w * 0.2f, h * 0.25f)
            cubicTo(w * 0.32f, h * 0.18f, w * 0.42f, h * 0.33f, w * 0.5f, h * 0.3f)
            lineTo(w * 0.5f, h * 0.78f)
            cubicTo(w * 0.38f, h * 0.72f, w * 0.28f, h * 0.7f, w * 0.18f, h * 0.78f)
            close()
        },
        color = colors.soft
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.5f, h * 0.3f)
            cubicTo(w * 0.58f, h * 0.33f, w * 0.68f, h * 0.18f, w * 0.8f, h * 0.25f)
            lineTo(w * 0.82f, h * 0.78f)
            cubicTo(w * 0.72f, h * 0.7f, w * 0.62f, h * 0.72f, w * 0.5f, h * 0.78f)
            close()
        },
        color = Color(0xFFFFF7D6)
    )
    drawLine(colors.accent, Offset(w * 0.5f, h * 0.3f), Offset(w * 0.5f, h * 0.78f), minSide * 0.014f, cap = StrokeCap.Round)
    drawCircle(colors.accent, minSide * 0.12f, Offset(w * 0.72f, h * 0.32f))
    drawLine(Color.White, Offset(w * 0.68f, h * 0.37f), Offset(w * 0.72f, h * 0.25f), minSide * 0.018f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.72f, h * 0.25f), Offset(w * 0.77f, h * 0.37f), minSide * 0.018f, cap = StrokeCap.Round)
    drawLine(Color.White, Offset(w * 0.695f, h * 0.32f), Offset(w * 0.755f, h * 0.32f), minSide * 0.014f, cap = StrokeCap.Round)
}

private fun DrawScope.drawMasteredIllustration(colors: ProgressVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    val center = Offset(w * 0.5f, h * 0.42f)

    drawPath(
        path = Path().apply {
            moveTo(w * 0.36f, h * 0.63f)
            lineTo(w * 0.28f, h * 0.84f)
            lineTo(w * 0.43f, h * 0.77f)
            lineTo(w * 0.48f, h * 0.63f)
            close()
        },
        color = Color(0xFFEF6F6C)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.64f, h * 0.63f)
            lineTo(w * 0.72f, h * 0.84f)
            lineTo(w * 0.57f, h * 0.77f)
            lineTo(w * 0.52f, h * 0.63f)
            close()
        },
        color = Color(0xFF6C8CFF)
    )
    drawCircle(colors.accentDark, minSide * 0.27f, center)
    drawCircle(colors.accent, minSide * 0.22f, center)
    drawStar(center, minSide * 0.12f, minSide * 0.052f, Color.White.copy(alpha = 0.94f))
}

private fun DrawScope.drawDueIllustration(colors: ProgressVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    val center = Offset(w * 0.44f, h * 0.44f)

    drawCircle(colors.soft, minSide * 0.25f, center)
    drawCircle(colors.accent, minSide * 0.2f, center)
    drawLine(Color.White, center, Offset(center.x, center.y - minSide * 0.11f), minSide * 0.018f, cap = StrokeCap.Round)
    drawLine(Color.White, center, Offset(center.x + minSide * 0.1f, center.y + minSide * 0.05f), minSide * 0.018f, cap = StrokeCap.Round)
    drawCircle(Color.White, minSide * 0.018f, center)
    drawRoundRect(
        color = Color(0xFFFFF8F0),
        topLeft = Offset(w * 0.56f, h * 0.52f),
        size = Size(w * 0.27f, h * 0.22f),
        cornerRadius = CornerRadius(minSide * 0.035f, minSide * 0.035f)
    )
    drawLine(colors.accentDark, Offset(w * 0.61f, h * 0.61f), Offset(w * 0.78f, h * 0.61f), minSide * 0.014f, cap = StrokeCap.Round)
}

private fun DrawScope.drawAccuracyIllustration(colors: ProgressVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    val center = Offset(w * 0.5f, h * 0.46f)

    drawCircle(colors.soft, minSide * 0.27f, center)
    drawCircle(colors.accent, minSide * 0.21f, center)
    drawCircle(colors.soft, minSide * 0.14f, center)
    drawCircle(colors.accentDark, minSide * 0.075f, center)
    drawPath(
        path = Path().apply {
            moveTo(w * 0.39f, h * 0.47f)
            lineTo(w * 0.47f, h * 0.55f)
            lineTo(w * 0.64f, h * 0.35f)
        },
        color = Color.White,
        style = Stroke(width = minSide * 0.034f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawReviewsIllustration(colors: ProgressVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension

    drawRoundRect(
        color = colors.soft,
        topLeft = Offset(w * 0.19f, h * 0.2f),
        size = Size(w * 0.48f, h * 0.62f),
        cornerRadius = CornerRadius(minSide * 0.055f, minSide * 0.055f)
    )
    listOf(0.36f, 0.5f, 0.64f).forEach { y ->
        drawLine(colors.accentDark.copy(alpha = 0.36f), Offset(w * 0.3f, h * y), Offset(w * 0.58f, h * y), minSide * 0.013f, cap = StrokeCap.Round)
    }
    drawPath(
        path = Path().apply {
            moveTo(w * 0.31f, h * 0.36f)
            lineTo(w * 0.37f, h * 0.43f)
            lineTo(w * 0.49f, h * 0.29f)
        },
        color = Color(0xFF2FBF71),
        style = Stroke(width = minSide * 0.032f, cap = StrokeCap.Round)
    )
    drawLine(colors.accent, Offset(w * 0.62f, h * 0.74f), Offset(w * 0.83f, h * 0.33f), minSide * 0.07f, cap = StrokeCap.Round)
    drawLine(Color(0xFF5A3E2B), Offset(w * 0.81f, h * 0.3f), Offset(w * 0.86f, h * 0.21f), minSide * 0.052f, cap = StrokeCap.Round)
}

private fun DrawScope.drawFocusIllustration(colors: ProgressVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    val lensCenter = Offset(w * 0.43f, h * 0.42f)

    drawCircle(colors.soft, minSide * 0.21f, lensCenter)
    drawCircle(
        color = colors.accent,
        radius = minSide * 0.2f,
        center = lensCenter,
        style = Stroke(width = minSide * 0.04f)
    )
    drawLine(colors.accentDark, Offset(w * 0.55f, h * 0.58f), Offset(w * 0.73f, h * 0.77f), minSide * 0.055f, cap = StrokeCap.Round)
    drawPath(
        path = Path().apply {
            moveTo(w * 0.44f, h * 0.29f)
            lineTo(w * 0.57f, h * 0.52f)
            lineTo(w * 0.31f, h * 0.52f)
            close()
        },
        color = Color(0xFFFFD166)
    )
    drawLine(Color(0xFF743000), Offset(w * 0.44f, h * 0.36f), Offset(w * 0.44f, h * 0.45f), minSide * 0.012f, cap = StrokeCap.Round)
    drawCircle(Color(0xFF743000), minSide * 0.01f, Offset(w * 0.44f, h * 0.485f))
}

private fun DrawScope.drawStar(
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

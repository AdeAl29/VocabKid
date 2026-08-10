package com.example.vocabkid.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun HomeVisualStatCard(
    type: HomeStatVisual,
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
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(colors.top, colors.bottom),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                )

                when (type) {
                    HomeStatVisual.Today -> drawTodayIllustration(colors)
                    HomeStatVisual.Mastered -> drawMasteredIllustration(colors)
                    HomeStatVisual.Practice -> drawPracticeIllustration(colors)
                    HomeStatVisual.Vocabulary -> drawVocabularyIllustration(colors)
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

enum class HomeStatVisual {
    Today,
    Mastered,
    Practice,
    Vocabulary
}

private data class StatVisualColors(
    val top: Color,
    val bottom: Color,
    val accent: Color,
    val accentDark: Color,
    val soft: Color
)

private fun HomeStatVisual.colors(isDark: Boolean): StatVisualColors {
    return when (this) {
        HomeStatVisual.Today -> if (isDark) {
            StatVisualColors(Color(0xFF163B5E), Color(0xFF0F253C), Color(0xFF7AD7FF), Color(0xFF2E83B7), Color(0xFFE8F7FF))
        } else {
            StatVisualColors(Color(0xFF9BE7FF), Color(0xFFFFE6A7), Color(0xFF1687D9), Color(0xFF0E5B91), Color.White)
        }
        HomeStatVisual.Mastered -> if (isDark) {
            StatVisualColors(Color(0xFF293A63), Color(0xFF16213E), Color(0xFFFFD166), Color(0xFFB47B14), Color(0xFFFFF0BF))
        } else {
            StatVisualColors(Color(0xFFFFEAA5), Color(0xFFFFC9DF), Color(0xFFFFB300), Color(0xFFC27400), Color.White)
        }
        HomeStatVisual.Practice -> if (isDark) {
            StatVisualColors(Color(0xFF304653), Color(0xFF172733), Color(0xFFFFC782), Color(0xFFD8892C), Color(0xFFFFF2DE))
        } else {
            StatVisualColors(Color(0xFFFFD89C), Color(0xFFFFF1BE), Color(0xFFE57C23), Color(0xFF9B4B12), Color.White)
        }
        HomeStatVisual.Vocabulary -> if (isDark) {
            StatVisualColors(Color(0xFF26396D), Color(0xFF142040), Color(0xFFAFC2FF), Color(0xFF5268D9), Color(0xFFF0F3FF))
        } else {
            StatVisualColors(Color(0xFFC8D5FF), Color(0xFFE9F8FF), Color(0xFF4E6BE6), Color(0xFF2E4198), Color.White)
        }
    }
}

private fun DrawScope.drawTodayIllustration(colors: StatVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension

    drawCircle(
        color = Color(0xFFFFD166),
        radius = minSide * 0.17f,
        center = Offset(w * 0.78f, h * 0.24f)
    )
    drawRoundRect(
        color = colors.soft,
        topLeft = Offset(w * 0.14f, h * 0.18f),
        size = Size(w * 0.42f, h * 0.58f),
        cornerRadius = CornerRadius(minSide * 0.06f, minSide * 0.06f)
    )
    drawRoundRect(
        color = colors.accent,
        topLeft = Offset(w * 0.14f, h * 0.18f),
        size = Size(w * 0.42f, h * 0.16f),
        cornerRadius = CornerRadius(minSide * 0.06f, minSide * 0.06f)
    )
    repeat(3) { row ->
        repeat(3) { column ->
            drawCircle(
                color = colors.accentDark.copy(alpha = 0.62f),
                radius = minSide * 0.023f,
                center = Offset(w * (0.23f + column * 0.12f), h * (0.45f + row * 0.1f))
            )
        }
    }
    drawRoundRect(
        color = Color(0xFFFFF8F0),
        topLeft = Offset(w * 0.5f, h * 0.47f),
        size = Size(w * 0.3f, h * 0.24f),
        cornerRadius = CornerRadius(minSide * 0.035f, minSide * 0.035f)
    )
    drawLine(
        color = colors.accent,
        start = Offset(w * 0.56f, h * 0.56f),
        end = Offset(w * 0.74f, h * 0.56f),
        strokeWidth = minSide * 0.018f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawMasteredIllustration(colors: StatVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension
    val center = Offset(w * 0.5f, h * 0.43f)

    drawPath(
        path = Path().apply {
            moveTo(w * 0.36f, h * 0.63f)
            lineTo(w * 0.29f, h * 0.83f)
            lineTo(w * 0.43f, h * 0.77f)
            lineTo(w * 0.48f, h * 0.63f)
            close()
        },
        color = Color(0xFFEF6F6C)
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.64f, h * 0.63f)
            lineTo(w * 0.71f, h * 0.83f)
            lineTo(w * 0.57f, h * 0.77f)
            lineTo(w * 0.52f, h * 0.63f)
            close()
        },
        color = Color(0xFF6C8CFF)
    )
    drawCircle(
        color = colors.accentDark,
        radius = minSide * 0.27f,
        center = center
    )
    drawCircle(
        color = colors.accent,
        radius = minSide * 0.22f,
        center = center
    )
    drawStar(
        center = center,
        outerRadius = minSide * 0.12f,
        innerRadius = minSide * 0.052f,
        color = Color.White.copy(alpha = 0.94f)
    )
}

private fun DrawScope.drawPracticeIllustration(colors: StatVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension

    drawRoundRect(
        color = colors.soft,
        topLeft = Offset(w * 0.22f, h * 0.17f),
        size = Size(w * 0.46f, h * 0.64f),
        cornerRadius = CornerRadius(minSide * 0.055f, minSide * 0.055f)
    )
    listOf(0.34f, 0.48f, 0.62f).forEach { y ->
        drawLine(
            color = colors.accentDark.copy(alpha = 0.36f),
            start = Offset(w * 0.34f, h * y),
            end = Offset(w * 0.61f, h * y),
            strokeWidth = minSide * 0.013f,
            cap = StrokeCap.Round
        )
    }
    drawPath(
        path = Path().apply {
            moveTo(w * 0.33f, h * 0.36f)
            lineTo(w * 0.38f, h * 0.43f)
            lineTo(w * 0.49f, h * 0.29f)
        },
        color = Color(0xFF2FBF71),
        style = Stroke(width = minSide * 0.032f, cap = StrokeCap.Round)
    )
    drawLine(
        color = colors.accent,
        start = Offset(w * 0.62f, h * 0.72f),
        end = Offset(w * 0.82f, h * 0.33f),
        strokeWidth = minSide * 0.07f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color(0xFF5A3E2B),
        start = Offset(w * 0.8f, h * 0.3f),
        end = Offset(w * 0.85f, h * 0.21f),
        strokeWidth = minSide * 0.052f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawVocabularyIllustration(colors: StatVisualColors) {
    val w = size.width
    val h = size.height
    val minSide = size.minDimension

    drawPath(
        path = Path().apply {
            moveTo(w * 0.18f, h * 0.26f)
            cubicTo(w * 0.3f, h * 0.2f, w * 0.42f, h * 0.33f, w * 0.5f, h * 0.3f)
            lineTo(w * 0.5f, h * 0.78f)
            cubicTo(w * 0.38f, h * 0.72f, w * 0.28f, h * 0.7f, w * 0.18f, h * 0.78f)
            close()
        },
        color = colors.soft
    )
    drawPath(
        path = Path().apply {
            moveTo(w * 0.5f, h * 0.3f)
            cubicTo(w * 0.58f, h * 0.33f, w * 0.7f, h * 0.2f, w * 0.82f, h * 0.26f)
            lineTo(w * 0.82f, h * 0.78f)
            cubicTo(w * 0.72f, h * 0.7f, w * 0.62f, h * 0.72f, w * 0.5f, h * 0.78f)
            close()
        },
        color = Color(0xFFFFF7D6)
    )
    drawLine(
        color = colors.accent,
        start = Offset(w * 0.5f, h * 0.3f),
        end = Offset(w * 0.5f, h * 0.78f),
        strokeWidth = minSide * 0.014f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = colors.accentDark.copy(alpha = 0.48f),
        start = Offset(w * 0.28f, h * 0.45f),
        end = Offset(w * 0.42f, h * 0.45f),
        strokeWidth = minSide * 0.012f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = colors.accentDark.copy(alpha = 0.48f),
        start = Offset(w * 0.58f, h * 0.52f),
        end = Offset(w * 0.72f, h * 0.52f),
        strokeWidth = minSide * 0.012f,
        cap = StrokeCap.Round
    )
    drawCircle(
        color = colors.accent,
        radius = minSide * 0.12f,
        center = Offset(w * 0.73f, h * 0.31f)
    )
    drawLine(
        color = Color.White,
        start = Offset(w * 0.69f, h * 0.36f),
        end = Offset(w * 0.73f, h * 0.24f),
        strokeWidth = minSide * 0.018f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White,
        start = Offset(w * 0.73f, h * 0.24f),
        end = Offset(w * 0.78f, h * 0.36f),
        strokeWidth = minSide * 0.018f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = Color.White,
        start = Offset(w * 0.705f, h * 0.31f),
        end = Offset(w * 0.765f, h * 0.31f),
        strokeWidth = minSide * 0.014f,
        cap = StrokeCap.Round
    )
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
            x = center.x + kotlin.math.cos(angle).toFloat() * radius,
            y = center.y + kotlin.math.sin(angle).toFloat() * radius
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

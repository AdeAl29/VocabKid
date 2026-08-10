package com.example.vocabkid.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

// ── Palette ───────────────────────────────────────────────────────────────────

private val SkyTop    = Color(0xFF1A80FF)
private val SkyMid    = Color(0xFF64CFFF)
private val SkyBot    = Color(0xFFD4F5FF)
private val RobotBody = Color(0xFFF9FBFF)
private val RobotDark = Color(0xFF0D2A45)
private val RobotBlue = Color(0xFF0D8CFF)
private val RobotLite = Color(0xFF88F0FF)
private val RobotAnt  = Color(0xFFFFC928)
private val RobotOng  = Color(0xFFFF9F1A)
private val RobotPink = Color(0xFFFF6B8B)
private val StarYel   = Color(0xFFFFE44D)
private val StarOrg   = Color(0xFFFF9F2D)
private val StarCyan  = Color(0xFF4EF0FF)
private val NameGrad1 = Color(0xFF0D8CFF)
private val NameGrad2 = Color(0xFF9B5BFF)
private val TagColor  = Color(0xFF0A5FAA)

// ── Particle data ─────────────────────────────────────────────────────────────

private data class Particle(
    val angleDeg: Float,
    val speed: Float,
    val color: Color,
    val size: Float,
    val isStar: Boolean
)

private val particles = listOf(
    Particle(15f,  1.0f, StarYel,   9f, true),
    Particle(55f,  1.3f, StarOrg,   7f, false),
    Particle(90f,  1.1f, StarCyan,  8f, true),
    Particle(130f, 0.9f, StarYel,   6f, false),
    Particle(165f, 1.2f, RobotBlue,10f, true),
    Particle(200f, 1.0f, StarOrg,   7f, false),
    Particle(240f, 1.3f, StarCyan,  9f, true),
    Particle(280f, 0.85f,StarYel,   6f, false),
    Particle(315f, 1.1f, RobotPink, 8f, true),
    Particle(350f, 1.2f, RobotLite, 7f, false)
)

// ── Splash Screen ─────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(onFinished: () -> Unit) {

    val robotScale       = remember { Animatable(0f) }
    val robotAlpha       = remember { Animatable(0f) }
    val logoAlpha        = remember { Animatable(0f) }
    val logoScale        = remember { Animatable(0.7f) }
    val tagAlpha         = remember { Animatable(0f) }
    val exitAlpha        = remember { Animatable(1f) }
    val particleProgress = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "splashIdle")

    val robotBob by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "robotBob"
    )
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "glow"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "ringRotate"
    )
    val starSpinA by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "starSpinA"
    )
    val starSpinB by infiniteTransition.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "starSpinB"
    )
    val bgShimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "bgShimmer"
    )

    LaunchedEffect(Unit) {
        // Phase 1: Robot bounces in
        launch {
            robotScale.animateTo(
                1.15f,
                animationSpec = tween(350, easing = CubicBezierEasing(0.17f, 0.89f, 0.32f, 1.28f))
            )
            robotScale.animateTo(1f, animationSpec = tween(160, easing = FastOutSlowInEasing))
        }
        launch { robotAlpha.animateTo(1f, animationSpec = tween(300)) }

        delay(380)

        // Phase 2: Particles burst
        launch {
            particleProgress.animateTo(
                1f,
                animationSpec = tween(520, easing = CubicBezierEasing(0.0f, 0.9f, 0.57f, 1.0f))
            )
        }

        delay(200)

        // Phase 3: Logo text
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(300))
            logoScale.animateTo(1f, animationSpec = tween(300, easing = FastOutSlowInEasing))
        }

        delay(250)

        // Phase 4: Tagline
        launch { tagAlpha.animateTo(1f, animationSpec = tween(280)) }

        // Hold at full display
        delay(1700)

        // Phase 5: Fade out
        exitAlpha.animateTo(0f, animationSpec = tween(360, easing = FastOutSlowInEasing))
        onFinished()
    }

    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(exitAlpha.value),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w  = size.width
            val h  = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Sky background
            val shimmedTop = lerpColor(SkyTop, Color(0xFF3A50FF), bgShimmer * 0.25f)
            val shimmedBot = lerpColor(SkyBot, Color(0xFFFFE9B0), bgShimmer * 0.15f)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(shimmedTop, SkyMid, shimmedBot),
                    startY = 0f, endY = h
                )
            )

            // Decorative light halos
            drawCircle(
                color = Color.White.copy(alpha = 0.18f),
                radius = w * 0.38f,
                center = Offset(w * 0.88f, h * 0.08f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.10f),
                radius = w * 0.22f,
                center = Offset(w * 0.12f, h * 0.05f)
            )

            // Rolling hills at bottom
            drawSplashHill(
                top   = h * 0.82f,
                front = Color(0xFF84D88C).copy(alpha = 0.88f),
                back  = Color(0xFF51C8A7).copy(alpha = 0.72f)
            )

            // Floating background sparkles
            drawTinySparkle(Offset(w * 0.10f, h * 0.12f), 6f, StarYel,   starSpinA)
            drawTinySparkle(Offset(w * 0.88f, h * 0.18f), 5f, StarCyan,  starSpinB)
            drawTinySparkle(Offset(w * 0.22f, h * 0.72f), 7f, StarOrg,   starSpinA)
            drawTinySparkle(Offset(w * 0.82f, h * 0.68f), 5f, StarYel,   starSpinB)
            drawTinySparkle(Offset(w * 0.55f, h * 0.11f), 4f, RobotPink, starSpinA)
            drawTinySparkle(Offset(w * 0.04f, h * 0.45f), 5f, RobotLite, starSpinB)
            drawTinySparkle(Offset(w * 0.95f, h * 0.42f), 6f, StarOrg,   starSpinA)

            // Glow ring behind robot
            val robotRadius = minOf(w, h) * 0.20f
            val robotCenter = Offset(cx, cy - 60f + robotBob)
            val glowR = robotRadius * 1.6f * glowRadius
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        RobotBlue.copy(alpha = 0.38f),
                        StarCyan.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    center = robotCenter,
                    radius = glowR
                ),
                radius = glowR,
                center = robotCenter
            )

            // Orbiting ring of dots
            if (robotAlpha.value > 0.5f) {
                drawOrbitingRing(
                    center   = robotCenter,
                    radius   = robotRadius * 1.55f,
                    rotation = ringRotation,
                    alpha    = robotAlpha.value
                )
            }

            // Burst particles
            val pp = particleProgress.value
            if (pp > 0f) {
                particles.forEach { p ->
                    val angleRad = Math.toRadians(p.angleDeg.toDouble()).toFloat()
                    val dist     = robotRadius * 1.9f * p.speed * pp
                    val fade     = if (pp < 0.5f) pp * 2f else 1f - (pp - 0.5f) * 2f
                    val px       = robotCenter.x + cos(angleRad) * dist
                    val py       = robotCenter.y + sin(angleRad) * dist
                    if (p.isStar) {
                        drawBurstStar(
                            center = Offset(px, py),
                            r = p.size * (0.7f + pp * 0.3f),
                            color  = p.color.copy(alpha = fade * robotAlpha.value)
                        )
                    } else {
                        drawCircle(
                            color  = p.color.copy(alpha = fade * robotAlpha.value),
                            radius = p.size * (0.6f + pp * 0.4f),
                            center = Offset(px, py)
                        )
                    }
                }
            }

            // Robot mascot
            if (robotAlpha.value > 0f) {
                val rSize = robotRadius * 2.1f
                translate(
                    left = robotCenter.x - rSize / 2f,
                    top  = robotCenter.y - rSize / 2f
                ) {
                    scale(scale = robotScale.value, pivot = Offset(rSize / 2f, rSize / 2f)) {
                        drawSplashRobot(w = rSize, h = rSize, alpha = robotAlpha.value)
                    }
                }
            }

            // Logo "VocabKid"
            if (logoAlpha.value > 0f) {
                val logoCenter = Offset(cx, cy + robotRadius * 1.32f)
                val fontSize   = (w * 0.098f / density).sp
                val nameStyle  = TextStyle(
                    fontSize   = fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    brush = Brush.horizontalGradient(
                        colors = listOf(NameGrad1, NameGrad2),
                        startX = logoCenter.x - w * 0.22f,
                        endX   = logoCenter.x + w * 0.22f
                    )
                )
                val measured = textMeasurer.measure("VocabKid", nameStyle)
                val textX    = logoCenter.x - measured.size.width / 2f
                val textY    = logoCenter.y - measured.size.height / 2f
                scale(scale = logoScale.value, pivot = logoCenter) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text  = "VocabKid",
                        style = TextStyle(
                            fontSize   = fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    NameGrad1.copy(alpha = logoAlpha.value),
                                    NameGrad2.copy(alpha = logoAlpha.value)
                                ),
                                startX = textX - w * 0.08f,
                                endX   = textX + measured.size.width + w * 0.08f
                            )
                        ),
                        topLeft = Offset(textX, textY)
                    )
                }
            }

            // Tagline
            if (tagAlpha.value > 0f) {
                val robotCenterFull = Offset(cx, cy - 60f)
                val robotRadiusFull = minOf(w, h) * 0.20f
                val tagY = robotCenterFull.y + robotRadiusFull * 1.32f + w * 0.11f
                val tagStyle = TextStyle(
                    fontSize   = (w * 0.038f / density).sp,
                    fontWeight = FontWeight.Medium,
                    color      = TagColor.copy(alpha = tagAlpha.value)
                )
                val tagMeasured = textMeasurer.measure("Belajar Kosakata, Seru & Mudah!", tagStyle)
                drawText(
                    textMeasurer = textMeasurer,
                    text         = "Belajar Kosakata, Seru & Mudah!",
                    style        = tagStyle,
                    topLeft      = Offset(cx - tagMeasured.size.width / 2f, tagY)
                )
            }
        }
    }
}

// ── Private draw helpers ──────────────────────────────────────────────────────

private fun lerpColor(a: Color, b: Color, t: Float): Color = Color(
    red   = a.red   + (b.red   - a.red)   * t,
    green = a.green + (b.green - a.green) * t,
    blue  = a.blue  + (b.blue  - a.blue)  * t,
    alpha = 1f
)

private fun DrawScope.drawSplashHill(top: Float, front: Color, back: Color) {
    val w = size.width
    val h = size.height
    val backHill = Path().apply {
        moveTo(0f, top + h * 0.04f)
        cubicTo(w * 0.18f, top - h * 0.05f, w * 0.34f, top + h * 0.06f, w * 0.52f, top)
        cubicTo(w * 0.72f, top - h * 0.08f, w * 0.86f, top + h * 0.04f, w, top - h * 0.02f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    val frontHill = Path().apply {
        moveTo(0f, top + h * 0.10f)
        cubicTo(w * 0.20f, top + h * 0.02f, w * 0.42f, top + h * 0.13f, w * 0.62f, top + h * 0.05f)
        cubicTo(w * 0.78f, top - h * 0.02f, w * 0.92f, top + h * 0.10f, w, top + h * 0.04f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    drawPath(path = backHill, color = back)
    drawPath(path = frontHill, color = front)
}

private fun DrawScope.drawOrbitingRing(
    center: Offset,
    radius: Float,
    rotation: Float,
    alpha: Float
) {
    val dotCount = 8
    repeat(dotCount) { i ->
        val angleDeg = rotation + i * (360f / dotCount)
        val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()
        val dotX     = center.x + cos(angleRad) * radius
        val dotY     = center.y + sin(angleRad) * radius
        val dotAlpha = (0.3f + 0.55f * (i.toFloat() / dotCount)) * alpha
        val color    = if (i % 2 == 0) RobotBlue else StarCyan
        drawCircle(
            color  = color.copy(alpha = dotAlpha),
            radius = if (i % 2 == 0) 5f else 3.5f,
            center = Offset(dotX, dotY)
        )
    }
}

private fun DrawScope.drawTinySparkle(center: Offset, r: Float, color: Color, spin: Float) {
    rotate(degrees = spin, pivot = center) {
        val path = Path()
        val arms = 4
        repeat(arms * 2) { i ->
            val angle = Math.toRadians((i * 180.0 / arms) - 90.0).toFloat()
            val rad   = if (i % 2 == 0) r else r * 0.38f
            val pt    = Offset(center.x + cos(angle) * rad, center.y + sin(angle) * rad)
            if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
        }
        path.close()
        drawPath(path, color.copy(alpha = 0.82f))
    }
}

private fun DrawScope.drawBurstStar(center: Offset, r: Float, color: Color) {
    val path  = Path()
    val inner = r * 0.42f
    repeat(10) { i ->
        val angle = Math.toRadians((i * 36.0) - 90.0).toFloat()
        val rad   = if (i % 2 == 0) r else inner
        val pt    = Offset(center.x + cos(angle) * rad, center.y + sin(angle) * rad)
        if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawSplashRobot(w: Float, h: Float, alpha: Float) {
    val white = RobotBody.copy(alpha = alpha)
    val dark  = RobotDark.copy(alpha = alpha)
    val acnt  = RobotBlue.copy(alpha = alpha)
    val lite  = RobotLite.copy(alpha = alpha)
    val ant   = RobotAnt.copy(alpha  = alpha)
    val ong   = RobotOng.copy(alpha  = alpha)
    val pink  = RobotPink.copy(alpha = alpha)
    val outln = Color(0xFFB8C5D8).copy(alpha = alpha * 0.9f)
    val blk   = Color.Black.copy(alpha = alpha)

    // Antenna
    drawLine(color = dark, start = Offset(w*0.54f,h*0.13f), end = Offset(w*0.58f,h*0.02f),
        strokeWidth = w*0.055f, cap = StrokeCap.Round)
    drawCircle(color = ant, radius = w*0.095f, center = Offset(w*0.6f, h*0.02f))
    drawCircle(color = ong, radius = w*0.065f, center = Offset(w*0.6f, h*0.02f))

    // Head shell
    drawRoundRect(color = white, topLeft = Offset(w*0.16f,h*0.15f),
        size = Size(w*0.68f,h*0.43f), cornerRadius = CornerRadius(w*0.19f,w*0.19f))
    drawRoundRect(color = outln, topLeft = Offset(w*0.16f,h*0.15f),
        size = Size(w*0.68f,h*0.43f), cornerRadius = CornerRadius(w*0.19f,w*0.19f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = w*0.018f))

    // Crown
    drawRoundRect(color = acnt, topLeft = Offset(w*0.31f,h*0.10f),
        size = Size(w*0.38f,h*0.12f), cornerRadius = CornerRadius(w*0.09f,w*0.09f))

    // Ears
    drawCircle(color = acnt, radius = w*0.07f, center = Offset(w*0.15f, h*0.36f))
    drawCircle(color = acnt, radius = w*0.07f, center = Offset(w*0.85f, h*0.36f))
    drawCircle(color = ong,  radius = w*0.04f, center = Offset(w*0.85f, h*0.36f))

    // Face screen
    drawRoundRect(color = dark, topLeft = Offset(w*0.24f,h*0.22f),
        size = Size(w*0.52f,h*0.28f), cornerRadius = CornerRadius(w*0.11f,w*0.11f))

    // Eyebrow bars
    drawRoundRect(color = lite, topLeft = Offset(w*0.31f,h*0.27f),
        size = Size(w*0.12f,h*0.03f), cornerRadius = CornerRadius(w*0.02f,w*0.02f))
    drawRoundRect(color = lite, topLeft = Offset(w*0.58f,h*0.27f),
        size = Size(w*0.12f,h*0.03f), cornerRadius = CornerRadius(w*0.02f,w*0.02f))

    // Eyes
    drawCircle(color = white, radius = w*0.09f,  center = Offset(w*0.38f, h*0.38f))
    drawCircle(color = white, radius = w*0.09f,  center = Offset(w*0.63f, h*0.38f))
    drawCircle(color = blk,   radius = w*0.052f, center = Offset(w*0.39f, h*0.39f))
    drawCircle(color = blk,   radius = w*0.052f, center = Offset(w*0.62f, h*0.39f))
    drawCircle(color = white, radius = w*0.018f, center = Offset(w*0.36f, h*0.36f))
    drawCircle(color = white, radius = w*0.018f, center = Offset(w*0.59f, h*0.36f))

    // Smile
    val smile = Path().apply {
        moveTo(w*0.47f, h*0.45f)
        quadraticBezierTo(w*0.50f, h*0.51f, w*0.56f, h*0.45f)
        quadraticBezierTo(w*0.52f, h*0.57f, w*0.47f, h*0.45f)
    }
    drawPath(color = pink, path = smile)

    // Body
    drawRoundRect(color = white, topLeft = Offset(w*0.31f,h*0.58f),
        size = Size(w*0.38f,h*0.29f), cornerRadius = CornerRadius(w*0.12f,w*0.12f))
    drawRoundRect(color = acnt, topLeft = Offset(w*0.38f,h*0.64f),
        size = Size(w*0.24f,h*0.13f), cornerRadius = CornerRadius(w*0.05f,w*0.05f))

    // Star badge on body
    val starPath = Path()
    val sc = Offset(w*0.50f, h*0.705f)
    val or2 = w*0.055f; val ir2 = w*0.026f
    repeat(10) { i ->
        val a = Math.toRadians((i*36.0)-90.0).toFloat()
        val rr = if (i%2==0) or2 else ir2
        val pt = Offset(sc.x+cos(a)*rr, sc.y+sin(a)*rr)
        if (i==0) starPath.moveTo(pt.x,pt.y) else starPath.lineTo(pt.x,pt.y)
    }
    starPath.close()
    drawPath(starPath, ant)

    // Arms
    drawLine(color=dark, start=Offset(w*0.34f,h*0.63f), end=Offset(w*0.17f,h*0.54f),
        strokeWidth=w*0.07f, cap=StrokeCap.Round)
    drawCircle(color=white, radius=w*0.09f,  center=Offset(w*0.13f,h*0.51f))
    drawLine(color=dark, start=Offset(w*0.68f,h*0.64f), end=Offset(w*0.81f,h*0.73f),
        strokeWidth=w*0.07f, cap=StrokeCap.Round)
    drawCircle(color=white, radius=w*0.085f, center=Offset(w*0.84f,h*0.76f))
}

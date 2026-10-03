package com.example.vocabkid.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vocabkid.audio.rememberSoundEffectPlayer
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun GlassBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemCount = mainBottomNavItems.size
    val routeIndex = mainBottomNavItems
        .indexOfFirst { item -> item.route == currentRoute }
        .coerceAtLeast(0)

    var selectedIndex by remember(currentRoute) { mutableIntStateOf(routeIndex) }
    val latestOnNavigate by rememberUpdatedState(onNavigate)
    val soundEffects = rememberSoundEffectPlayer()
    val hapticFeedback = LocalHapticFeedback.current

    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val shape = RoundedCornerShape(32.dp)

    // Glass container gradient (iOS translucent frosted glass)
    val glassBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(
                Color(0x991E293B),
                Color(0x800F172A)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.82f),
                Color(0xFFF1F6FB).copy(alpha = 0.65f)
            )
        }
    )

    // Specular glass highlight border (catches light at the top edge)
    val borderBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.08f)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.88f),
                Color.White.copy(alpha = 0.25f)
            )
        }
    )

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(selectedIndex.toFloat()) }

    // When dragging, follow finger directly; otherwise glide smoothly with Apple-style spring physics
    val targetIndexFloat = if (isDragging) dragProgress else selectedIndex.toFloat()
    val animatedIndex by animateFloatAsState(
        targetValue = targetIndexFloat,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "navAnimatedIndex"
    )

    val visualSelectedIndex = animatedIndex.roundToInt().coerceIn(0, itemCount - 1)

    // Haptic feedback tick when passing tabs while dragging
    var lastHapticIndex by remember { mutableIntStateOf(selectedIndex) }
    LaunchedEffect(visualSelectedIndex) {
        if (isDragging && visualSelectedIndex != lastHapticIndex) {
            lastHapticIndex = visualSelectedIndex
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .shadow(
                elevation = if (isDark) 16.dp else 12.dp,
                shape = shape,
                spotColor = if (isDark) Color.Black.copy(alpha = 0.60f) else Color(0x28000000),
                ambientColor = Color.Transparent
            ),
        shape = shape,
        color = Color.Transparent
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(66.dp)
                .clip(shape)
                .background(glassBrush)
                .border(0.9.dp, borderBrush, shape)
        ) {
            val horizontalPadding = 8.dp
            val verticalPadding = 8.dp
            val availableWidth = maxWidth - (horizontalPadding * 2)

            val inactiveWidthFraction = INACTIVE_ITEM_WEIGHT / TOTAL_WEIGHT
            val activeWidthFraction = ACTIVE_ITEM_WEIGHT / TOTAL_WEIGHT

            val inactiveWidthDp = availableWidth * inactiveWidthFraction
            val activeWidthDp = availableWidth * activeWidthFraction

            // Organic elastic stretch: pill stretches slightly during transit between tabs
            val normalizedPos = animatedIndex.coerceIn(0f, (itemCount - 1).toFloat())
            val transitFraction = (normalizedPos % 1f)
            val stretchFactor = (1f - 2f * abs(transitFraction - 0.5f)).coerceAtLeast(0f)
            val stretchDp = 10.dp * stretchFactor

            val pillXDp = horizontalPadding + (inactiveWidthDp * normalizedPos) - (stretchDp / 2f)
            val pillWidthDp = activeWidthDp + stretchDp

            // Floating Pill Capsule (iOS Frosted Pill Slider)
            val pillBackground = Brush.verticalGradient(
                colors = if (isDark) {
                    listOf(
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.15f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFFF0F5FA).copy(alpha = 0.88f)
                    )
                }
            )

            val pillBorderBrush = Brush.verticalGradient(
                colors = if (isDark) {
                    listOf(
                        Color.White.copy(alpha = 0.40f),
                        Color.White.copy(alpha = 0.15f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = 0.98f),
                        Color.White.copy(alpha = 0.70f)
                    )
                }
            )

            val pillShape = RoundedCornerShape(26.dp)

            // Render the gliding pill underneath the tabs
            Box(
                modifier = Modifier
                    .offset(x = pillXDp)
                    .align(Alignment.CenterStart)
                    .width(pillWidthDp)
                    .height(50.dp)
                    .shadow(
                        elevation = if (isDark) 2.dp else 4.dp,
                        shape = pillShape,
                        spotColor = if (isDark) Color.Black.copy(alpha = 0.45f) else Color(0x22000000),
                        ambientColor = Color.Transparent
                    )
                    .clip(pillShape)
                    .background(pillBackground)
                    .border(0.9.dp, pillBorderBrush, pillShape)
            )

            // Drag gesture detector and tab items row
            val availableWidthPx = with(LocalDensity.current) { availableWidth.toPx() }
            val inactiveWidthPx = availableWidthPx * inactiveWidthFraction

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .pointerInput(selectedIndex, inactiveWidthPx) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                                dragProgress = selectedIndex.toFloat()
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                if (inactiveWidthPx > 0f) {
                                    dragProgress = (dragProgress + (dragAmount / inactiveWidthPx))
                                        .coerceIn(0f, (itemCount - 1).toFloat())
                                }
                            },
                            onDragEnd = {
                                val targetIndex = dragProgress.roundToInt().coerceIn(0, itemCount - 1)
                                isDragging = false
                                if (targetIndex != selectedIndex) {
                                    selectedIndex = targetIndex
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    soundEffects.playSelect()
                                    latestOnNavigate(mainBottomNavItems[targetIndex].route)
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                dragProgress = selectedIndex.toFloat()
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                mainBottomNavItems.forEachIndexed { index, item ->
                    val isSelected = visualSelectedIndex == index
                    val itemWeight by animateFloatAsState(
                        targetValue = if (isSelected) ACTIVE_ITEM_WEIGHT else INACTIVE_ITEM_WEIGHT,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        ),
                        label = "bottomNavWeight_$index"
                    )

                    GlassBottomNavItem(
                        item = item,
                        selected = isSelected,
                        onClick = {
                            if (index != selectedIndex) {
                                selectedIndex = index
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                soundEffects.playSelect()
                                latestOnNavigate(item.route)
                            }
                        },
                        modifier = Modifier.weight(itemWeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassBottomNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            if (isDark) Color(0xFF38BDF8) else Color(0xFF044E45)
        } else {
            if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "bottomNavContent"
    )

    val iconScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.88f
            selected -> 1.15f
            else -> 0.90f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bottomNavIconScale"
    )

    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bottomNavPressScale"
    )

    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom scale animation gives tactile feedback
                onClick = onClick
            )
            .graphicsLayer {
                scaleX = pressScale
                scaleY = pressScale
            }
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                    }
            )
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ) + expandHorizontally(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    expandFrom = Alignment.Start
                ),
                exit = fadeOut(tween(80)) + shrinkHorizontally(
                    animationSpec = tween(110),
                    shrinkTowards = Alignment.Start
                )
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val mainBottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Default.Home),
    BottomNavItem(Routes.STUDY, "Belajar", Icons.Default.School),
    BottomNavItem(Routes.QUIZ, "Kuis", Icons.Default.Quiz),
    BottomNavItem(Routes.VOCABULARY, "Kosakata", Icons.AutoMirrored.Filled.MenuBook),
    BottomNavItem(Routes.PROGRESS, "Progress", Icons.Default.BarChart)
)

private const val ACTIVE_ITEM_WEIGHT = 1.62f
private const val INACTIVE_ITEM_WEIGHT = 0.72f
private const val TOTAL_WEIGHT = ACTIVE_ITEM_WEIGHT + (4 * INACTIVE_ITEM_WEIGHT)

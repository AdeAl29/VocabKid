package com.example.vocabkid.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.vocabkid.audio.rememberSoundEffectPlayer
import kotlin.math.roundToInt

@Composable
fun GlassBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentIndex = mainBottomNavItems
        .indexOfFirst { item -> item.route == currentRoute }
        .coerceAtLeast(0)
    val latestOnNavigate by rememberUpdatedState(onNavigate)
    val soundEffects = rememberSoundEffectPlayer()
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val shape = RoundedCornerShape(30.dp)
    val itemCount = mainBottomNavItems.size
    val glassBrush = Brush.linearGradient(
        colors = if (isDark) {
            listOf(
                Color(0x661A263A),
                Color(0x4D0D1729)
            )
        } else {
            listOf(
                Color.White.copy(alpha = 0.56f),
                Color(0xFFE9F8FF).copy(alpha = 0.34f)
            )
        }
    )
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.18f)
    } else {
        Color.White.copy(alpha = 0.42f)
    }
    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(currentIndex.toFloat()) }
    val visualSelectedIndex = if (isDragging) {
        dragProgress.roundToInt()
    } else {
        currentIndex
    }.coerceIn(0, itemCount - 1)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        shape = shape,
        color = Color.Transparent,
        shadowElevation = 6.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .height(66.dp)
                .clip(shape)
                .background(glassBrush)
                .border(0.8.dp, borderColor, shape)
        ) {
            val horizontalPadding = 8.dp
            val verticalPadding = 8.dp
            val itemWidth = (maxWidth - 16.dp) / itemCount
            val itemWidthPx = with(LocalDensity.current) { itemWidth.toPx() }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .pointerInput(currentIndex, itemWidthPx) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                isDragging = true
                                dragProgress = currentIndex.toFloat()
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume()
                                if (itemWidthPx > 0f) {
                                    dragProgress = (
                                        dragProgress + (dragAmount / itemWidthPx)
                                        ).coerceIn(0f, (itemCount - 1).toFloat())
                                }
                            },
                            onDragEnd = {
                                val targetIndex = dragProgress
                                    .roundToInt()
                                    .coerceIn(0, itemCount - 1)
                                isDragging = false
                                if (targetIndex != currentIndex) {
                                    soundEffects.playSelect()
                                    latestOnNavigate(mainBottomNavItems[targetIndex].route)
                                } else {
                                    dragProgress = currentIndex.toFloat()
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                dragProgress = currentIndex.toFloat()
                            }
                        )
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                mainBottomNavItems.forEachIndexed { index, item ->
                    GlassBottomNavItem(
                        item = item,
                        selected = visualSelectedIndex == index,
                        onClick = {
                            if (index != currentIndex) {
                                soundEffects.playSelect()
                            }
                            latestOnNavigate(item.route)
                        },
                        modifier = Modifier.weight(
                            animateFloatAsState(
                                targetValue = if (visualSelectedIndex == index) {
                                    ACTIVE_ITEM_WEIGHT
                                } else {
                                    INACTIVE_ITEM_WEIGHT
                                },
                                animationSpec = tween(
                                    durationMillis = 240,
                                    easing = FastOutSlowInEasing
                                ),
                                label = "bottomNavWeight"
                            ).value
                        )
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
    val activeContainer = if (isDark) {
        Color.White.copy(alpha = 0.18f)
    } else {
        Color.White.copy(alpha = 0.52f)
    }
    val containerColor by animateColorAsState(
        targetValue = if (selected) activeContainer else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavContainer"
    )
    val activeBorderColor by animateColorAsState(
        targetValue = if (selected) {
            Color.White.copy(alpha = if (isDark) 0.30f else 0.72f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavBorder"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            if (isDark) Color.White else Color(0xFF063D36)
        } else {
            MaterialTheme.colorScheme.onBackground.copy(alpha = if (isDark) 0.66f else 0.58f)
        },
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavContent"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.14f else 0.80f,
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavIconScale"
    )

    Box(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(1.dp, activeBorderColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
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
                enter = fadeIn(tween(150)) + expandHorizontally(tween(190)),
                exit = fadeOut(tween(100)) + shrinkHorizontally(tween(140))
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

private const val ACTIVE_ITEM_WEIGHT = 1.58f
private const val INACTIVE_ITEM_WEIGHT = 0.69f

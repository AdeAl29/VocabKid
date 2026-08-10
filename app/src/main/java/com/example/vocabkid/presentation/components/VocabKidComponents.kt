package com.example.vocabkid.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LooksOne
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 116.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BigMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    supportingText: String? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyMessage(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val visual = categoryVisualFor(category)
    val chipModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Surface(
        modifier = chipModifier,
        shape = RoundedCornerShape(8.dp),
        color = visual.containerColor,
        contentColor = visual.contentColor,
        border = BorderStroke(1.dp, visual.contentColor.copy(alpha = 0.18f))
    ) {
        CategoryChipContent(
            category = category,
            icon = visual.icon,
            textColor = visual.contentColor
        )
    }
}

@Composable
fun CategoryFilterChip(
    category: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val visual = categoryVisualFor(category)
    val containerColor = if (selected) {
        visual.containerColor
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        visual.contentColor
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = if (selected) {
        visual.contentColor.copy(alpha = 0.22f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.42f)
    }

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        CategoryChipContent(
            category = category,
            icon = visual.icon,
            textColor = contentColor
        )
    }
}

@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val visual = statusVisualFor(status)
    val chipModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Surface(
        modifier = chipModifier,
        shape = RoundedCornerShape(8.dp),
        color = visual.containerColor,
        contentColor = visual.contentColor,
        border = BorderStroke(1.dp, visual.contentColor.copy(alpha = 0.18f))
    ) {
        CategoryChipContent(
            category = status,
            icon = visual.icon,
            textColor = visual.contentColor
        )
    }
}

@Composable
fun categoryThemeContainerColor(category: String): Color {
    val visual = categoryVisualFor(category)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (isDark) {
        lerp(MaterialTheme.colorScheme.surface, visual.contentColor, 0.2f)
    } else {
        lerp(MaterialTheme.colorScheme.surface, visual.containerColor, 0.78f)
    }
}

@Composable
fun categoryThemeContentColor(category: String): Color {
    val visual = categoryVisualFor(category)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (isDark) visual.containerColor else visual.contentColor
}

fun categoryThemeIcon(category: String): ImageVector {
    return categoryVisualFor(category).icon
}

@Composable
private fun CategoryChipContent(
    category: String,
    icon: ImageVector,
    textColor: Color
) {
    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = category,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private data class CategoryVisual(
    val icon: ImageVector,
    val containerColor: Color,
    val contentColor: Color
)

private fun categoryVisualFor(category: String): CategoryVisual {
    return when (category.trim().lowercase()) {
        "animal" -> CategoryVisual(Icons.Default.Pets, Color(0xFFE5F5D7), Color(0xFF2D5D14))
        "fruit" -> CategoryVisual(Icons.Default.LocalFlorist, Color(0xFFFFDFEA), Color(0xFF7B173A))
        "vegetable" -> CategoryVisual(Icons.Default.Eco, Color(0xFFDDF6DD), Color(0xFF235D25))
        "food" -> CategoryVisual(Icons.Default.Restaurant, Color(0xFFFFE7C7), Color(0xFF6A3B00))
        "drink" -> CategoryVisual(Icons.Default.LocalDrink, Color(0xFFD9F0FF), Color(0xFF064B73))
        "school" -> CategoryVisual(Icons.Default.School, Color(0xFFE8E1FF), Color(0xFF31226F))
        "family" -> CategoryVisual(Icons.Default.FamilyRestroom, Color(0xFFFFE0EF), Color(0xFF71173F))
        "color" -> CategoryVisual(Icons.Default.Palette, Color(0xFFFFF0BD), Color(0xFF614500))
        "number" -> CategoryVisual(Icons.Default.LooksOne, Color(0xFFE1E8FF), Color(0xFF1B3478))
        "action" -> CategoryVisual(Icons.AutoMirrored.Filled.DirectionsRun, Color(0xFFD9F7EF), Color(0xFF00513F))
        "body" -> CategoryVisual(Icons.Default.AccessibilityNew, Color(0xFFFFE1D4), Color(0xFF6B2412))
        "clothing" -> CategoryVisual(Icons.Default.Checkroom, Color(0xFFECE0FF), Color(0xFF4C277A))
        "home" -> CategoryVisual(Icons.Default.Home, Color(0xFFE1EDFF), Color(0xFF153D72))
        "object" -> CategoryVisual(Icons.Default.Category, Color(0xFFE8F0EE), Color(0xFF34504B))
        "place" -> CategoryVisual(Icons.Default.Place, Color(0xFFE0F4FF), Color(0xFF0C5272))
        "nature" -> CategoryVisual(Icons.Default.Park, Color(0xFFDCF5D9), Color(0xFF285D1F))
        "weather" -> CategoryVisual(Icons.Default.WbSunny, Color(0xFFFFF1C2), Color(0xFF664900))
        "time" -> CategoryVisual(Icons.Default.AccessTime, Color(0xFFE9E4FF), Color(0xFF382A72))
        "feeling" -> CategoryVisual(Icons.Default.SentimentSatisfiedAlt, Color(0xFFFFE4D8), Color(0xFF74300E))
        "adjective" -> CategoryVisual(Icons.Default.AutoAwesome, Color(0xFFF0F3C8), Color(0xFF4C5310))
        "transportation" -> CategoryVisual(Icons.Default.DirectionsCar, Color(0xFFDCEEFF), Color(0xFF134E78))
        "job" -> CategoryVisual(Icons.Default.Work, Color(0xFFE6EAF0), Color(0xFF354154))
        "position" -> CategoryVisual(Icons.Default.OpenWith, Color(0xFFE7ECF5), Color(0xFF304158))
        "expression" -> CategoryVisual(Icons.Default.ChatBubble, Color(0xFFDFF5F2), Color(0xFF11554E))
        "semua" -> CategoryVisual(Icons.Default.Category, Color(0xFFD7F3EF), Color(0xFF053D36))
        else -> CategoryVisual(Icons.Default.Category, Color(0xFFE8F0EE), Color(0xFF41504D))
    }
}

private fun statusVisualFor(status: String): CategoryVisual {
    return when (status.trim().lowercase()) {
        "baru" -> CategoryVisual(Icons.Default.AutoAwesome, Color(0xFFE1E8FF), Color(0xFF1B3478))
        "dipelajari" -> CategoryVisual(Icons.Default.School, Color(0xFFFFE7C7), Color(0xFF6A3B00))
        "sering salah" -> CategoryVisual(Icons.Default.ErrorOutline, Color(0xFFFFDAD6), Color(0xFF8C1D18))
        "dikuasai" -> CategoryVisual(Icons.Default.CheckCircle, Color(0xFFDDF6DD), Color(0xFF235D25))
        else -> CategoryVisual(Icons.Default.Category, Color(0xFFE8F0EE), Color(0xFF41504D))
    }
}

@Composable
fun ProgressLine(
    label: String,
    value: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (max == 0) 0f else value.toFloat() / max.toFloat()
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "$value / $max", style = MaterialTheme.typography.bodyMedium)
        }
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 8.dp)
        )
    }
}

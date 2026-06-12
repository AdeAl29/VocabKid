package com.example.vocabkid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VocabKidColors = lightColorScheme(
    primary = Color(0xFF16877A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD7F3EF),
    onPrimaryContainer = Color(0xFF053D36),
    secondary = Color(0xFFFFA43A),
    onSecondary = Color(0xFF332000),
    secondaryContainer = Color(0xFFFFE5BD),
    onSecondaryContainer = Color(0xFF3B2500),
    tertiary = Color(0xFF4E6BE6),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE1E6FF),
    onTertiaryContainer = Color(0xFF17235B),
    background = Color(0xFFFBFCF8),
    onBackground = Color(0xFF1A1C1B),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1B),
    surfaceVariant = Color(0xFFE8F0EE),
    onSurfaceVariant = Color(0xFF41504D),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun VocabKidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VocabKidColors,
        typography = Typography(),
        content = content
    )
}

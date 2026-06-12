package com.example.vocabkid.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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

private val VocabKidTypography = Typography(
    headlineLarge = TextStyle(
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineMedium = TextStyle(
        fontSize = 26.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    headlineSmall = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold
    )
)

@Composable
fun VocabKidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = VocabKidColors,
        typography = VocabKidTypography,
        content = content
    )
}

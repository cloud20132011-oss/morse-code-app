package com.example.morsecode.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Navy = Color(0xFF0F172A)
private val NavySurface = Color(0xFF1E293B)
private val NavyCard = Color(0xFF243349)
private val Amber = Color(0xFFF59E0B)
private val AmberDark = Color(0xFFD97706)
private val TextLight = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFF94A3B8)
private val Green = Color(0xFF22C55E)
private val Red = Color(0xFFEF4444)

private val MorseColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = Color.Black,
    primaryContainer = AmberDark,
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF38BDF8),
    onSecondary = Color.Black,
    background = Navy,
    onBackground = TextLight,
    surface = NavySurface,
    onSurface = TextLight,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextMuted,
    error = Red,
    tertiary = Green
)

@Composable
fun MorseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MorseColorScheme,
        content = content
    )
}

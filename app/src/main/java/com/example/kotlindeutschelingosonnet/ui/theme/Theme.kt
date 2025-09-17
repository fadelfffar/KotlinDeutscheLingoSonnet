// ui/theme/Theme.kt
package com.example.kotlindeutschelingosonnet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// German flag colors
private val GermanRed = Color(0xFFDD0000)
private val GermanGold = Color(0xFFFFD700)
private val GermanBlack = Color(0xFF000000)

private val DarkColorScheme = darkColorScheme(
    primary = GermanGold,
    secondary = GermanRed,
    tertiary = Color(0xFFFFB74D),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)

private val LightColorScheme = lightColorScheme(
    primary = GermanRed,
    secondary = GermanGold,
    tertiary = Color(0xFFF57C00),
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE)
)

@Composable
fun GermanExamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}

// Backward compatibility
@Composable
fun EnglishExamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    GermanExamTheme(darkTheme, content)
}

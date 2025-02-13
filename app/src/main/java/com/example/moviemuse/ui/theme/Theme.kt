package com.example.moviemuse.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color.Black,
    secondary = Color.DarkGray,
    background = Color.Black,
    onPrimary = Color.White,
    onBackground = Color.White
)

@Composable
fun MovieMuseTheme(
    darkTheme: Boolean = true,  // Force dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}

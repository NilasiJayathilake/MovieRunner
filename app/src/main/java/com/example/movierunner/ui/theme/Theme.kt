package com.example.movierunner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFEF5350),          // Soft red
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB71C1C), // Deep red
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFEF9A9A),        // Light red
    onSecondary = Color.Black,
    background = Color(0xFF121212),       // Dark mode background
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),          // Cards, etc.
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),      // Rich red
    onPrimary = Color.White,          // White text on red
    secondary = Color(0xFFB0BEC5),    // Soft gray-blue
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),   // Light gray background
    onBackground = Color.Black,
    surface = Color.White,            // Card/background surfaces
    onSurface = Color.Black
)

@Composable
fun MovieRunnerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // Use system theme by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

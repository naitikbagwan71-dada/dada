package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPurple,
    tertiary = NeonPink,
    background = ObsidianBackground,
    surface = DarkSlateSurface,
    onPrimary = Color(0xFF020617),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = LightGrayText,
    onSurface = LightGrayText,
    surfaceVariant = DarkSlateSurfaceCard,
    onSurfaceVariant = LightGrayText
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for distraction-free study environment
  dynamicColor: Boolean = false, // Disable dynamic color to maintain neon branding
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IvorySeniorColorScheme =
  lightColorScheme(
    primary = MedDoneGreen,
    onPrimary = Color.White,
    primaryContainer = MedDoneSoftSurface,
    onPrimaryContainer = MedDoneGreenBadge,
    secondary = WaterPrimaryBlue,
    onSecondary = Color.White,
    secondaryContainer = WaterCardBg,
    onSecondaryContainer = DeepInk,
    background = IvoryBackground,
    onBackground = DeepInk,
    surface = CreamSurface,
    onSurface = DeepInk,
    surfaceVariant = IvoryTopGlow,
    onSurfaceVariant = SecondaryInk,
    outline = WarmBorder,
  )

@Composable
fun HaruHealthTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = IvorySeniorColorScheme,
    typography = AppTypography,
    content = content,
  )
}

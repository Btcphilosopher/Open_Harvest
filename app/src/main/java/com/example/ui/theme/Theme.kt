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
    primary = Color(0xFFE5DFD5),
    secondary = FreshLime,
    tertiary = HoneyGold,
    background = DeepForestDark,
    surface = SoftCharcoal,
    onPrimary = DeepForestDark,
    onSecondary = SoftWhite,
    onTertiary = DeepForestDark,
    onBackground = SoftWhite,
    onSurface = SoftWhite
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EcoGreen,
    secondary = ClayBrown,
    tertiary = HoneyGold,
    background = SoftWhite,
    surface = EditorialCardLight,
    onPrimary = SoftWhite,
    onSecondary = SoftWhite,
    onTertiary = SoftWhite,
    onBackground = SoftCharcoal,
    onSurface = SoftCharcoal
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color disabled to strictly respect custom "Editorial Aesthetic"
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

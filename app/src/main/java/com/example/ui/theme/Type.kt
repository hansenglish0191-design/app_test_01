package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val NotoSansKrFontFamily = FontFamily(Font(R.font.noto_sans_kr, FontWeight.Normal))

// All typography styles are >= 22sp for effortless readability for adults 60+.
val AppTypography =
  Typography(
    displayLarge =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 46.sp,
        lineHeight = 54.sp,
        letterSpacing = (-0.5).sp,
      ),
    headlineLarge =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.1.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.15.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = NotoSansKrFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 23.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.1.sp,
      ),
  )

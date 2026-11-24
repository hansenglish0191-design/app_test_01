package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.example.ui.theme.WaterCupEmptyBg
import com.example.ui.theme.WaterCupOutline
import com.example.ui.theme.WaterFillDeep
import com.example.ui.theme.WaterFillLight
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaterCupVisual(
  cups: Int,
  goalCups: Int,
  description: String,
  modifier: Modifier = Modifier,
) {
  val targetFraction = (cups.toFloat() / goalCups.coerceAtLeast(1)).coerceIn(0f, 1f)
  val animatedFill by
    animateFloatAsState(
      targetValue = targetFraction,
      animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing),
      label = "waterFill",
    )

  val infiniteTransition = rememberInfiniteTransition(label = "waterWave")
  val wavePhase by
    infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = (2f * PI).toFloat(),
      animationSpec =
        infiniteRepeatable(
          animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
          repeatMode = RepeatMode.Restart,
        ),
      label = "wavePhase",
    )

  Canvas(modifier = modifier.semantics { contentDescription = description }) {
    val w = size.width
    val h = size.height
    val strokeWidth = w * 0.065f
    val topInset = w * 0.10f
    val bottomInset = w * 0.19f
    val verticalPad = strokeWidth / 2f
    val cornerRadius = w * 0.14f

    // Tapered glass tumbler silhouette
    val cupPath =
      Path().apply {
        moveTo(topInset, verticalPad)
        lineTo(w - topInset, verticalPad)
        lineTo(w - bottomInset, h - verticalPad - cornerRadius)
        quadraticTo(
          w - bottomInset,
          h - verticalPad,
          w - bottomInset - cornerRadius,
          h - verticalPad,
        )
        lineTo(bottomInset + cornerRadius, h - verticalPad)
        quadraticTo(
          bottomInset,
          h - verticalPad,
          bottomInset,
          h - verticalPad - cornerRadius,
        )
        close()
      }

    // Empty glass background
    drawPath(path = cupPath, color = WaterCupEmptyBg)

    // Water fill clipped inside cup interior
    if (animatedFill > 0.01f) {
      val innerPad = strokeWidth * 0.7f
      val innerCupPath =
        Path().apply {
          moveTo(topInset + innerPad, verticalPad + innerPad)
          lineTo(w - topInset - innerPad, verticalPad + innerPad)
          lineTo(w - bottomInset - innerPad, h - verticalPad - cornerRadius)
          quadraticTo(
            w - bottomInset - innerPad,
            h - verticalPad - innerPad,
            w - bottomInset - cornerRadius,
            h - verticalPad - innerPad,
          )
          lineTo(bottomInset + cornerRadius, h - verticalPad - innerPad)
          quadraticTo(
            bottomInset + innerPad,
            h - verticalPad - innerPad,
            bottomInset + innerPad,
            h - verticalPad - cornerRadius,
          )
          close()
        }

      clipPath(innerCupPath) {
        val usableHeight = h - verticalPad * 2f - innerPad * 2f
        val waterTopY = (h - verticalPad - innerPad) - (usableHeight * animatedFill)
        val waveAmplitude = if (animatedFill >= 0.99f) 0f else h * 0.032f

        val wavePath =
          Path().apply {
            moveTo(0f, h)
            lineTo(0f, waterTopY)
            val steps = 24
            for (i in 0..steps) {
              val x = (i.toFloat() / steps) * w
              val yOffset = sin((x / w) * 2f * PI + wavePhase).toFloat() * waveAmplitude
              lineTo(x, waterTopY + yOffset)
            }
            lineTo(w, h)
            close()
          }

        drawPath(
          path = wavePath,
          brush =
            Brush.verticalGradient(
              colors = listOf(WaterFillLight, WaterFillDeep),
              startY = waterTopY,
              endY = h,
            ),
        )

        // Subtle glass shine streak on left side
        drawRoundRect(
          color = Color.White.copy(alpha = 0.28f),
          topLeft = Offset(w * 0.26f, waterTopY + h * 0.08f),
          size = Size(w * 0.07f, (h - waterTopY) * 0.65f),
          cornerRadius = CornerRadius(w * 0.04f, w * 0.04f),
        )
      }
    }

    // Crisp glass rim & outline
    drawPath(
      path = cupPath,
      color = WaterCupOutline,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )

    // Subtle measurement ticks on right edge for clarity
    val tickRight = w - bottomInset - w * 0.09f
    for (level in listOf(0.25f, 0.5f, 0.75f)) {
      val tickY = (h - verticalPad) - ((h - verticalPad * 2f) * level)
      drawLine(
        color = WaterCupOutline.copy(alpha = 0.35f),
        start = Offset(tickRight - w * 0.09f, tickY),
        end = Offset(tickRight, tickY),
        strokeWidth = strokeWidth * 0.45f,
        cap = StrokeCap.Round,
      )
    }
  }
}

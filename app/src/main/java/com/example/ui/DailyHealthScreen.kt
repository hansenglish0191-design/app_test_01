package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DailyHealthState
import com.example.data.MedicationSlot
import com.example.ui.components.WaterCupVisual
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.DeepInk
import com.example.ui.theme.DinnerBadgeBg
import com.example.ui.theme.DinnerTint
import com.example.ui.theme.IvoryBackground
import com.example.ui.theme.IvoryTopGlow
import com.example.ui.theme.LunchBadgeBg
import com.example.ui.theme.LunchTint
import com.example.ui.theme.MedDoneGreen
import com.example.ui.theme.MedDoneGreenBadge
import com.example.ui.theme.MedDoneGreenBorder
import com.example.ui.theme.MedDoneSoftSurface
import com.example.ui.theme.MorningBadgeBg
import com.example.ui.theme.MorningTint
import com.example.ui.theme.SecondaryInk
import com.example.ui.theme.WarmBorder
import com.example.ui.theme.WaterCardBg
import com.example.ui.theme.WaterCardBorder
import com.example.ui.theme.WaterFillDeep
import com.example.ui.theme.WaterPrimaryBlue

@Composable
fun DailyHealthScreen(
  uiState: HealthUiState,
  onMedicationClick: (MedicationSlot) -> Unit,
  onAddWaterClick: () -> Unit,
  onRemoveWaterClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val haptic = LocalHapticFeedback.current
  val health = uiState.healthState

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          brush =
            Brush.verticalGradient(
              colors = listOf(IvoryTopGlow, IvoryBackground, IvoryBackground),
            ),
        ),
  ) {
    Scaffold(
      containerColor = Color.Transparent,
      contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
      Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
      ) {
        Column(
          modifier =
            Modifier
              .fillMaxWidth()
              .widthIn(max = 600.dp)
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
          DateHeaderCard(
            dateTitle = uiState.dateTitle,
            dayOfWeekTitle = uiState.dayOfWeekTitle,
            takenCount = health.takenMedicationCount,
            allMedicationsTaken = health.allMedicationsTaken,
          )

          Spacer(modifier = Modifier.height(26.dp))

          Text(
            text = stringResource(R.string.med_section_title),
            style = MaterialTheme.typography.titleLarge,
            color = DeepInk,
            modifier = Modifier.padding(horizontal = 4.dp),
          )

          Spacer(modifier = Modifier.height(16.dp))

          Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            MedicationCheckButton(
              title = stringResource(R.string.med_morning),
              icon = Icons.Filled.WbTwilight,
              iconTint = MorningTint,
              iconBg = MorningBadgeBg,
              isTaken = health.morningTaken,
              testTag = "med_button_morning",
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onMedicationClick(MedicationSlot.MORNING)
              },
            )

            MedicationCheckButton(
              title = stringResource(R.string.med_lunch),
              icon = Icons.Filled.WbSunny,
              iconTint = LunchTint,
              iconBg = LunchBadgeBg,
              isTaken = health.lunchTaken,
              testTag = "med_button_lunch",
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onMedicationClick(MedicationSlot.LUNCH)
              },
            )

            MedicationCheckButton(
              title = stringResource(R.string.med_dinner),
              icon = Icons.Filled.NightsStay,
              iconTint = DinnerTint,
              iconBg = DinnerBadgeBg,
              isTaken = health.dinnerTaken,
              testTag = "med_button_dinner",
              onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onMedicationClick(MedicationSlot.DINNER)
              },
            )
          }

          Spacer(modifier = Modifier.height(28.dp))

          WaterIntakeSection(
            waterCups = health.waterCups,
            onAddWaterClick = {
              haptic.performHapticFeedback(HapticFeedbackType.LongPress)
              onAddWaterClick()
            },
            onRemoveWaterClick = {
              haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
              onRemoveWaterClick()
            },
          )

          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
private fun DateHeaderCard(
  dateTitle: String,
  dayOfWeekTitle: String,
  takenCount: Int,
  allMedicationsTaken: Boolean,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(26.dp),
    color = CreamSurface,
    border = BorderStroke(2.dp, WarmBorder),
    shadowElevation = 2.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Box(
          modifier =
            Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(IvoryTopGlow),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Filled.CalendarToday,
            contentDescription = null,
            tint = DeepInk,
            modifier = Modifier.size(26.dp),
          )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(
            text = dateTitle,
            style = MaterialTheme.typography.headlineLarge,
            color = DeepInk,
            modifier = Modifier.testTag("date_header_text"),
          )
          Text(
            text = dayOfWeekTitle,
            style = MaterialTheme.typography.titleLarge,
            color = SecondaryInk,
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      val summaryBg by
        animateColorAsState(
          targetValue = if (allMedicationsTaken) MedDoneSoftSurface else IvoryTopGlow,
          label = "summaryBg",
        )
      val summaryTextColor by
        animateColorAsState(
          targetValue = if (allMedicationsTaken) MedDoneGreenBadge else DeepInk,
          label = "summaryText",
        )

      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = summaryBg,
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (allMedicationsTaken) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = null,
              tint = MedDoneGreen,
              modifier = Modifier.size(26.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
          }
          Text(
            text =
              if (allMedicationsTaken) {
                stringResource(R.string.med_all_done_message)
              } else {
                stringResource(R.string.med_progress_format, takenCount)
              },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = summaryTextColor,
          )
        }
      }
    }
  }
}

@Composable
private fun MedicationCheckButton(
  title: String,
  icon: ImageVector,
  iconTint: Color,
  iconBg: Color,
  isTaken: Boolean,
  testTag: String,
  onClick: () -> Unit,
) {
  val containerColor by
    animateColorAsState(
      targetValue = if (isTaken) MedDoneGreen else CreamSurface,
      animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
      label = "medCardBg",
    )
  val borderColor by
    animateColorAsState(
      targetValue = if (isTaken) MedDoneGreenBorder else WarmBorder,
      label = "medCardBorder",
    )
  val titleColor by
    animateColorAsState(
      targetValue = if (isTaken) Color.White else DeepInk,
      label = "medTitleColor",
    )
  val statusColor by
    animateColorAsState(
      targetValue = if (isTaken) Color.White.copy(alpha = 0.96f) else SecondaryInk,
      label = "medStatusColor",
    )

  Surface(
    onClick = onClick,
    modifier =
      Modifier
        .fillMaxWidth()
        .heightIn(min = 96.dp)
        .minimumInteractiveComponentSize()
        .testTag(testTag)
        .semantics { role = Role.Checkbox },
    shape = RoundedCornerShape(24.dp),
    color = containerColor,
    border = BorderStroke(2.5.dp, borderColor),
    shadowElevation = if (isTaken) 4.dp else 2.dp,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 20.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Box(
        modifier =
          Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(if (isTaken) Color.White.copy(alpha = 0.20f) else iconBg),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isTaken) Color.White else iconTint,
          modifier = Modifier.size(32.dp),
        )
      }

      Spacer(modifier = Modifier.width(18.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineMedium,
          color = titleColor,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text =
            if (isTaken) {
              stringResource(R.string.med_status_done)
            } else {
              stringResource(R.string.med_status_pending)
            },
          style = MaterialTheme.typography.titleMedium,
          fontWeight = if (isTaken) FontWeight.Bold else FontWeight.Medium,
          color = statusColor,
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      if (isTaken) {
        AnimatedVisibility(
          visible = true,
          enter = fadeIn() + scaleIn(initialScale = 0.7f),
        ) {
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = stringResource(R.string.med_status_done),
            tint = Color.White,
            modifier = Modifier.size(44.dp),
          )
        }
      } else {
        Icon(
          imageVector = Icons.Filled.RadioButtonUnchecked,
          contentDescription = stringResource(R.string.med_status_pending),
          tint = WarmBorder,
          modifier = Modifier.size(42.dp),
        )
      }
    }
  }
}

@Composable
private fun WaterIntakeSection(
  waterCups: Int,
  onAddWaterClick: () -> Unit,
  onRemoveWaterClick: () -> Unit,
) {
  val goalCups = DailyHealthState.DAILY_WATER_GOAL
  val goalReached = waterCups >= goalCups

  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(28.dp),
    color = WaterCardBg,
    border = BorderStroke(2.dp, WaterCardBorder),
    shadowElevation = 2.dp,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(22.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Filled.WaterDrop,
            contentDescription = null,
            tint = WaterPrimaryBlue,
            modifier = Modifier.size(30.dp),
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = stringResource(R.string.water_section_title),
            style = MaterialTheme.typography.titleLarge,
            color = DeepInk,
          )
        }

        if (waterCups > 0) {
          OutlinedButton(
            onClick = onRemoveWaterClick,
            modifier =
              Modifier
                .heightIn(min = 50.dp)
                .minimumInteractiveComponentSize()
                .testTag("water_minus_button"),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, WaterCardBorder),
            colors =
              ButtonDefaults.outlinedButtonColors(
                containerColor = CreamSurface,
                contentColor = DeepInk,
              ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          ) {
            Text(
              text = stringResource(R.string.water_minus_button),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        WaterCupVisual(
          cups = waterCups,
          goalCups = goalCups,
          description = stringResource(R.string.water_cup_content_desc),
          modifier = Modifier.size(width = 96.dp, height = 114.dp),
        )

        Spacer(modifier = Modifier.width(26.dp))

        Column {
          Text(
            text = stringResource(R.string.water_cups_format, waterCups),
            fontSize = 46.sp,
            lineHeight = 54.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WaterPrimaryBlue,
            modifier = Modifier.testTag("water_count_text"),
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text =
              if (goalReached) {
                stringResource(R.string.water_goal_reached)
              } else {
                stringResource(R.string.water_goal_format, goalCups)
              },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (goalReached) FontWeight.Bold else FontWeight.Medium,
            color = if (goalReached) MedDoneGreenBadge else SecondaryInk,
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Visual row of 8 mini cup/drop indicators so seniors see each cup at a glance
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        for (cupIndex in 1..goalCups) {
          val filled = cupIndex <= waterCups
          Box(
            modifier =
              Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (filled) WaterFillDeep else Color.White)
                .padding(4.dp),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Filled.WaterDrop,
              contentDescription = null,
              tint = if (filled) Color.White else WaterCardBorder,
              modifier = Modifier.size(20.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      Button(
        onClick = onAddWaterClick,
        modifier =
          Modifier
            .fillMaxWidth()
            .heightIn(min = 78.dp)
            .minimumInteractiveComponentSize()
            .testTag("water_add_button"),
        shape = RoundedCornerShape(22.dp),
        colors =
          ButtonDefaults.buttonColors(
            containerColor = WaterPrimaryBlue,
            contentColor = Color.White,
          ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
      ) {
        Icon(
          imageVector = Icons.Filled.LocalDrink,
          contentDescription = null,
          modifier = Modifier.size(30.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
          text = stringResource(R.string.water_add_button),
          style = MaterialTheme.typography.labelLarge,
          color = Color.White,
        )
      }
    }
  }
}

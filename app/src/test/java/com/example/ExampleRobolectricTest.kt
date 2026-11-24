package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.core.app.ApplicationProvider
import com.example.data.DailyHealthRepository
import com.example.data.MedicationSlot
import com.example.ui.DailyHealthScreen
import com.example.ui.HealthViewModel
import com.example.ui.theme.HaruHealthTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `app name matches Korean title`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("하루 건강", appName)
  }

  @Test
  fun `repository persists today medication and water across new instances`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val fixedDate = "2026-11-17"

    val repo1 = DailyHealthRepository(context, dateProvider = { fixedDate })
    repo1.toggleMedication(MedicationSlot.MORNING)
    repo1.addWaterCup()
    repo1.addWaterCup()

    // Re-create repository to simulate app restart on the same day
    val repo2 = DailyHealthRepository(context, dateProvider = { fixedDate })
    val restored = repo2.dailyState.first()
    assertTrue(restored.morningTaken)
    assertFalse(restored.lunchTaken)
    assertEquals(2, restored.waterCups)

    // Next day resets records cleanly
    val repoNextDay = DailyHealthRepository(context, dateProvider = { "2026-11-18" })
    val nextDayState = repoNextDay.dailyState.first()
    assertFalse(nextDayState.morningTaken)
    assertEquals(0, nextDayState.waterCups)
  }

  @Test
  fun `ui toggles medication done and increments water count`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = DailyHealthRepository(context, dateProvider = { "2099-01-01" })
    val viewModel = HealthViewModel(repo, autoWatchMidnight = false)

    composeTestRule.setContent {
      HaruHealthTheme {
        val state = viewModel.uiState.collectAsStateWithLifecycle().value
        DailyHealthScreen(
          uiState = state,
          onMedicationClick = viewModel::onMedicationClick,
          onAddWaterClick = viewModel::onAddWaterClick,
          onRemoveWaterClick = viewModel::onRemoveWaterClick,
        )
      }
    }

    composeTestRule.onNodeWithTag("date_header_text").assertIsDisplayed()
    composeTestRule.onNodeWithTag("med_button_morning").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      runCatching {
          composeTestRule.onNodeWithText("복용 완료").assertIsDisplayed()
          true
        }
        .getOrDefault(false)
    }

    composeTestRule.onNodeWithTag("water_add_button").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      runCatching {
          composeTestRule.onNodeWithTag("water_count_text").performScrollTo().assertTextContains("1잔")
          true
        }
        .getOrDefault(false)
    }
  }

  @Test
  fun `midnight rollover automatically resets yesterday medications and water`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    var currentDay = "2027-03-10"
    val repo = DailyHealthRepository(context, dateProvider = { currentDay })
    val viewModel = HealthViewModel(repo, autoWatchMidnight = false)

    composeTestRule.setContent {
      HaruHealthTheme {
        val state = viewModel.uiState.collectAsStateWithLifecycle().value
        DailyHealthScreen(
          uiState = state,
          onMedicationClick = viewModel::onMedicationClick,
          onAddWaterClick = viewModel::onAddWaterClick,
          onRemoveWaterClick = viewModel::onRemoveWaterClick,
        )
      }
    }

    composeTestRule.onNodeWithTag("date_header_text").assertTextContains("2027년 3월 10일")
    composeTestRule.onNodeWithTag("med_button_morning").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      runCatching {
          composeTestRule.onNodeWithText("복용 완료").assertIsDisplayed()
          true
        }
        .getOrDefault(false)
    }

    composeTestRule.onNodeWithTag("water_add_button").performScrollTo().performClick()
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      runCatching {
          composeTestRule.onNodeWithTag("water_count_text").performScrollTo().assertTextContains("1잔")
          true
        }
        .getOrDefault(false)
    }

    // Simulate clock crossing midnight to next day
    currentDay = "2027-03-11"
    viewModel.refreshDateIfNeeded()

    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      runCatching {
          composeTestRule.onNodeWithTag("date_header_text").performScrollTo().assertTextContains("2027년 3월 11일")
          composeTestRule.onNodeWithTag("water_count_text").performScrollTo().assertTextContains("0잔")
          composeTestRule.onNodeWithText("복용 완료").assertDoesNotExist()
          true
        }
        .getOrDefault(false)
    }
  }
}

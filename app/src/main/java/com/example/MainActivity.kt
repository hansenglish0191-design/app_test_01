package com.example

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DailyHealthScreen
import com.example.ui.HealthViewModel
import com.example.ui.theme.HaruHealthTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HaruHealthTheme {
        val context = LocalContext.current
        val healthViewModel: HealthViewModel =
          viewModel(factory = HealthViewModel.factory(context))
        val uiState by healthViewModel.uiState.collectAsStateWithLifecycle()

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
          val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
              healthViewModel.refreshDateIfNeeded()
            }
          }
          lifecycleOwner.lifecycle.addObserver(observer)
          onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        DisposableEffect(context, healthViewModel) {
          val dateChangeReceiver =
            object : BroadcastReceiver() {
              override fun onReceive(ctx: Context?, intent: Intent?) {
                healthViewModel.refreshDateIfNeeded()
              }
            }
          val filter =
            IntentFilter().apply {
              addAction(Intent.ACTION_DATE_CHANGED)
              addAction(Intent.ACTION_TIME_CHANGED)
              addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }
          ContextCompat.registerReceiver(
            context,
            dateChangeReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
          )
          onDispose { context.unregisterReceiver(dateChangeReceiver) }
        }

        DailyHealthScreen(
          uiState = uiState,
          onMedicationClick = healthViewModel::onMedicationClick,
          onAddWaterClick = healthViewModel::onAddWaterClick,
          onRemoveWaterClick = healthViewModel::onRemoveWaterClick,
        )
      }
    }
  }
}

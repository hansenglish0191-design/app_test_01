package com.example.data

enum class MedicationSlot {
  MORNING,
  LUNCH,
  DINNER,
}

data class DailyHealthState(
  val dateKey: String,
  val morningTaken: Boolean = false,
  val lunchTaken: Boolean = false,
  val dinnerTaken: Boolean = false,
  val waterCups: Int = 0,
) {
  val takenMedicationCount: Int
    get() = listOf(morningTaken, lunchTaken, dinnerTaken).count { it }

  val allMedicationsTaken: Boolean
    get() = morningTaken && lunchTaken && dinnerTaken

  fun isTaken(slot: MedicationSlot): Boolean =
    when (slot) {
      MedicationSlot.MORNING -> morningTaken
      MedicationSlot.LUNCH -> lunchTaken
      MedicationSlot.DINNER -> dinnerTaken
    }

  companion object {
    const val DAILY_WATER_GOAL = 8
  }
}

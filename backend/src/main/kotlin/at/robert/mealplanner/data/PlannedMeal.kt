package at.robert.mealplanner.data

import java.time.LocalDate

data class PlannedMeal(
    val id: Int,
    val data: PlannedMealData,
    val day: LocalDate,
    val ordering: Int,
)

data class PlannedMealData(
    val desiredCalories: Int? = null,
    val people: Int = 1,
)

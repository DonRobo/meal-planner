package at.robert.mealplanner.service

import at.robert.mealplanner.data.PlannedMeal
import at.robert.mealplanner.data.PlannedMealData
import at.robert.mealplanner.repository.PlannedMealRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PlannedMealService(
    private val plannedMealRepository: PlannedMealRepository,
) {

    fun plannedMealsForDay(day: LocalDate): MutableList<PlannedMeal> {
        return plannedMealRepository.plannedMealsForDay(day)
    }

    fun addPlannedMeal(day: LocalDate, plannedMealData: PlannedMealData, insertBeforePlannedMealId: Int? = null) {
        plannedMealRepository.addPlannedMeal(day, plannedMealData, insertBeforePlannedMealId)
    }
}

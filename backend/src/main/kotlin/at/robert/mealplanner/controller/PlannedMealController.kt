package at.robert.mealplanner.controller

import at.robert.mealplanner.data.PlannedMeal
import at.robert.mealplanner.data.PlannedMealData
import at.robert.mealplanner.service.PlannedMealService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class PlannedMealController(
    private val plannedMealService: PlannedMealService,
) {

    @GetMapping("/planned-meals/{day}")
    fun plannedMealsForDay(@PathVariable("day") day: LocalDate): MutableList<PlannedMeal> {
        return plannedMealService.plannedMealsForDay(day)
    }

    @PostMapping("/planned-meals/{day}")
    fun addPlannedMeal(
        @PathVariable("day") day: LocalDate,
        @RequestBody plannedMealData: PlannedMealData,
        @RequestParam(required = false) insertBeforePlannedMealId: Int?,
    ) {
        plannedMealService.addPlannedMeal(day, plannedMealData, insertBeforePlannedMealId)
    }

}

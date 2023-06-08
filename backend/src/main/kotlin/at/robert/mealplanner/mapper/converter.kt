package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.records.JNutritionInfoRecord
import at.robert.mealplanner.data.NutritionData

fun JNutritionInfoRecord?.toNutritionData(): NutritionData {
    if (this == null) return NutritionData()

    return NutritionData(
        calories = this.calories,
        fat = this.fat,
        saturatedFat = this.saturatedFat,
        protein = this.protein,
        carbs = this.carbs,
        sugar = this.sugar,
        salt = this.salt,
    )
}

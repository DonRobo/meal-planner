package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.records.JNutritionDataRecord
import at.robert.mealplanner.data.NutritionData

fun JNutritionDataRecord?.toNutritionData(): NutritionData {
    if (this == null) return NutritionData()

    return NutritionData(
        id = this.id,
        calories = this.calories,
        fat = this.fat,
        saturatedFat = this.saturatedFat,
        protein = this.protein,
        carbs = this.carbs,
        sugar = this.sugar,
        salt = this.salt,
        vegetarian = this.vegetarian,
        vegan = this.vegan,
    )
}

package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.JIngredient
import at.robert.mealplanner.data.Ingredient
import at.robert.mealplanner.data.NutritionData
import org.jooq.Record
import org.jooq.RecordMapper

class IngredientMapper(
    private val i: JIngredient,
) : RecordMapper<Record, Ingredient> {

    val fields = listOf(
        i.ID,
        i.NAME,
        i.IMAGE_URL,
        i.CALORIES,
        i.FAT,
        i.SATURATED_FAT,
        i.PROTEIN,
        i.CARBS,
        i.SUGAR,
        i.SALT,
        i.VEGETARIAN,
        i.VEGAN,
    )

    override fun map(r: Record): Ingredient {
        return Ingredient(
            id = r[i.ID],
            name = r[i.NAME],
            imageUrl = r[i.IMAGE_URL],
            nutrition = NutritionData(
                calories = r[i.CALORIES],
                fat = r[i.FAT],
                saturatedFat = r[i.SATURATED_FAT],
                protein = r[i.PROTEIN],
                carbs = r[i.CARBS],
                sugar = r[i.SUGAR],
                salt = r[i.SALT],
                vegetarian = r[i.VEGETARIAN],
                vegan = r[i.VEGAN],
            )
        )
    }
}

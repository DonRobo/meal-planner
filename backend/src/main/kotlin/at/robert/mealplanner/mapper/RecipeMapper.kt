package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.JRecipe
import at.robert.mealplanner.data.NutritionData
import at.robert.mealplanner.data.Recipe
import at.robert.mealplanner.data.RecipeStep
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.Record
import org.jooq.RecordMapper

class RecipeMapper(
    private val r: JRecipe,
    private val objectMapper: ObjectMapper,
) : RecordMapper<Record, Recipe> {

    val fields = listOf(
        r.ID,
        r.LINK,
        r.NAME,
        r.DESCRIPTION,
        r.IMAGE_URL,
        r.STEPS,
        r.PREP_TIME,
        r.COOK_TIME,
        r.TOTAL_TIME,
        r.CALORIES,
        r.FAT,
        r.SATURATED_FAT,
        r.PROTEIN,
        r.CARBS,
        r.SUGAR,
        r.SALT,
        r.VEGETARIAN,
        r.VEGAN,
    )

    override fun map(record: Record): Recipe {
        val steps = parseSteps(record[r.STEPS].data())

        val nutritionData = NutritionData(
            calories = record[r.CALORIES],
            fat = record[r.FAT],
            saturatedFat = record[r.SATURATED_FAT],
            protein = record[r.PROTEIN],
            carbs = record[r.CARBS],
            sugar = record[r.SUGAR],
            salt = record[r.SALT],
            vegetarian = record[r.VEGETARIAN],
            vegan = record[r.VEGAN],
        )

        return Recipe(
            id = record[r.ID],
            link = record[r.LINK],
            name = record[r.NAME],
            description = record[r.DESCRIPTION],
            imageUrl = record[r.IMAGE_URL],
            steps = steps,
            ingredients = emptyList(), // this must be filled later on
            nutritionData = nutritionData,
            prepTime = record[r.PREP_TIME],
            cookTime = record[r.COOK_TIME],
            totalTime = record[r.TOTAL_TIME],
        )
    }

    private fun parseSteps(stepsJson: String): List<RecipeStep> = objectMapper.readValue<List<RecipeStep>>(stepsJson)

}

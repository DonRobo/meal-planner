package at.robert.mealplanner.service

import at.robert.mealplanner.data.Ingredient
import at.robert.mealplanner.data.NutritionData
import at.robert.mealplanner.data.Recipe
import at.robert.mealplanner.data.SparseRecipe
import at.robert.mealplanner.repository.RecipeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
) {
    fun getRecipe(recipeId: Int): Recipe {
        return recipeRepository.getRecipe(recipeId)
    }

    @Transactional
    fun createRecipe(recipe: Recipe): Int {
        val existingId = if (recipe.link != null) {
            recipeRepository.getRecipeIdByUrl(recipe.link)
        } else {
            null
        }

        val nutritionData = recipe.nutritionData

        val recipeRecord = recipeRepository.upsertRecipe(
            id = existingId,
            name = recipe.name,
            description = recipe.description,
            imageUrl = recipe.imageUrl,
            link = recipe.link,
            prepTime = recipe.prepTime,
            cookTime = recipe.cookTime,
            totalTime = recipe.totalTime,
            steps = recipe.steps,

            nutritionData = nutritionData,
        )

        recipeRepository.clearIngredientsForRecipe(recipeRecord.id)
        recipe.ingredients!!.forEach { recipeIngredient ->
            val ingredient = if (recipeIngredient.ingredient.id > 0)
                recipeIngredient.ingredient
            else
                getOrCreateIngredient(
                    ingredientName = recipeIngredient.ingredient.name,
                    imageUrl = recipeIngredient.ingredient.imageUrl,
                    nutritionData = recipeIngredient.ingredient.nutrition,
                )
            val ingredientId = ingredient.id

            recipeRepository.insertRecipeIngredient(
                recipeId = recipeRecord.id,
                ingredientId = ingredientId,
                quantity = recipeIngredient.quantity,
                unit = recipeIngredient.unit,
            )
        }

        return recipeRecord.id
    }

    fun getIngredient(ingredientId: Int): Ingredient {
        return recipeRepository.getIngredients(listOf(ingredientId)).single()
    }

    fun getOrCreateIngredient(
        ingredientName: String,
        imageUrl: String?,
        nutritionData: NutritionData?,
    ): Ingredient {
        val existing = recipeRepository.getIngredientIdByName(ingredientName)

        return if (existing != null) {
            getIngredient(existing)
        } else {
            val ingredientRecord = recipeRepository.createIngredient(
                name = ingredientName,
                imageUrl = imageUrl,
                nutritionData = nutritionData ?: NutritionData(),
            )
            getIngredient(ingredientRecord.id)
        }
    }

    fun listRecipes(): List<SparseRecipe> {
        return recipeRepository.listSparseRecipes()
    }
}

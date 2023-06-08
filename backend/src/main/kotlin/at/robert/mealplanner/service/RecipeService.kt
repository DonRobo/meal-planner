package at.robert.mealplanner.service

import at.robert.mealplanner.data.*
import at.robert.mealplanner.mapper.toNutritionData
import at.robert.mealplanner.repository.RecipeRepository
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
) {
    fun getRecipe(recipeId: Int): Recipe {
        val recipe = recipeRepository.getRecipe(recipeId) ?: error("Recipe not found")
        val steps = recipeRepository.getRecipeSteps(recipeId)
        val recipeIngredients = recipeRepository.getRecipeIngredients(recipeId)
        val ingredients =
            recipeRepository.getIngredients(recipeIngredients.map { it.ingredientId }.distinct()).associateBy { it.id }
        val recipeNutrition = recipeRepository.getRecipeNutrition(recipeId)
        val ingredientNutrition = recipeRepository.getIngredientNutritions(ingredients.keys)

        val ingredientDtos = recipeIngredients.map { recipeIngredient ->
            val ingredient = ingredients[recipeIngredient.ingredientId] ?: error("Ingredient not found")
            val nutrition = ingredientNutrition[recipeIngredient.ingredientId].toNutritionData()

            val ingredientDto = Ingredient(
                id = ingredient.id,
                name = ingredient.name,
                imageUrl = ingredient.imageUrl,
                nutrition = nutrition,
            )

            RecipeIngredient(
                ingredient = ingredientDto,
                quantity = recipeIngredient.quantity,
                unit = recipeIngredient.unit,
            )
        }

        val stepDtos = steps.map { step ->
            RecipeStep(
                text = step.description,
                image = step.imageUrl
            )
        }

        return Recipe(
            id = recipe.id,
            url = recipe.link,
            name = recipe.name,
            description = recipe.description,
            image = recipe.imageUrl,
            nutrition = recipeNutrition.toNutritionData(),
            ingredients = ingredientDtos,
            steps = stepDtos,
            prepTime = recipe.prepTime,
            cookTime = recipe.cookTime,
            totalTime = recipe.totalTime,
        )
    }

    fun createRecipe(recipe: Recipe): Int {
        val existingId = if (recipe.url != null) {
            recipeRepository.getRecipeByUrl(recipe.url).id
        } else {
            null
        }

        val recipeRecord = recipeRepository.upsertRecipe(
            id = existingId,
            name = recipe.name,
            description = recipe.description,
            imageUrl = recipe.image,
            link = recipe.url,
            prepTime = recipe.prepTime,
            cookTime = recipe.cookTime,
            totalTime = recipe.totalTime,
        )

        return recipeRecord.id
    }

    fun getOrCreateIngredient(ingredientName: String): Ingredient {
        val existing = recipeRepository.getIngredientByName(ingredientName)
        TODO()
    }

    fun getOrCreateNutritionData(nutritionData: NutritionData): NutritionData {
        TODO("Not yet implemented")
    }

}

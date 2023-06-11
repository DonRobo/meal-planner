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
            val nutrition = ingredientNutrition[ingredient.nutritionDataId].toNutritionData()

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
            imageUrl = recipe.imageUrl,
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
            recipeRepository.getRecipeByUrl(recipe.url)?.id
        } else {
            null
        }

        val nutritionData = recipe.nutrition.persisted()

        val recipeRecord = recipeRepository.upsertRecipe(
            id = existingId,
            name = recipe.name,
            description = recipe.description,
            imageUrl = recipe.imageUrl,
            link = recipe.url,
            prepTime = recipe.prepTime,
            cookTime = recipe.cookTime,
            totalTime = recipe.totalTime,
            nutritionDataId = nutritionData.id,
        )

        recipeRepository.clearStepsForRecipe(recipeRecord.id)
        recipe.steps.forEachIndexed { index, step ->
            recipeRepository.insertRecipeStep(
                recipeId = recipeRecord.id,
                stepNumber = index + 1,
                description = step.text,
                imageUrl = step.image,
            )
        }

        recipeRepository.clearIngredientsForRecipe(recipeRecord.id)
        recipe.ingredients.forEach { recipeIngredient ->
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
        val ing = recipeRepository.getIngredients(listOf(ingredientId)).single()
        val nutritionData = recipeRepository.getIngredientNutritions(listOf(ingredientId))
            .values
            .single()
            .toNutritionData()

        return Ingredient(
            id = ing.id,
            name = ing.name,
            imageUrl = ing.imageUrl,
            nutrition = nutritionData,
        )
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
            val nd = nutritionData.persisted()

            val ingredientRecord = recipeRepository.createIngredient(
                name = ingredientName,
                imageUrl = imageUrl,
                nutritionDataId = nd.id
            )
            getIngredient(ingredientRecord.id)
        }
    }

    fun getOrCreateNutritionData(nutritionData: NutritionData?): NutritionData {
        return if (nutritionData == null || nutritionData.id <= 0) {
            val nd = nutritionData ?: NutritionData()
            val nutritionRecord = recipeRepository.findByNutritionData(nd) ?: recipeRepository.createNutritionData(nd)

            nutritionRecord.toNutritionData()
        } else {
            nutritionData
        }
    }

    fun listRecipes(): List<SparseRecipe> {
        return recipeRepository.listRecipes()
            .map {
                SparseRecipe(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    imageUrl = it.imageUrl,
                )
            }
    }

    private fun NutritionData?.persisted(): NutritionData = getOrCreateNutritionData(this)
}

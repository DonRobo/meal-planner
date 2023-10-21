package at.robert.mealplanner.data

data class Recipe(
    val id: Int,
    val link: String?,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val steps: List<RecipeStep>,
    val ingredients: List<RecipeIngredient>,
    val nutritionData: NutritionData,
    val prepTime: Int?,
    val cookTime: Int?,
    val totalTime: Int?,
)

data class SparseRecipe(
    val id: Int,
    val name: String,
    val description: String?,
    val imageUrl: String?,
)

data class RecipeStep(
    val text: String,
    val image: String?
)

data class RecipeIngredient(
    val ingredient: Ingredient,
    val quantity: Float,
    val unit: String,
)

data class Ingredient(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val nutrition: NutritionData,
)

data class NutritionData(
    val calories: Int?,
    val fat: Float?,
    val saturatedFat: Float?,
    val protein: Float?,
    val carbs: Float?,
    val sugar: Float?,
    val salt: Float?,
    val vegetarian: Boolean?,
    val vegan: Boolean?,
) {
    constructor() : this(null, null, null, null, null, null, null, null, null)
}

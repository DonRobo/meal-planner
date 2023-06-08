package at.robert.mealplanner.data

data class Recipe(
    val id: Int,
    val url: String?,
    val name: String,
    val description: String,
    val image: String?,
    val steps: List<RecipeStep>,
    val ingredients: List<RecipeIngredient>,
    val nutrition: NutritionData,
)

data class RecipeStep(
    val text: String,
    val image: String?
)

data class RecipeIngredient(
    val ingredient: Ingredient,
    val amount: String,
    val unit: String,
)

data class Ingredient(
    val id: Int,
    val name: String,
    val image: String?,
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
) {
    constructor() : this(null, null, null, null, null, null, null)
}

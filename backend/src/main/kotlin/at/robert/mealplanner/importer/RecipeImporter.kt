package at.robert.mealplanner.importer

interface RecipeImporter {
    fun supports(url: String): Boolean
    fun importRecipe(url: String): Int

}

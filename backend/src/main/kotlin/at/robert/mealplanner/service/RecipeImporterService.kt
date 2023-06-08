package at.robert.mealplanner.service

import at.robert.mealplanner.data.Recipe
import at.robert.mealplanner.importer.RecipeImporter
import org.springframework.stereotype.Service

@Service
class RecipeImporterService(
    private val recipeService: RecipeService,
    private val recipeImporters: List<RecipeImporter>,
) {

    fun importRecipe(url: String): Recipe {
        val recipeImporter = recipeImporters.filter { it.supports(url) }

        when (recipeImporter.size) {
            0 -> error("No importer found for url $url")
            1 -> {
                val imported = recipeImporter.single().importRecipe(url)
                return recipeService.getRecipe(imported)
            }

            else -> error("Multiple importers found for url $url: $recipeImporter")
        }
    }

}

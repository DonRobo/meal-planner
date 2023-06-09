package at.robert.mealplanner.controller

import at.robert.mealplanner.data.Recipe
import at.robert.mealplanner.data.SparseRecipe
import at.robert.mealplanner.service.RecipeImporterService
import at.robert.mealplanner.service.RecipeService
import org.springframework.web.bind.annotation.*

@RequestMapping("/recipes")
@RestController
class RecipeController(
    private val recipeService: RecipeService,
    private val recipeImporterService: RecipeImporterService,
) {

    @GetMapping
    fun listRecipes(): List<SparseRecipe> {
        return recipeService.listRecipes()
    }

    @GetMapping("{recipeId}")
    fun getRecipe(@PathVariable recipeId: Int): Recipe {
        return recipeService.getRecipe(recipeId)
    }

    @PostMapping("import")
    fun importRecipe(@RequestParam url: String): Recipe {
        return recipeImporterService.importRecipe(url)
    }
}

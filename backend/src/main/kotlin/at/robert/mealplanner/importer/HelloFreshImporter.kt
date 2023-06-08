package at.robert.mealplanner.importer

import at.robert.mealplanner.data.NutritionData
import at.robert.mealplanner.data.Recipe
import at.robert.mealplanner.data.RecipeIngredient
import at.robert.mealplanner.data.RecipeStep
import at.robert.mealplanner.service.RecipeService
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.net.URL

@Component
class HelloFreshImporter(
    private val recipeService: RecipeService,
) : RecipeImporter {
    override fun supports(url: String): Boolean {
        return url.startsWith("https://www.hellofresh.at/recipes/")
    }

    override fun importRecipe(url: String): Int {
        val doc = Jsoup.parse(URL(url), 10000)
        val title = doc.selectFirst("h1")?.text() ?: error("No title found")
        val description = doc.selectFirst("span[data-test-id='description-body-title'] p")?.text()

        val ingredientListDiv = doc.selectFirst("div[data-test-id='ingredients-list']") ?: error("No ingredients found")
        val ingredientDivs = ingredientListDiv.select("div[data-test-id='ingredient-item-shipped']")

        val portions = 2
        val ingredients = ingredientDivs.map {
            val ps = it.select("p")
            val amount = ps[0].text()
            val name = ps[1].text()

            name to amount
        }

        val stepsDiv = doc.selectFirst("div[data-test-id='instructions']") ?: error("No steps found")
        val stepDivs = stepsDiv.select("div[data-test-id='instruction-step']")

        val steps = stepDivs.map { div ->
            val p = div.select("p")
            val image = div.select("img").last()

            RecipeStep(p.text(), image?.attr("src"))
        }

        val recipe = Recipe(
            id = 0,
            name = title,
            description = description,
            url = url,
            image = null, //TODO
            steps = steps,
            ingredients = ingredients.map { (ingredientName, amount) ->
                val split = amount.split(" ")
                RecipeIngredient(
                    ingredient = recipeService.getOrCreateIngredient(ingredientName),
                    quantity = split[0],
                    unit = split[1],
                )
            },
            nutrition = recipeService.getOrCreateNutritionData(NutritionData()),
            prepTime = null, //TODO
            cookTime = null,
            totalTime = null, //TODO
        )

        return recipeService.createRecipe(recipe)
    }
}

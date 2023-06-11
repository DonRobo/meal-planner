package at.robert.mealplanner.importer

import at.robert.mealplanner.data.NutritionData
import at.robert.mealplanner.data.Recipe
import at.robert.mealplanner.data.RecipeIngredient
import at.robert.mealplanner.data.RecipeStep
import at.robert.mealplanner.parsePortions
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

        val nutritionDiv = doc.selectFirst("div[data-test-id='items-per-serving']") ?: error("No nutrition found")
        val nutritionData = nutritionDiv.select("div[data-test-id='nutrition-step']").map {
            val name = it.child(0).text()
            val value = it.child(1).text()

            name to value
        }.toMap()

        val nd = NutritionData(
            id = -1,
            calories = nutritionData["Energie (kcal)"]?.replace(" kcal", "")?.toInt(),
            fat = nutritionData["Fett"]?.replace(" g", "")?.toFloat(),
            saturatedFat = nutritionData["davon gesättigte Fettsäuren"]?.replace(" g", "")?.toFloat(),
            protein = nutritionData["Eiweiß"]?.replace(" g", "")?.toFloat(),
            carbs = nutritionData["Kohlenhydrate"]?.replace(" g", "")?.toFloat(),
            sugar = nutritionData["davon Zucker"]?.replace(" g", "")?.toFloat(),
            salt = nutritionData["Salz"]?.replace(" g", "")?.toFloat(),
            vegetarian = null, //TODO
            vegan = null, //TODO
        )

        val recipe = Recipe(
            id = -1,
            name = title,
            description = description,
            url = url,
            imageUrl = null, //TODO
            steps = steps,
            ingredients = ingredients.map { (ingredientName, amount) ->
                val split = amount.split(" ")
                RecipeIngredient(
                    ingredient = recipeService.getOrCreateIngredient(
                        ingredientName = ingredientName,
                        imageUrl = null, //TODO
                        nutritionData = null, //TODO
                    ),
                    quantity = split[0].parsePortions() / portions,
                    unit = split[1],
                )
            },
            nutritionData = recipeService.getOrCreateNutritionData(nd),
            prepTime = null, //TODO
            cookTime = null,
            totalTime = null, //TODO
        )

        return recipeService.createRecipe(recipe)
    }
}

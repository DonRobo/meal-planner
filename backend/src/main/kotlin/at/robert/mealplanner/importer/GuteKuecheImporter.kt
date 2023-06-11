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
class GuteKuecheImporter(
    private val recipeService: RecipeService,
) : RecipeImporter {
    override fun supports(url: String): Boolean {
        return url.startsWith("https://www.gutekueche.at/")
    }

    override fun importRecipe(url: String): Int {
        val doc = Jsoup.parse(URL(url), 10000)
        val h1 = doc.selectFirst("h1") ?: error("No title found")
        val title = h1.text()
        val description = h1.nextElementSibling()?.text()

        val ingredientsDiv = doc.selectFirst("div.ingredients-table") ?: error("No ingredients found")
        val ingredientsTrs = ingredientsDiv.select("tr")

        val portions = doc.selectFirst("span.portions")?.text()?.toInt() ?: error("No portions found")

        val ingredients = ingredientsTrs.map {
            val quantity = it.selectFirst("td") ?: error("No quantity found for $it")
            val ths = it.select("th")
            val unit = ths[0]
            val name = ths[1]

            RecipeIngredient(
                ingredient = recipeService.getOrCreateIngredient(
                    ingredientName = name.text(),
                    imageUrl = null, //TODO
                    nutritionData = null, //TODO
                ),
                quantity = quantity.text().toFloat() / portions,
                unit = unit.text()
            )
        }

        val stepElements = doc.select(".rezept-preperation li")
        val steps = stepElements.map {
            RecipeStep(
                text = it.text(),
                image = null,
            )
        }

        return recipeService.createRecipe(
            Recipe(
                id = -1,
                url = url,
                name = title,
                description = description,
                imageUrl = null, //TODO
                steps = steps,
                ingredients = ingredients,
                nutrition = NutritionData(), //TODO
                prepTime = null, //TODO
                cookTime = null, //TODO
                totalTime = null, //TODO
            )
        )
    }
}

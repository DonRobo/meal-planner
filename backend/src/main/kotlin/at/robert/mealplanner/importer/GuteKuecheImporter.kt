package at.robert.mealplanner.importer

import org.springframework.stereotype.Component

@Component
class GuteKuecheImporter : RecipeImporter {
    override fun supports(url: String): Boolean {
        return url.startsWith("https://www.gutekueche.at/")
    }

    override fun importRecipe(url: String): Int {
        TODO("Not yet implemented")
    }
}

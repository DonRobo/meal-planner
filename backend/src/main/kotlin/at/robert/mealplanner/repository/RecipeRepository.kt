package at.robert.mealplanner.repository

import at.robbert.mealplanner.jooq.Tables
import at.robbert.mealplanner.jooq.tables.records.JIngredientRecord
import at.robbert.mealplanner.jooq.tables.records.JRecipeRecord
import at.robert.mealplanner.data.*
import at.robert.mealplanner.mapper.IngredientMapper
import at.robert.mealplanner.mapper.RecipeIngredientMapper
import at.robert.mealplanner.mapper.RecipeMapper
import at.robert.mealplanner.mapper.SparseRecipeMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.DSLContext
import org.jooq.JSONB
import org.springframework.stereotype.Repository

@Repository
class RecipeRepository(
    private val ctx: DSLContext,
    private val objectMapper: ObjectMapper,
) {
    private val r = Tables.RECIPE.`as`("r")
    private val ri = Tables.RECIPE_INGREDIENT.`as`("ri")
    private val i = Tables.INGREDIENT.`as`("i")

    private val iMapper = IngredientMapper(i)
    private val riMapper = RecipeIngredientMapper(ri, iMapper)
    private val rMapper = RecipeMapper(r, objectMapper)
    private val sparseRMapper = SparseRecipeMapper(r)

    fun getRecipe(recipeId: Int): Recipe {
        val recipe = ctx.select(rMapper.fields)
            .from(r)
            .where(r.ID.eq(recipeId))
            .fetchOne(rMapper) ?: error("Recipe not found")

        return recipe.copy(ingredients = getRecipeIngredients(recipeId))
    }

    fun getRecipeIngredients(recipeId: Int): List<RecipeIngredient> {
        return ctx.select(riMapper.fields)
            .from(ri)
            .join(i).on(ri.INGREDIENT_ID.eq(i.ID))
            .where(ri.RECIPE_ID.eq(recipeId))
            .orderBy(ri.INGREDIENT_ID)
            .fetch(riMapper)
    }

    fun upsertRecipe(
        id: Int?,
        name: String,
        description: String?,
        imageUrl: String?,
        link: String?,
        prepTime: Int?,
        cookTime: Int?,
        totalTime: Int?,
        steps: List<RecipeStep>,

        nutritionData: NutritionData,
    ): JRecipeRecord {
        val record = ctx.newRecord(r).apply {
            this.name = name
            this.description = description
            this.imageUrl = imageUrl
            this.link = link
            this.prepTime = prepTime
            this.cookTime = cookTime
            this.totalTime = totalTime
            this.steps = JSONB.jsonb(objectMapper.writeValueAsString(steps))

            this.calories = nutritionData.calories
            this.fat = nutritionData.fat
            this.saturatedFat = nutritionData.saturatedFat
            this.protein = nutritionData.protein
            this.carbs = nutritionData.carbs
            this.sugar = nutritionData.sugar
            this.salt = nutritionData.salt
            this.vegan = nutritionData.vegan
            this.vegetarian = nutritionData.vegetarian
        }

        return if (id != null)
            ctx.update(r)
                .set(record)
                .where(r.ID.eq(id))
                .returning().fetchOne()!!
        else
            ctx.insertInto(r)
                .set(record)
                .returning().fetchOne()!!
    }

    fun getRecipeIdByUrl(url: String): Int? {
        return ctx.select(r.ID)
            .from(r)
            .where(r.LINK.eq(url))
            .fetchOne(r.ID)
    }

    fun getRecipeByUrl(url: String): Recipe? {
        return ctx.select(rMapper.fields)
            .from(r)
            .where(r.LINK.eq(url))
            .fetchOne(rMapper)
    }

    fun getIngredientIdByName(ingredientName: String): Int? {
        return ctx.select(i.ID)
            .from(i)
            .where(i.NAME.eq(ingredientName))
            .fetchOne(i.ID)
    }

    fun createIngredient(
        name: String,
        imageUrl: String?,

        nutritionData: NutritionData,
    ): JIngredientRecord {
        val record = ctx.newRecord(i).apply {
            this.name = name
            this.imageUrl = imageUrl

            this.calories = nutritionData.calories
            this.fat = nutritionData.fat
            this.saturatedFat = nutritionData.saturatedFat
            this.protein = nutritionData.protein
            this.carbs = nutritionData.carbs
            this.sugar = nutritionData.sugar
            this.salt = nutritionData.salt
            this.vegan = nutritionData.vegan
            this.vegetarian = nutritionData.vegetarian
        }

        return ctx.insertInto(i)
            .set(record)
            .returning().fetchOne()!!
    }

    fun clearIngredientsForRecipe(recipeId: Int) {
        ctx.deleteFrom(ri)
            .where(ri.RECIPE_ID.eq(recipeId))
            .execute()
    }

    fun insertRecipeIngredient(recipeId: Int, ingredientId: Int, quantity: Float, unit: String) {
        val record = ctx.newRecord(ri).apply {
            this.recipeId = recipeId
            this.ingredientId = ingredientId
            this.quantity = quantity
            this.unit = unit
        }

        ctx.insertInto(ri)
            .set(record)
            .execute()
    }

    fun listSparseRecipes(): List<SparseRecipe> {
        return ctx.select(sparseRMapper.fields)
            .from(r)
            .orderBy(r.NAME)
            .fetch(sparseRMapper)
    }

    fun getIngredients(ingredientIds: List<Int>): List<Ingredient> {
        return ctx.select(iMapper.fields)
            .from(i)
            .where(i.ID.`in`(ingredientIds))
            .fetch(iMapper)
    }

}

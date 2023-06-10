package at.robert.mealplanner.repository

import at.robbert.mealplanner.jooq.Tables
import at.robbert.mealplanner.jooq.tables.records.*
import at.robert.mealplanner.data.NutritionData
import at.robert.mealplanner.eqOrIsNull
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class RecipeRepository(
    private val ctx: DSLContext,
) {
    private val r = Tables.RECIPE.`as`("r")
    private val rs = Tables.RECIPE_STEP.`as`("rs")
    private val ri = Tables.RECIPE_INGREDIENT.`as`("ri")
    private val i = Tables.INGREDIENT.`as`("i")
    private val nd = Tables.NUTRITION_DATA.`as`("nd")

    fun getRecipe(recipeId: Int): JRecipeRecord? {
        return ctx.selectFrom(r)
            .where(r.ID.eq(recipeId))
            .fetchOne()
    }

    fun getRecipeSteps(recipeId: Int): List<JRecipeStepRecord> {
        return ctx.selectFrom(rs)
            .where(rs.RECIPE_ID.eq(recipeId))
            .orderBy(rs.STEP_NUMBER)
            .fetch()
    }

    fun getRecipeIngredients(recipeId: Int): List<JRecipeIngredientRecord> {
        return ctx.selectFrom(ri)
            .where(ri.RECIPE_ID.eq(recipeId))
            .orderBy(ri.INGREDIENT_ID)
            .fetch()
    }

    fun getIngredients(ingredientIds: List<Int>): List<JIngredientRecord> {
        return ctx.selectFrom(i)
            .where(i.ID.`in`(ingredientIds))
            .orderBy(i.NAME)
            .fetch()
    }

    fun getRecipeNutrition(recipeId: Int): JNutritionDataRecord? {
        return ctx.select(*nd.fields())
            .from(nd)
            .join(r).on(nd.ID.eq(r.NUTRITION_DATA_ID))
            .where(r.ID.eq(recipeId))
            .fetchOneInto(nd)
    }

    fun getIngredientNutritions(ingredientIds: Collection<Int>): Map<Int, JNutritionDataRecord> {
        return ctx.select(*nd.fields())
            .from(nd)
            .join(i).on(nd.ID.eq(i.NUTRITION_DATA_ID))
            .where(i.ID.`in`(ingredientIds))
            .fetchInto(nd)
            .associateBy { it.id }
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
        nutritionDataId: Int
    ): JRecipeRecord {
        val record = ctx.newRecord(r).apply {
            this.name = name
            this.description = description
            this.imageUrl = imageUrl
            this.link = link
            this.prepTime = prepTime
            this.cookTime = cookTime
            this.totalTime = totalTime
            this.nutritionDataId = nutritionDataId
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

    fun getRecipeByUrl(url: String): JRecipeRecord? {
        return ctx.selectFrom(r)
            .where(r.LINK.eq(url))
            .fetchOne()
    }

    fun getIngredientIdByName(ingredientName: String): Int? {
        return ctx.select(i.ID)
            .from(i)
            .where(i.NAME.eq(ingredientName))
            .fetchOne(i.ID)
    }

    fun createIngredient(
        name: String,
        vegetarian: Boolean?,
        vegan: Boolean?,
        imageUrl: String?,
        nutritionDataId: Int
    ): JIngredientRecord {
        val record = ctx.newRecord(i).apply {
            this.name = name
            this.vegetarian = vegetarian
            this.vegan = vegan
            this.imageUrl = imageUrl
            this.nutritionDataId = nutritionDataId
        }

        return ctx.insertInto(i)
            .set(record)
            .returning().fetchOne()!!
    }

    fun getNutritionData(nutritionDataId: Int): JNutritionDataRecord? {
        return ctx.selectFrom(nd)
            .where(nd.ID.eq(nutritionDataId))
            .fetchOne()
    }

    fun findByNutritionData(nutritionData: NutritionData): JNutritionDataRecord? {
        return ctx.selectFrom(nd)
            .where(nd.CALORIES.eqOrIsNull(nutritionData.calories))
            .and(nd.FAT.eqOrIsNull(nutritionData.fat))
            .and(nd.SATURATED_FAT.eqOrIsNull(nutritionData.saturatedFat))
            .and(nd.PROTEIN.eqOrIsNull(nutritionData.protein))
            .and(nd.CARBS.eqOrIsNull(nutritionData.carbs))
            .and(nd.SUGAR.eqOrIsNull(nutritionData.sugar))
            .and(nd.SALT.eqOrIsNull(nutritionData.salt))
            .fetchOne()
    }

    fun createNutritionData(nutritionData: NutritionData): JNutritionDataRecord {
        val record = ctx.newRecord(nd).apply {
            this.calories = nutritionData.calories
            this.fat = nutritionData.fat
            this.saturatedFat = nutritionData.saturatedFat
            this.protein = nutritionData.protein
            this.carbs = nutritionData.carbs
            this.sugar = nutritionData.sugar
            this.salt = nutritionData.salt
        }

        return ctx.insertInto(nd)
            .set(record)
            .returning().fetchOne()!!
    }

    fun clearStepsForRecipe(recipeId: Int) {
        ctx.deleteFrom(rs)
            .where(rs.RECIPE_ID.eq(recipeId))
            .execute()
    }

    fun insertRecipeStep(recipeId: Int, stepNumber: Int, description: String, imageUrl: String?) {
        val record = ctx.newRecord(rs).apply {
            this.recipeId = recipeId
            this.stepNumber = stepNumber
            this.description = description
            this.imageUrl = imageUrl
        }

        ctx.insertInto(rs)
            .set(record)
            .execute()
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

    fun listRecipes(): List<JRecipeRecord> {
        return ctx.selectFrom(r)
            .orderBy(r.NAME)
            .fetch()
    }

}

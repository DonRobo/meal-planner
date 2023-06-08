package at.robert.mealplanner.repository

import at.robbert.mealplanner.jooq.Tables
import at.robbert.mealplanner.jooq.tables.records.*
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
    private val ni = Tables.NUTRITION_INFO.`as`("ni")
    private val rni = Tables.RECIPE_NUTRITION_INFO.`as`("rni")
    private val ini = Tables.INGREDIENT_NUTRITION_INFO.`as`("ini")

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

    fun getRecipeNutrition(recipeId: Int): JNutritionInfoRecord? {
        return ctx.select(*ni.fields())
            .from(ni)
            .join(rni).on(ni.ID.eq(rni.NUTRITION_INFO_ID))
            .where(rni.RECIPE_ID.eq(recipeId))
            .fetchOneInto(ni)
    }

    fun getIngredientNutritions(ingredientIds: Collection<Int>): Map<Int, JNutritionInfoRecord> {
        return ctx.select(*ni.fields(), ini.INGREDIENT_ID)
            .from(ni)
            .join(ini).on(ni.ID.eq(ini.NUTRITION_INFO_ID))
            .where(ini.INGREDIENT_ID.`in`(ingredientIds))
            .fetch()
            .associateBy { it[ini.INGREDIENT_ID] }
            .mapValues { it.value.into(ni) }
    }

    fun upsertRecipe(
        id: Int?,
        name: String,
        description: String?,
        imageUrl: String?,
        link: String?,
        prepTime: Int?,
        cookTime: Int?,
        totalTime: Int?
    ): JRecipeRecord {
        val record = ctx.newRecord(r).apply {
            this.name = name
            this.description = description
            this.imageUrl = imageUrl
            this.link = link
            this.prepTime = prepTime
            this.cookTime = cookTime
            this.totalTime = totalTime
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

    fun getRecipeByUrl(url: String): JRecipeRecord {
        TODO("Not yet implemented")
    }

    fun getIngredientByName(ingredientName: String): JIngredientRecord {
        TODO("Not yet implemented")
    }

}

package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.JRecipeIngredient
import at.robert.mealplanner.data.RecipeIngredient
import org.jooq.Record
import org.jooq.RecordMapper

class RecipeIngredientMapper(
    private val ri: JRecipeIngredient,
    private val iMapper: IngredientMapper
) : RecordMapper<Record, RecipeIngredient> {

    val fields = listOf(
        ri.QUANTITY,
        ri.UNIT,
    ) + iMapper.fields

    override fun map(r: Record): RecipeIngredient {
        return RecipeIngredient(
            ingredient = iMapper.map(r),
            quantity = r[ri.QUANTITY],
            unit = r[ri.UNIT],
        )
    }
}


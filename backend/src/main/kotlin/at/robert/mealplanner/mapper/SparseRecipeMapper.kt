package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.JRecipe
import at.robert.mealplanner.data.SparseRecipe
import org.jooq.Record
import org.jooq.RecordMapper

class SparseRecipeMapper(
    private val r: JRecipe,
) : RecordMapper<Record, SparseRecipe> {
    val fields = listOf(
        r.ID,
        r.NAME,
        r.DESCRIPTION,
        r.IMAGE_URL,
    )

    override fun map(record: Record): SparseRecipe {
        return SparseRecipe(
            id = record[r.ID],
            name = record[r.NAME],
            description = record[r.DESCRIPTION],
            imageUrl = record[r.IMAGE_URL],
        )
    }
}

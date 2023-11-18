package at.robert.mealplanner.mapper

import at.robbert.mealplanner.jooq.tables.JPlannedMeal
import at.robert.mealplanner.data.PlannedMeal
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jooq.Record
import org.jooq.RecordMapper


class PlannedMealMapper(
    private val pm: JPlannedMeal,
    private val objectMapper: ObjectMapper,
) : RecordMapper<Record, PlannedMeal> {

    val fields = listOf(
        pm.ID,
        pm.DATA,
        pm.ORDERING,
        pm.DAY,
    )

    override fun map(r: Record): PlannedMeal {
        return PlannedMeal(
            id = r[pm.ID],
            data = objectMapper.readValue(r[pm.DATA].data()),
            ordering = r[pm.ORDERING],
            day = r[pm.DAY],
        )
    }
}

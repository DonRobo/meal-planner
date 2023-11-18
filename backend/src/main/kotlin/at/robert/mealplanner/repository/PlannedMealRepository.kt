package at.robert.mealplanner.repository

import at.robbert.mealplanner.jooq.Tables
import at.robert.mealplanner.data.PlannedMeal
import at.robert.mealplanner.data.PlannedMealData
import at.robert.mealplanner.mapper.PlannedMealMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.DSLContext
import org.jooq.JSONB.jsonb
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class PlannedMealRepository(
    private val ctx: DSLContext,
    private val objectMapper: ObjectMapper,
) {

    private val pm = Tables.PLANNED_MEAL.`as`("pm")
    private val pmMapper = PlannedMealMapper(pm, objectMapper)

    fun addPlannedMeal(
        day: LocalDate,
        plannedMealData: PlannedMealData,
        insertBeforePlannedMealId: Int? = null,
    ) {
        ctx.insertInto(pm)
            .columns(pm.DAY, pm.DATA, pm.ORDERING)
            .values(
                day,
                jsonb(objectMapper.writeValueAsString(plannedMealData)),
                if (insertBeforePlannedMealId != null) {
                    val plannedMealOrdering = ctx.select(pm.ORDERING)
                        .from(pm)
                        .where(pm.ID.eq(insertBeforePlannedMealId))
                        .fetchOne(pm.ORDERING) ?: error("Planned meal with id $insertBeforePlannedMealId not found")
                    ctx.update(pm)
                        .set(pm.ORDERING, pm.ORDERING + 1)
                        .where(pm.ORDERING.greaterOrEqual(plannedMealOrdering))
                        .execute()
                    plannedMealOrdering
                } else {
                    (ctx.select(DSL.max(pm.ORDERING))
                        .from(pm)
                        .where(pm.DAY.eq(day))
                        .fetchOne(pm.ORDERING) ?: 0) + 1
                },
            )
    }

    fun plannedMealsForDay(day: LocalDate): MutableList<PlannedMeal> {
        return ctx.select(pmMapper.fields)
            .from(pm)
            .where(pm.DAY.eq(day))
            .orderBy(pm.ORDERING.asc())
            .fetch(pmMapper)
    }

}

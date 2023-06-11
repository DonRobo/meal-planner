package at.robert.mealplanner

import org.jooq.Condition
import org.jooq.Field
import org.jooq.Select
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private val loggers = ConcurrentHashMap<Class<*>, Logger>()
val Any.log: Logger
    get() = loggers.getOrPut(this::class.java) {
        LoggerFactory.getLogger(this::class.java)
    }

fun String.getUriParameter(param: String): String? = getUriParameters()[param]

fun String.getUriParameters(): Map<String, String> {
    val params = mutableMapOf<String, String>()
    val parts = this.split("?")
    if (parts.size == 2) {
        val paramParts = parts[1].split("&")
        paramParts.forEach {
            val param = it.split("=")
            if (param.size == 2) {
                params[param[0]] = param[1]
            }
        }
    }
    return params
}

inline fun <reified T> Select<*>.fetchInto(): List<T> {
    return this.fetchInto(T::class.java)
}

fun <T> Field<T>.eqOrIsNull(value: T?): Condition {
    return if (value == null) {
        this.isNull
    } else {
        this.eq(value)
    }
}

fun String.parsePortions(): Float {
    return when (this) {
        "1/2", "½" -> 0.5f
        "1/4", "¼" -> 0.25f
        "3/4", "¾" -> 0.75f
        "1/3", "⅓" -> 0.33f
        "2/3", "⅔" -> 0.66f

        else -> this.toFloat()
    }
}

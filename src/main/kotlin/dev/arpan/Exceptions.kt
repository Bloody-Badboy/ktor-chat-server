package dev.arpan

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table

/**
 * Validation of input parameters failed.
 */
class ValidationException(msg: String) : Exception(msg)

/**
 * A resource could not be found.
 */
class NotFoundException(msg: String) : Exception(msg)

/**
 * User doesn't have access to a resource.
 */
class IllegalAccessException(msg: String) : SecurityException(msg)

/**
 * Operation failed for some reason e.g. because the db or some external service wasn't available.
 */
class ServiceUnavailable(msg: String) : Exception(msg)

inline fun <reified T : Enum<T>> Table.customEnumeration(name: String) =
    registerColumn<T>(name, object : ColumnType() {

        override fun sqlType(): String =
            enumValues<T>().joinToString(",", prefix = "ENUM(", postfix = ")") { "'" + it.name + "'" }

        override fun valueFromDB(value: Any) {
            println(value)
            enumValueOf<T>(value as String)
        }

        override fun notNullValueToDB(value: Any) = (value as T).name
    })

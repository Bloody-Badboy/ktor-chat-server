package dev.arpan.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object KeyStores : IntIdTable(name = "chat_key_stores", columnName = "_id") {
    val user = reference("user_id", Users)
    val token = uuid("token")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
}

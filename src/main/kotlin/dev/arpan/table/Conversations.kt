package dev.arpan.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Conversations : IntIdTable(name = "chat_conversations") {
    val owner = reference("user_id", Users)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
}

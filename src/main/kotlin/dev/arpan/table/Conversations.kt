package dev.arpan.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Conversations : IntIdTable(name = "chat_conversations", columnName = "_id") {
    val owner = reference("user_id", Users)
    val lastMessage = reference("last_message_id", Messages).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
}

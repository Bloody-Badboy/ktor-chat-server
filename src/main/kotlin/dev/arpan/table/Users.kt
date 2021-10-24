package dev.arpan.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object Users : IntIdTable(name = "chat_users", columnName = "_id") {
    val fullName = varchar("full_name", 256)
    val email = varchar("email", 256).uniqueIndex()
    val password = varchar("password", 256)
    val isOnline = bool("is_online").default(false)
    val isAdmin = bool("is_admin").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
}

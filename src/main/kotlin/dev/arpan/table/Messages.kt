package dev.arpan.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

enum class MessageType {
    TEXT,
    MEDIA,
    LOCATION;
}

object Messages : IntIdTable(name = "chat_messages", columnName = "_id") {
    val conversation = reference("conversation_id", Conversations)
    val sender = reference("sender_id", Users)
    val message = text("message").nullable()

    val messageType = customEnumeration(
        name = "message_type",
        sql = "ENUM ('TEXT', 'MEDIA', 'LOCATION')",
        fromDb = {
            enumValueOf<MessageType>(it as String)
        },
        toDb = { type ->
            type.name
        })

    // val messageType = customEnumeration<MessageType>("message_type")
    val mediaUrl = varchar("media_url", 256).nullable()
    val mediaName = varchar("media_name", 256).nullable()
    val mediaMimeType = varchar("media_mime_type", 256).nullable()
    val latitude = double("latitude").nullable()
    val longitude = double("longitude").nullable()
    val timestamp = datetime("timestamp").defaultExpression(CurrentDateTime())
}

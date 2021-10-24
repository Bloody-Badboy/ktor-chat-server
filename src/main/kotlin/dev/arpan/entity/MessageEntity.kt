package dev.arpan.entity

import dev.arpan.model.ws.response.SocketResponse
import dev.arpan.table.MessageType
import dev.arpan.table.Messages
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.ZoneOffset

class MessageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MessageEntity>(Messages)

    var conversation by ConversationEntity referencedOn Messages.conversation
    var sender by UserEntity referencedOn Messages.sender
    var message by Messages.message
    var messageType by Messages.messageType
    var mediaUrl by Messages.mediaUrl
    var mediaName by Messages.mediaName
    var mediaMimeType by Messages.mediaMimeType
    var latitude by Messages.latitude
    var longitude by Messages.longitude
    var timestamp by Messages.timestamp

    fun toMessageResponse(): SocketResponse.Message {
        return when (messageType) {
            MessageType.TEXT -> {
                SocketResponse.Message.Text(
                    messageId = id.value,
                    message = message.orEmpty(),
                    senderId = sender.id.value,
                    timestamp = timestamp.toEpochSecond(ZoneOffset.UTC)
                )
            }
            MessageType.MEDIA -> {
                SocketResponse.Message.Media(
                    messageId = id.value,
                    senderId = sender.id.value,
                    mediaName = mediaName.orEmpty(),
                    mediaUrl = mediaUrl.orEmpty(),
                    mediaType = mediaMimeType.orEmpty(),
                    timestamp = timestamp.toEpochSecond(ZoneOffset.UTC)
                )
            }
            MessageType.LOCATION -> {
                SocketResponse.Message.Location(
                    messageId = id.value,
                    senderId = sender.id.value,
                    latitude = latitude ?: 0.0,
                    longitude = longitude ?: 0.0,
                    timestamp = timestamp.toEpochSecond(ZoneOffset.UTC)
                )
            }
        }
    }
}

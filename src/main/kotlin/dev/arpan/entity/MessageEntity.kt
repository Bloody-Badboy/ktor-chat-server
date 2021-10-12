package dev.arpan.entity

import dev.arpan.table.Messages
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

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
}

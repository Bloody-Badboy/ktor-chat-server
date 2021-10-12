package dev.arpan.entity

import dev.arpan.table.Conversations
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ConversationEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ConversationEntity>(Conversations)

    var owner by UserEntity referencedOn Conversations.owner
    var createdAt by Conversations.createdAt
}

package dev.arpan.entity

import dev.arpan.table.KeyStores
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class KeyStoreEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<KeyStoreEntity>(KeyStores)

    var user by UserEntity referencedOn KeyStores.user
    var token by KeyStores.token
    var createdAt by KeyStores.createdAt
}

package dev.arpan.entity

import dev.arpan.table.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(uuid: EntityID<Int>) : IntEntity(uuid) {
    companion object : IntEntityClass<UserEntity>(Users)

    var fullName by Users.fullName
    var email by Users.email
    var password by Users.password
    var isOnline by Users.isOnline
    var isAdmin by Users.isAdmin
    var createdAt by Users.createdAt
}

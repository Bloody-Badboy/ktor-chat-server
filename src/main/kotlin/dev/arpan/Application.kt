package dev.arpan

import dev.arpan.entity.KeyStoreEntity
import dev.arpan.plugins.configureRouting
import dev.arpan.plugins.configureSockets
import dev.arpan.table.Conversations
import dev.arpan.table.KeyStores
import dev.arpan.table.Messages
import dev.arpan.table.Users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 9000, host = "0.0.0.0") {
        initDatabase()
        install(ContentNegotiation) {
            register(ContentType.Application.Json, MoshiContentConverter(Inject.provideMoshi()))
        }
        install(StatusPages) {
            exception<NotFoundException> { cause ->
                call.respond(HttpStatusCode.NotFound, cause.message.orEmpty())
            }
            exception<Throwable> { cause ->
                call.respond(HttpStatusCode.InternalServerError, cause.stackTraceToString())
            }
        }
        configureRouting()
        configureSockets()
    }.start(wait = true)
}

private fun Application.initDatabase() {
    Database.connect("jdbc:mysql://localhost:3306/chat-db", user = "root", password = "admin")
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, Conversations, Messages, KeyStores)
    }
    // transaction {
    //     SchemaUtils.drop(Users, Conversations, Messages, KeyStores)
    //     SchemaUtils.create(Users, Conversations, Messages, KeyStores)
    //
    //     val userEntity1 = UserEntity.new {
    //         fullName = "Admin One"
    //         email = "admin1@admin.com"
    //         password = "s3cret"
    //     }
    //
    //     val userEntity2 = UserEntity.new {
    //         fullName = "Admin Two"
    //         email = "admin2@admin.com"
    //         password = "s3cret"
    //     }
    //
    //     ConversationEntity.new {
    //         owner = userEntity1
    //     }
    //
    //     ConversationEntity.new {
    //         owner = userEntity2
    //     }
    //
    //     ConversationEntity.new {
    //         owner = userEntity2
    //     }
    //
    //     val keyStoreEntity1 = KeyStoreEntity.new {
    //         user = userEntity1
    //         token = UUID.randomUUID()
    //     }
    //
    //     val keyStoreEntity2 = KeyStoreEntity.new {
    //         user = userEntity2
    //         token = UUID.randomUUID()
    //     }
    //
    //     println("[*] TOKEN: ${keyStoreEntity1.token} ${keyStoreEntity1.user.fullName}")
    //
    //     println("[*] TOKEN: ${keyStoreEntity2.token} ${keyStoreEntity2.user.fullName}")
    // }

    transaction {
        println(KeyStoreEntity.all().toList().map {
            "Token: ${it.token} User: ${it.user.id} ${it.user.fullName}"
        }.joinToString("\n"))
    }
}

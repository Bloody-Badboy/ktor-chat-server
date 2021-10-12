package dev.arpan.plugins

import dev.arpan.NotFoundException
import dev.arpan.entity.KeyStoreEntity
import dev.arpan.entity.UserEntity
import dev.arpan.model.LoginResponse
import dev.arpan.model.request.LoginRequest
import dev.arpan.table.Users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val userEntity = transaction {
                UserEntity.find { Users.email eq request.email }.firstOrNull()
            } ?: throw NotFoundException("User with ${request.email} not found.")

            val response = transaction {
                val entity = KeyStoreEntity.new {
                    user = userEntity
                    token = UUID.randomUUID()
                }
                LoginResponse(token = entity.token.toString())
            }
            call.respond(response)
        }
    }
}

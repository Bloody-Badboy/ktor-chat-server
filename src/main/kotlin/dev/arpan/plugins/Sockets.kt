package dev.arpan.plugins

import dev.arpan.entity.ConversationEntity
import dev.arpan.entity.KeyStoreEntity
import dev.arpan.entity.MessageEntity
import dev.arpan.model.ws.command.SocketCommand
import dev.arpan.model.ws.response.Sender
import dev.arpan.model.ws.response.SocketResponse
import dev.arpan.table.Conversations
import dev.arpan.table.KeyStores
import dev.arpan.table.MessageType
import dev.arpan.table.Messages
import dev.arpan.utils.Util
import dev.arpan.utils.toJson
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.timeout
import io.ktor.routing.routing
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.time.ZoneOffset
import java.util.UUID

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/loopback") {
            try {
                println("Client connected!")
                for (frame in incoming) {
                    val text = (frame as? Frame.Text)?.readText() ?: continue
                    send(Frame.Text(text))
                }
            } finally {
                println("Client disconnected!")
            }
        }
        webSocket("/") {
            val userToken = call.request.queryParameters["token"]
            if (userToken != null) {
                val keyStoreEntity = transaction {
                    KeyStoreEntity.find { KeyStores.token eq UUID.fromString(userToken) }.firstOrNull()
                } ?: run {
                    closeUnauthorized()
                    return@webSocket
                }
                val userEntity = transaction {
                    keyStoreEntity.user
                }
                val connection = ConnectionManager.Connection(session = this, userId = userEntity.id.value)
                ConnectionManager.connections += connection
                println("Adding ${userEntity.fullName}!")

                send(Frame.Text("Hello ${userEntity.fullName}!, Welcome to chat server."))

                transaction {
                    userEntity.isOnline = true
                }
                try {
                    for (frame in incoming) {
                        when (val request = Util.parserFrame(frame)) {
                            is SocketCommand.AllConversations -> {
                                val conversations = transaction {
                                    ConversationEntity.find {
                                        Conversations.owner eq userEntity.id
                                    }.map {
                                        SocketResponse.Conversation(
                                            id = it.id.value,
                                            createdAt = it.createdAt.toEpochSecond(ZoneOffset.UTC),
                                            lastMessage = it.lastMessage?.toMessageResponse(),
                                            lastMessageSender = it.lastMessage?.sender?.let { sender ->
                                                Sender(
                                                    senderId = sender.id.value,
                                                    senderName = sender.fullName
                                                )
                                            }
                                        )
                                    }
                                }

                                sendResponse(
                                    response = SocketResponse.AllConversations(
                                        conversations = conversations
                                    )
                                )
                            }
                            is SocketCommand.AllMessages -> {
                                val messages = transaction {
                                    MessageEntity.find {
                                        Messages.conversation eq request.conversationId
                                    }.map { entity ->
                                        entity.toMessageResponse()
                                    }
                                }

                                sendResponse(
                                    response = SocketResponse.AllMessages(
                                        messages = messages
                                    )
                                )
                            }
                            is SocketCommand.NewConversation -> {
                                transaction {
                                    ConversationEntity.new {
                                        owner = userEntity
                                    }.run {
                                        SocketResponse.Conversation(
                                            id = id.value,
                                            createdAt = createdAt.toEpochSecond(ZoneOffset.UTC),
                                            lastMessage = null,
                                            lastMessageSender = null
                                        )
                                    }
                                }
                            }
                            is SocketCommand.SubscribeConversation -> {
                                // TODO add validation before subscribing
                                connection.subConversationId = request.conversationId
                                sendResponse(
                                    response = SocketResponse.SubscribeAck(
                                        conversationId = request.conversationId,
                                        success = true
                                    )
                                )
                            }
                            is SocketCommand.UnsubscribeConversation -> {
                                connection.subConversationId = null
                                sendResponse(
                                    response = SocketResponse.UnsubscribeAck(
                                        conversationId = request.conversationId,
                                        success = true
                                    )
                                )
                            }
                            is SocketCommand.ConversationMessage -> {
                                val conversationEntity = transaction {
                                    ConversationEntity.findById(id = request.id)
                                }
                                if (conversationEntity != null) {
                                    val message = transaction {
                                        when (val msg = request.message) {

                                            is SocketCommand.Message.Media -> {

                                                MessageEntity.new {
                                                    conversation = conversationEntity
                                                    sender = userEntity
                                                    messageType = MessageType.MEDIA
                                                    mediaName = msg.mediaName
                                                    mediaUrl = msg.mediaUrl
                                                    mediaMimeType = msg.mediaType
                                                }.run {
                                                    conversationEntity.lastMessage = this
                                                    SocketResponse.Message.Media(
                                                        messageId = id.value,
                                                        senderId = sender.id.value,
                                                        mediaName = mediaName.orEmpty(),
                                                        mediaUrl = mediaUrl.orEmpty(),
                                                        mediaType = mediaMimeType.orEmpty(),
                                                        timestamp = timestamp.toEpochSecond(ZoneOffset.UTC)
                                                    )
                                                }
                                            }
                                            is SocketCommand.Message.Location -> {

                                                MessageEntity.new {
                                                    conversation = conversationEntity
                                                    sender = userEntity
                                                    messageType = MessageType.LOCATION
                                                    latitude = msg.latitude
                                                    longitude = msg.longitude
                                                }.run {
                                                    conversationEntity.lastMessage = this
                                                    SocketResponse.Message.Location(
                                                        messageId = id.value,
                                                        senderId = sender.id.value,
                                                        latitude = latitude ?: 0.0,
                                                        longitude = longitude ?: 0.0,
                                                        timestamp = timestamp.toEpochSecond(ZoneOffset.UTC)
                                                    )
                                                }
                                            }
                                            is SocketCommand.Message.Text -> {

                                                MessageEntity.new {
                                                    conversation = conversationEntity
                                                    sender = userEntity
                                                    message = msg.message
                                                    messageType = MessageType.TEXT
                                                }.run {
                                                    conversationEntity.lastMessage = this
                                                    SocketResponse.Message.Text(
                                                        messageId = id.value,
                                                        message = message.orEmpty(),
                                                        senderId = sender.id.value,
                                                        timestamp = timestamp.toEpochSecond(ZoneOffset.UTC)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    ConnectionManager.publishMessageToSubscribedUsers(conversationEntity, message)
                                }
                            }
                        }
                    }
                } finally {
                    println("Removing ${userEntity.fullName}!")
                    ConnectionManager.connections -= connection
                    transaction {
                        userEntity.isOnline = false
                    }
                }
            } else {
                closeUnauthorized()
            }
        }
    }
}

private suspend inline fun DefaultWebSocketServerSession.closeUnauthorized() {
    close(
        CloseReason(
            CloseReason.Codes.CANNOT_ACCEPT,
            "Unauthorized!"
        )
    )
}

suspend inline fun DefaultWebSocketServerSession.sendResponse(response: SocketResponse) {
    toJson(response)?.let { send(Frame.Text(it)) }
}

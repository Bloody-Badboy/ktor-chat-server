package dev.arpan.plugins

import dev.arpan.entity.ConversationEntity
import dev.arpan.model.ws.response.SocketResponse
import io.ktor.websocket.DefaultWebSocketServerSession
import java.util.Collections

object ConnectionManager {
    val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    suspend fun publishMessageToSubscribedUsers(entity: ConversationEntity, message: SocketResponse.Message) {
        connections.filter { con -> con.subConversationId == entity.id.value }
            .map { it.session }
            .forEach { session ->
                session.sendResponse(SocketResponse.ConversationMessage(message))
            }
    }

    class Connection(val session: DefaultWebSocketServerSession, val userId: Int) {
        var subConversationId: Int? = null
    }
}

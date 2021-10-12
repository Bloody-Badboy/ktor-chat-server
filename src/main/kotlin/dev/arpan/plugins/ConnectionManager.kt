package dev.arpan.plugins

import io.ktor.websocket.DefaultWebSocketServerSession
import java.util.Collections

object ConnectionManager {
    val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    class Connection(val session: DefaultWebSocketServerSession) {
        var subscribedConversationId: Int? = null
    }
}

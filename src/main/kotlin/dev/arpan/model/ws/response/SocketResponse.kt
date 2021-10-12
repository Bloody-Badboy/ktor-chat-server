package dev.arpan.model.ws.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class SocketResponse {

    sealed class Message {
        @JsonClass(generateAdapter = true)
        data class Text(
            @Json(name = "m_id") val messageId: Int,
            @Json(name = "msg") val message: String,
            @Json(name = "s_id") val senderId: Int,
            @Json(name = "ts") val timestamp: Long
        ) : Message()

        @JsonClass(generateAdapter = true)
        data class Media(
            @Json(name = "m_id") val messageId: Int,
            @Json(name = "s_id") val senderId: Int,
            @Json(name = "m_name") val mediaName: String,
            @Json(name = "m_url") val mediaUrl: String,
            @Json(name = "m_type") val mediaType: String,
            @Json(name = "ts") val timestamp: Long
        ) : Message()

        @JsonClass(generateAdapter = true)
        data class Location(
            @Json(name = "m_id") val messageId: Int,
            @Json(name = "s_id") val senderId: Int,
            @Json(name = "lat") val latitude: Double,
            @Json(name = "lng") val longitude: Double,
            @Json(name = "ts") val timestamp: Long
        ) : Message()
    }

    @JsonClass(generateAdapter = true)
    data class Conversation(
        @Json(name = "c_id") val id: Int,
        @Json(name = "ts") val createdAt: Long
    )

    @JsonClass(generateAdapter = true)
    data class AllConversations(
        @Json(name = "convs")
        val conversations: List<Conversation>
    ) : SocketResponse()

    @JsonClass(generateAdapter = true)
    data class AllMessages(
        @Json(name = "msgs")
        val messages: List<Message>
    ) : SocketResponse()

    @JsonClass(generateAdapter = true)
    data class ConversationMessage(
        @Json(name = "msg") val message: Message
    ) : SocketResponse()

    @JsonClass(generateAdapter = true)
    data class SubscribeAck(
        @Json(name = "c_id") val conversationId: Int,
        @Json(name = "success") val success: Boolean
    ) : SocketResponse()

    @JsonClass(generateAdapter = true)
    data class UnsubscribeAck(
        @Json(name = "c_id") val conversationId: Int,
        @Json(name = "success") val success: Boolean
    ) : SocketResponse()
}

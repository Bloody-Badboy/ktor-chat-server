package dev.arpan.model.ws.command

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Suppress("CanSealedSubClassBeObject")
sealed class SocketCommand {

    sealed class Message {
        @JsonClass(generateAdapter = true)
        data class Text(
            @Json(name = "msg") val message: String
        ) : Message()

        @JsonClass(generateAdapter = true)
        data class Media(
            @Json(name = "m_name") val mediaName: String,
            @Json(name = "m_url") val mediaUrl: String,
            @Json(name = "m_type") val mediaType: String,
        ) : Message()

        @JsonClass(generateAdapter = true)
        data class Location(
            @Json(name = "lat") val latitude: Double,
            @Json(name = "lng") val longitude: Double
        ) : Message()
    }

    @JsonClass(generateAdapter = true)
    class AllConversations : SocketCommand()

    @JsonClass(generateAdapter = true)
    class NewConversation : SocketCommand()

    @JsonClass(generateAdapter = true)
    data class AllMessages(
        @Json(name = "c_id") val conversationId: Int
    ) : SocketCommand()

    @JsonClass(generateAdapter = true)
    data class ConversationMessage(
        @Json(name = "c_id") val id: Int,
        @Json(name = "msg") val message: Message
    ) : SocketCommand()

    @JsonClass(generateAdapter = true)
    data class SubscribeConversation(
        @Json(name = "c_id") val conversationId: Int
    ) : SocketCommand()

    @JsonClass(generateAdapter = true)
    data class UnsubscribeConversation(
        @Json(name = "c_id") val conversationId: Int
    ) : SocketCommand()
}

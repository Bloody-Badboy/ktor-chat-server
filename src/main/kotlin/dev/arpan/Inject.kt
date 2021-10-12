package dev.arpan

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import dev.arpan.model.ws.request.SocketCommand
import dev.arpan.model.ws.response.SocketResponse

object Inject {
    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(SocketCommand::class.java, "op")
                .withSubtype(SocketCommand.AllConversations::class.java, "conv_all")
                .withSubtype(SocketCommand.AllMessages::class.java, "msg_all")
                .withSubtype(SocketCommand.ConversationMessage::class.java, "msg")
                .withSubtype(SocketCommand.SubscribeConversation::class.java, "conv_sub")
                .withSubtype(SocketCommand.UnsubscribeConversation::class.java, "conv_unsub")
        )
        .add(
            PolymorphicJsonAdapterFactory.of(SocketCommand.Message::class.java, Constants.MESSAGE_TYPE_KEY)
                .withSubtype(SocketCommand.Message.Text::class.java, Constants.MESSAGE_TYPE_TEXT)
                .withSubtype(SocketCommand.Message.Media::class.java, Constants.MESSAGE_TYPE_MEDIA)
                .withSubtype(SocketCommand.Message.Location::class.java, Constants.MESSAGE_TYPE_LOCATION)
        )
        .add(
            PolymorphicJsonAdapterFactory.of(SocketResponse::class.java, "op")
                .withSubtype(SocketResponse.AllConversations::class.java, "conv_all")
                .withSubtype(SocketResponse.AllMessages::class.java, "msg_all")
                .withSubtype(SocketResponse.ConversationMessage::class.java, "msg")
                .withSubtype(SocketResponse.SubscribeAck::class.java, "subscribe_ack")
                .withSubtype(SocketResponse.UnsubscribeAck::class.java, "unsubscribe_ack")
        )
        .add(
            PolymorphicJsonAdapterFactory.of(SocketResponse.Message::class.java, Constants.MESSAGE_TYPE_KEY)
                .withSubtype(SocketResponse.Message.Text::class.java, Constants.MESSAGE_TYPE_TEXT)
                .withSubtype(SocketResponse.Message.Media::class.java, Constants.MESSAGE_TYPE_MEDIA)
                .withSubtype(SocketResponse.Message.Location::class.java, Constants.MESSAGE_TYPE_LOCATION)
        )
        .build()

    fun provideMoshi() = moshi
}

package dev.arpan.utils

import dev.arpan.Inject
import dev.arpan.model.ws.command.SocketCommand
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText

object Util {
    fun parserFrame(frame: Frame): SocketCommand? {
        val text = (frame as? Frame.Text)?.readText() ?: return null
        return fromJson(text)
    }
}

inline fun <reified T> fromJson(text: String): T? {
    try {
        return Inject.provideMoshi().adapter(T::class.java).fromJson(text)
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return null
}

inline fun <reified T> toJson(obj: T): String? {
    try {
        return Inject.provideMoshi().adapter(T::class.java).toJson(obj)
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return null
}

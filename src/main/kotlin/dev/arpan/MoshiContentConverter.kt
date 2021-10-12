package dev.arpan

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.ContentConverter
import io.ktor.features.ContentNegotiation
import io.ktor.features.suitableCharset
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.request.ApplicationReceiveRequest
import io.ktor.util.pipeline.PipelineContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import kotlin.reflect.jvm.jvmErasure

class MoshiContentConverter(private val moshi: Moshi) : ContentConverter {
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        val result = runCatching {
            val request = context.subject
            val channel = request.value as? ByteReadChannel ?: return null
            val type = request.typeInfo
            val javaType = type.jvmErasure

            withContext(Dispatchers.IO) {
                val source = channel.toInputStream().source().buffer()
                moshi.adapter(javaType.javaObjectType).fromJson(source)
            }
        }
        if (result.isSuccess) {
            return result.getOrThrow()
        } else {
            when (val ex = result.exceptionOrNull()) {
                is JsonDataException -> throw IllegalArgumentException("Incorrect json format: ${ex.message}")
                is JsonEncodingException -> throw IllegalArgumentException("Invalid json format: ${ex.message}")
                else -> throw IllegalArgumentException(ex?.message ?: "")
            }
        }
    }

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any {
        return TextContent(
            moshi.adapter(value.javaClass).toJson(value),
            contentType.withCharset(context.call.suitableCharset())
        )
    }
}

fun ContentNegotiation.Configuration.moshi(block: Moshi.Builder.() -> Unit) {
    val builder = Moshi.Builder().apply(block).build()
    val converter = MoshiContentConverter(builder)
    register(ContentType.Application.Json, converter)
}

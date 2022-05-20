import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.utils.io.charsets.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.cbor.Cbor

/**
 * File request;
 * Approved status:
 *
 *   0  -> waiting
 *
 *   1  -> approved
 *
 *   2  -> uploaded
 *
 *   -1 -> rejected
 *
 *   -2 -> not found
 *
 *
 * @property [recipient][String]
 * @property [filename][String]
 * @property [sender][String]
 * @property [status][Int] = 0
 */
@kotlinx.serialization.Serializable
//putting and Serializable ENUM as the status leads to weird internal IDE errors, this is also fine
data class FileRequest(val recipient: String, val filename: String, val sender: String, var status: Int = 0)

fun FileRequest.Url() = "${sender.replace(" ", "_")}/${filename.encodeURLPath()}"

suspend inline fun <reified T : Any> DefaultWebSocketSession.sendSerial(data: T) {
    this.sendSerializedBase(data, KotlinxWebsocketSerializationConverter(Cbor), Charsets.UTF_8)
}

inline fun <T> optional(expression: () -> T): T? {
    return try {
        expression()
    } catch (ex: Throwable) {
        null
    }
}

fun log(message: Any) {
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).run {
        println("${"$dayOfMonth".padStart(2, '0')}/${month} ${"$hour".padStart(2, '0')}:${"$minute".padStart(2, '0')}:${"$second".padStart(2, '0')}: $message")
        return@run 5
    }
}
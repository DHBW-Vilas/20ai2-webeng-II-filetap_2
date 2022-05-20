@file:OptIn(ExperimentalSerializationApi::class)

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import java.util.*

fun Application.configureSockets() {
    install(WebSockets){
        this.pingPeriodMillis = 15000
        this.timeoutMillis = 15000
        this.maxFrameSize = Long.MAX_VALUE
        this.masking = false
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        CoroutineScope(Dispatchers.IO).launch {
            webSocket("/comm") {
                val thisConnection = Connection(this)
                connections += thisConnection
                send("name:${thisConnection.name}") // sending generated username to client;


                for (frame in (this as WebSocketSession).incoming) {
                    when (frame) {
                        is Frame.Text   -> {
                            if (frame.readText().lowercase() == "ping") {
                                log("PING")
                                send("PONG")
                            }
                        }
                        is Frame.Binary -> {
                            frame.readBytes().also {
                                optional { // ignore any errors, because we don't know what binary data we get
                                    Cbor.decodeFromByteArray<FileRequest>(it).also { fr ->
                                        when (fr.status) {
                                            0  -> {
                                                log("New file request: $fr")
                                                connections.find { it.name == fr.recipient }?.let {
                                                    thisConnection.session.sendSerial(fr)
                                                    log("Sent upload link")
                                                } ?: run {
                                                    log("No recipient found for $fr")
                                                    fr.status = -2
                                                    thisConnection.session.sendSerial(fr)
                                                }
                                            }
                                            -1 -> {
                                                log("File request rejected: $fr")
                                                connections.find { it.name == fr.sender }?.session?.sendSerial(fr)
                                            }
                                            1  -> {
                                                log("File request approved: $fr")
                                                connections.find { it.name == fr.sender }?.session?.sendSerial(fr)
                                            }
                                            2  -> {
                                                log("File for $fr uploaded")
                                                connections.find { it.name == fr.recipient }?.let {
                                                    it.session.sendSerial(fr)
                                                    log("Relayed file request")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else            -> {}
                    }
                }
            }
        }
    }
}

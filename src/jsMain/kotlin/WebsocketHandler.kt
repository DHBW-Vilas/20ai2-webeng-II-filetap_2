@file:OptIn(ExperimentalTime::class)

import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.currentTimeMillis
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get
import org.w3c.dom.url.URL
import org.w3c.fetch.RequestInit
import org.w3c.files.FileReader
import kotlin.js.Promise.Companion.resolve
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


object WebsocketHandler {
    @Suppress("NestedLambdaShadowedImplicitParameter")
    suspend fun DefaultClientWebSocketSession.handler() {
        ws = this
        launch {
            try {
                for (frame in incoming) {
                    log("Received: $frame")
                    when (frame) {
                        is Frame.Text   -> {
                            with(frame.readText()) {
                                handleTextFrame()
                            }
                        }
                        is Frame.Binary -> {
                            frame.readBytes().run {
                                handleBinaryFrame()
                            }
                        }
                        is Frame.Close  -> {
                            Toast("Server closed connection, please reload!")
                            ws = null
                            resetForm()
                        }
                        else            -> {
                            /* no-op */
                        }
                    }
                }
            } catch (e: Exception) {
                log("Error while receiving: " + e.message)
            }
        }.join()
    }

    private fun String.handleTextFrame() {
        when {
            startsWith("name:")    -> {
                username = this.removePrefix("name:")
                byId<HTMLInputElement>("fileinput").disabled = false
                byId<HTMLDivElement>("status").run {
                    querySelector("span")?.textContent = this@handleTextFrame.removePrefix("name:")
                    childNodes[0]?.nodeValue = "You are: "
                }
            }
            startsWith("request:") -> {
                val sender = this.removePrefix("file:").takeWhile { it != ',' }
                val filename = this.takeLastWhile { it != ',' }
                DialogBox("$sender want to send you $filename", "Do you approve?") {
                    Toast(it.name)
                }
            }
            startsWith("system:")  -> {
                Toast(this.removePrefix("system:"))
            }
            else                   -> {
                // ignore everything we don't know
            }
        }
    }

    private fun ByteArray.handleBinaryFrame() {
        optional { // same reason as in server WebSocket.kt
            Cbor.decodeFromByteArray<FileRequest>(this).also { fr ->
                when (fr.status) {
                    -2 -> {
                        Toast("${fr.recipient} was not found")
                        resetForm()
                    }
                    -1 -> {
                        Toast("${fr.recipient} denied your request")
                    }
                    0  -> {
                        Toast("Uploading file...")
                        log("uploading to ${fr.Url()}")
                        FileReader().run {
                            readAsArrayBuffer(file!!)
                            onloadend = {
                                measureTime {
                                    log("before upload")
                                    resolve(
                                        window.fetch(
                                            fr.Url(),
                                            RequestInit(method = "PUT", body = result)
                                        )
                                    ).then {
                                        log("uploaded, resetting form")
                                        mainScope.launch {
                                            // 2500ms is the half of the full driving animation, we want to wait to the next multple of 2500 until we reset everything
                                            delay(2500L - (currentTimeMillis() - animationStartTime).mod(2500L))
                                            Toast("Uploaded")
                                            fr.status = 2
                                            ws!!.sendSerial(fr)
                                            resetForm()
                                        }
                                    }
                                    resolve("done uploading")
                                }.also { log("upload time: $it") }
                            }
                        }

                    }
                    1  -> {
                        Toast("${fr.recipient} approved your request")
                    }
                    2  -> {
                        DialogBox("${fr.sender} wants to send you ${fr.filename}", "Do you approve?") {
                            when (it) {
                                DialogBoxCloseType.OK     -> {
                                    mainScope.launch {
                                        (document.createElement("a") as HTMLAnchorElement).also {
                                            it.href = fr.Url()
                                            it.download = fr.filename
                                            document.body!!.append(it)
                                            Toast("Downloading ${fr.filename}...")
                                            it.click()
                                            delay(10000)
                                            document.body!!.removeChild(it)
                                            URL.revokeObjectURL(it.href)
                                        }
                                    }
                                }
                                DialogBoxCloseType.CANCEL -> {
                                    mainScope.launch {
                                        fr.status = -1
                                        ws!!.sendSerial(fr)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

    }
}
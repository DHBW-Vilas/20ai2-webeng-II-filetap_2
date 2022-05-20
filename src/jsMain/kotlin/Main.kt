@file:OptIn(ExperimentalSerializationApi::class)

import WebsocketHandler.handler
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import org.w3c.dom.*
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get

val mainScope = MainScope()

val client = HttpClient {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Cbor)
    }
}
var file: File? = null
var ws: DefaultWebSocketSession? = null
var username: String? = null
var animationStartTime = 0L

fun main() {

    window.onload = {
        mainScope.launch {
            if (window.location.hostname == "localhost") { // run without SSL Layer for dev
                client.ws(
                    port = window.location.port.toIntOrNull(),
                    path = "/comm"
                ) {
                    handler()
                }
            } else {
                client.wss(
                    path = "/comm"
                ) {
                    handler()
                }
            }
        }
        null
    }

    byQuery<HTMLDialogElement>("dialog").append {
        h2 {
            +"Alice wants to send you 'HelloWorld.pdf'"
        }
        h4 { +"Do you approve?" }
        div {
            button {
                id = "approve"
                +"Approve"
            }
            button {
                id = "deny"
                +"Deny"
            }
        }
    }
    byId<HTMLDivElement>("root").append {
        div {
            id = "content"
            htmlObject {
                data = "logo.svg"
                +"FileTap"
            }
            form {
                autoComplete = false
                onSubmitFunction = {
                    FileReader().run {
                        readAsBinaryString(file!!)
                        onloadend = {
                            mainScope.launch {
                                ws!!.sendSerial(FileRequest(byId<HTMLInputElement>("recinput").value, file!!.name, username!!))
                            }
                        }
                        it.preventDefault()
                    }
                }
                div {
                    id = "tag"
                    input {
                        required = true
                        id = "recinput"
                        autoComplete = false
                        type = InputType.text
                        placeholder = "Recipient"
                        if (isSmallTouchDevice()) {
                            onFocusFunction = {
                                byId<HTMLDivElement>("tape-button-wrapper").style.display = "none"
                            }
                            onBlurFunction = {
                                byId<HTMLDivElement>("tape-button-wrapper").style.display = ""
                            }
                        }
                    }
                    input {
                        required = true
                        disabled = true
                        id = "fileinput"
                        type = InputType.file
                        accept = "*"
                        onInputFunction = {
                            file = byId<HTMLInputElement>("fileinput").files?.get(0)
                            if (file == null) {
                                byId<HTMLImageElement>("box-icon").run {
                                    src = "box-open.svg"
                                    removeAttribute("full")
                                }
                            } else {
                                byId<HTMLImageElement>("box-icon").run {
                                    src = "box-closed.svg"
                                    setAttribute("full", "")
                                }
                            }
                        }
                    }
                    label {
                        img {
                            id = "box-icon"
                            src = "box-open.svg"
                            +"file box"
                        }
                        htmlFor = "fileinput"
                    }
                    div {
                        id = "status"
                        +"Waiting on backend connection..."
                        span {
                            +"" // name will go here, once received
                        }
                        span {
                            classes = setOf("material-icons")
                            +"content_copy"
                            onClickFunction = {
                                window.navigator.clipboard.writeText(username ?: "wait a second...")
                                Toast("Copied to clipboard")
                            }
                        }
                    }
                }
                div {
                    id = "tape-button-wrapper"
                    button {
                        id = "send-button"
                        +"Send"
                        onClickFunction = {
                            if (file == null || byId<HTMLInputElement>("recinput").value.isBlank()) {
                                /* no-op */
                            } else {
                                byId<HTMLInputElement>("recinput").disabled = true
                                byId<HTMLInputElement>("fileinput").disabled = true
                                byId<HTMLImageElement>("box-icon").src = "truck.svg"
                                byId<HTMLImageElement>("box-icon").setAttribute("waiting", "")
                                animationStartTime = currentTimeMillis()
                            }
                        }
                    }

                    div {
                        classes = setOf("tape-expander")
                    }
                    div {
                        classes = setOf("tape-ends-wrapper")
                        repeat(3) {
                            div {}
                        }
                    }
                }
            }
        }
    }
}

fun resetForm() {
    file = null
    byQuery<HTMLFormElement>("form").reset()
    byId<HTMLInputElement>("recinput").disabled = false
    byId<HTMLInputElement>("fileinput").disabled = false
    byId<HTMLImageElement>("box-icon").src = "box-open.svg"
    byId<HTMLImageElement>("box-icon").removeAttribute("waiting")
}

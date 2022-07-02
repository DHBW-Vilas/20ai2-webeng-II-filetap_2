import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZonedDateTime

fun main(args: Array<String>) {
  embeddedServer(CIO, port = args.firstOrNull()?.toIntOrNull() ?: 8000, host = "0.0.0.0", watchPaths = listOf("classes", "objects")) {
    routing {
      get("/") {
        call.respondText(
          this::class.java.classLoader.getResource("index.html")!!.readText(),
          ContentType.Text.Html
        )
      }
      put("/{sender}/{filename}") {
        withContext(Dispatchers.IO) {
          try {
            File("objects/${call.parameters["sender"]}_${call.parameters["filename"]}").run {
              if (!exists()) {
                parentFile.mkdirs()
                createNewFile()
              }
              writeBytes(call.receive<ByteArray>().also {
                log("Written ${it.size} bytes to ${this.absolutePath}")
              })
              call.respond(HttpStatusCode.Created)
            }
          } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
          }
        }
      }
      get("/{sender}/{filename}") {
        log("GET ${call.parameters["sender"]}_${call.parameters["filename"]}")
        withContext(Dispatchers.IO) {
          try {
            File("objects/${call.parameters["sender"]}_${call.parameters["filename"]}").run {
              if (exists()) {
                call.respondFile(this)
              } else {
                call.respond(HttpStatusCode.NotFound)
              }
            }
          } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
          }
        }

      }
      static("/") {
        resources("")
      }
    }
    install(CachingHeaders) {
      options { call, outgoingContent ->
        CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 604800), ZonedDateTime.now().plusDays(7))
      }
    }
    install(ContentNegotiation)
    install(Compression) {
      gzip {
        priority = 1.0
      }
      deflate {
        priority = 10.0
        minimumSize(1024) // condition
      }
    }
    configureSockets()
  }.start(wait = true)

}
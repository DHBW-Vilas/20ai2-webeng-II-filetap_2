import io.github.serpro69.kfaker.faker
import io.ktor.websocket.*

val faker = faker {}

data class Connection(
  val session: DefaultWebSocketSession,
  val name: String = faker.run {
    "${color.name()} ${animal.unique.name()}".trim()
  },
) {
  init {
    log("New connection: $name")
  }
}
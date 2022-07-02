import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*

inline fun <reified T : HTMLElement> byId(s: String): T {
  return document.getElementById(s)?.let { it as? T } ?: throw IllegalArgumentException("Element with id $s not found")
}

inline fun <reified T : HTMLElement> byQuery(s: String): T {
  return document.querySelector(s)?.let { it as? T } ?: throw IllegalArgumentException("Element with query $s not found")
}

fun isSmallTouchDevice(): Boolean {
  return (window.innerWidth < 777 && window.navigator.maxTouchPoints > 0)
}

enum class DialogBoxCloseType {
  OK,
  CANCEL
}

object DialogBox {
  private val dialog = byQuery<HTMLDialogElement>("dialog")

  operator fun invoke(title: String, message: String, onClose: (closeType: DialogBoxCloseType) -> Unit = {}) {
    dialog.showModal()
    byQuery<HTMLHeadingElement>("dialog h2").innerText = title
    byQuery<HTMLHeadingElement>("dialog h4").innerText = message
    byQuery<HTMLButtonElement>("dialog button:last-of-type").onclick = {
      dialog.close()
      onClose(DialogBoxCloseType.CANCEL)
    }
    byQuery<HTMLButtonElement>("dialog button:first-of-type").onclick = {
      dialog.close()
      onClose(DialogBoxCloseType.OK)
    }
  }
}

object Toast {
  private var toastTimeout: Int? = null
  const val short = 3500
  const val long = 6000

  operator fun invoke(message: String, duration: Int = short) {
    if (toastTimeout != null) {
      window.clearTimeout(toastTimeout!!)
      toastClose()
    }
    toastTimeout = window.setTimeout({
      toastClose()
    }, duration)
    log("toast: $message")
    byId<HTMLDivElement>("toast").run {
      this.textContent = message
      this.style.opacity = "1"
    }
  }

  private fun toastClose() {
    byId<HTMLDivElement>("toast").style.opacity = "0"
    toastTimeout = null
  }
}

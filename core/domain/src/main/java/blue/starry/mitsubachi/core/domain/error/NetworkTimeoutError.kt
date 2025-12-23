package blue.starry.mitsubachi.core.domain.error

import java.io.IOException

data class NetworkTimeoutError(override val cause: IOException) : RetryableAppError() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

package blue.starry.mitsubachi.domain.error

class NetworkUnavailableError : RetryableAppError() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

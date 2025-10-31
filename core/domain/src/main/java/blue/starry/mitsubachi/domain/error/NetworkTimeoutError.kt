package blue.starry.mitsubachi.domain.error

data class NetworkTimeoutError(override val cause: Exception) : AppError() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

package blue.starry.mitsubachi.domain.error

data class NetworkTimeoutError(override val cause: Exception) : AppError() {
  companion object {
    private const val serialVersionUID = 1L
  }
}

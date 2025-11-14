package blue.starry.mitsubachi.core.domain.error

sealed class AppError : Exception() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

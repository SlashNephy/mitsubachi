package blue.starry.mitsubachi.core.domain.error

class UnauthorizedError : AppError() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

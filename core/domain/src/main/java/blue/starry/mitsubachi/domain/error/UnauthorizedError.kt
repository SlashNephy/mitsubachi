package blue.starry.mitsubachi.domain.error

class UnauthorizedError : AppError() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

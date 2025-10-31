package blue.starry.mitsubachi.domain.error

data object UnauthorizedError : AppError() {
  @JvmField
  @Suppress("MayBeConstant")
  val serialVersionUID = 1L

  fun readResolve(): Any = UnauthorizedError
}

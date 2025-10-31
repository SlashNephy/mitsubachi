package blue.starry.mitsubachi.domain.error

data object UnauthorizedError : AppError() {
  private const val serialVersionUID = 1L
  private fun readResolve(): Any = UnauthorizedError
}

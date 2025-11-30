package blue.starry.mitsubachi.core.domain.error

/**
 * AppError のうち、再試行が可能なエラーであることを表す型。
 */
sealed class RetryableAppError : AppError() {
  companion object {
    @JvmField
    @Suppress("MayBeConstant")
    val serialVersionUID = 1L
  }
}

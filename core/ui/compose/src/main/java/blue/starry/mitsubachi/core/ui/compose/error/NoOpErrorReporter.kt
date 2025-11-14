package blue.starry.mitsubachi.core.ui.compose.error

internal object NoOpErrorReporter : ErrorReporter {
  override fun report(exception: Exception) {
    // noop
  }
}

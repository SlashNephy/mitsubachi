package blue.starry.mitsubachi.ui.error

internal object NoOpErrorReporter : ErrorReporter {
  override fun report(exception: Exception) {
    // noop
  }
}

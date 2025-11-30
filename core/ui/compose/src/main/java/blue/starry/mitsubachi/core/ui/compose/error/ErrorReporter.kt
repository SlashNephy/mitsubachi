package blue.starry.mitsubachi.core.ui.compose.error

interface ErrorReporter {
  fun report(exception: Exception)
}

package blue.starry.mitsubachi.ui.error

interface ErrorReporter {
  fun report(exception: Exception)
}

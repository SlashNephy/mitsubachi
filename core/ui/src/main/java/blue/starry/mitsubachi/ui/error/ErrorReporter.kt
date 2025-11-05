package blue.starry.mitsubachi.ui.error

interface ErrorReporter {
  fun report(throwable: Throwable)
}

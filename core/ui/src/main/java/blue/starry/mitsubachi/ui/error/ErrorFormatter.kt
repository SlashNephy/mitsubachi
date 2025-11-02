package blue.starry.mitsubachi.ui.error

interface ErrorFormatter {
  fun format(throwable: Throwable): String
}

fun ErrorFormatter.format(throwable: Throwable, template: (String) -> String): String {
  return template(format(throwable))
}

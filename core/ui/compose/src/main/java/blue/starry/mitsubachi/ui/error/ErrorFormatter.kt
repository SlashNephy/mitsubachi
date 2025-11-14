package blue.starry.mitsubachi.ui.error

interface ErrorFormatter {
  fun format(exception: Exception): String
}

fun ErrorFormatter.format(exception: Exception, template: (String) -> String): String {
  return template(format(exception))
}

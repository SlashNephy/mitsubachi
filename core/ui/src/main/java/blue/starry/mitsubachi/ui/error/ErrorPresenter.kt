package blue.starry.mitsubachi.ui.error

interface ErrorPresenter {
  suspend fun handle(exception: Exception, template: (String) -> String = { it })
}

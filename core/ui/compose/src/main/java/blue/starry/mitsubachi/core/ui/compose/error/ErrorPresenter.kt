package blue.starry.mitsubachi.core.ui.compose.error

interface ErrorPresenter {
  suspend fun handle(exception: Exception, template: (String) -> String = { it })
}

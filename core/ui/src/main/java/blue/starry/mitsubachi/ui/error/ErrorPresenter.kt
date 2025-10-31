package blue.starry.mitsubachi.ui.error

interface ErrorPresenter {
  suspend fun handle(throwable: Throwable)
}

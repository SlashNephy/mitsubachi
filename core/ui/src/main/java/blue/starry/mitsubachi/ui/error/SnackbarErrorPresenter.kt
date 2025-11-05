package blue.starry.mitsubachi.ui.error

import blue.starry.mitsubachi.ui.snackbar.SnackbarHostService
import blue.starry.mitsubachi.ui.snackbar.enqueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarErrorPresenter @Inject constructor(
  private val formatter: ErrorFormatter,
  private val reporter: ErrorReporter,
  private val snackbarHostService: SnackbarHostService,
) : ErrorPresenter {
  override suspend fun handle(throwable: Throwable, template: (String) -> String) {
    reporter.report(throwable)

    val message = formatter.format(throwable, template)
    snackbarHostService.enqueue(message)
  }
}

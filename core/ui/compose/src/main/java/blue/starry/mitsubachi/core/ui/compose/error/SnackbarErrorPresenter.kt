package blue.starry.mitsubachi.core.ui.compose.error

import blue.starry.mitsubachi.core.ui.compose.snackbar.SnackbarHostService
import blue.starry.mitsubachi.core.ui.compose.snackbar.enqueue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarErrorPresenter @Inject constructor(
  private val formatter: ErrorFormatter,
  private val reporter: ErrorReporter,
  private val snackbarHostService: SnackbarHostService,
) : ErrorPresenter {
  override suspend fun handle(exception: Exception, template: (String) -> String) {
    reporter.report(exception)

    val message = formatter.format(exception, template)
    snackbarHostService.enqueue(message)
  }
}

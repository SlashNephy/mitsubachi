package blue.starry.mitsubachi.ui.error

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastErrorPresenter @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val formatter: ErrorFormatter,
  private val reporter: ErrorReporter,
) : ErrorPresenter {
  override suspend fun handle(exception: Exception, template: (String) -> String) {
    reporter.report(exception)

    val message = formatter.format(exception, template)
    withContext(Dispatchers.Main) {
      Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
  }
}

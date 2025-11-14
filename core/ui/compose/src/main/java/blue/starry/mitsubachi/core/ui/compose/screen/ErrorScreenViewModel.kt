package blue.starry.mitsubachi.core.ui.compose.screen

import androidx.lifecycle.ViewModel
import blue.starry.mitsubachi.core.ui.compose.error.ErrorFormatter
import blue.starry.mitsubachi.core.ui.compose.error.ErrorReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ErrorScreenViewModel @Inject constructor(
  reporter: ErrorReporter,
  formatter: ErrorFormatter,
) : ViewModel(), ErrorReporter by reporter, ErrorFormatter by formatter

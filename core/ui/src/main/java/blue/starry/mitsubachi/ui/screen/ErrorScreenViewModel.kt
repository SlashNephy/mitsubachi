package blue.starry.mitsubachi.ui.screen

import androidx.lifecycle.ViewModel
import blue.starry.mitsubachi.ui.error.ErrorFormatter
import blue.starry.mitsubachi.ui.error.ErrorReporter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ErrorScreenViewModel @Inject constructor(
  reporter: ErrorReporter,
  formatter: ErrorFormatter,
) : ViewModel(), ErrorReporter by reporter, ErrorFormatter by formatter

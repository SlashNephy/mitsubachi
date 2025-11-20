package blue.starry.mitsubachi.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import blue.starry.mitsubachi.core.domain.usecase.SignOutUseCase
import blue.starry.mitsubachi.core.ui.compose.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.core.ui.compose.formatter.RelativeDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
  private val signOutUseCase: SignOutUseCase,
  private val applicationSettingsRepository: ApplicationSettingsRepository,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
  relativeDateTimeFormatter: RelativeDateTimeFormatter,
) : ViewModel(), RelativeDateTimeFormatter by relativeDateTimeFormatter {
  sealed interface UiState {
    data object Loading : UiState

    @JvmInline
    value class Loaded(val applicationSettings: ApplicationSettings) : UiState
  }

  val state = applicationSettingsRepository.flow.map {
    UiState.Loaded(it)
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5.seconds),
    initialValue = UiState.Loading,
  )

  fun update(block: (ApplicationSettings) -> ApplicationSettings): Job {
    return viewModelScope.launch {
      runCatching {
        applicationSettingsRepository.update(block)
      }.onException { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }

  fun signOut(): Job {
    return viewModelScope.launch {
      runCatching {
        signOutUseCase()
      }.onException { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }
}

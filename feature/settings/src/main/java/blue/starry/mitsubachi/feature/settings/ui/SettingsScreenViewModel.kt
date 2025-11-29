package blue.starry.mitsubachi.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.UserSettings
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import blue.starry.mitsubachi.core.domain.usecase.FoursquareAccountRepository
import blue.starry.mitsubachi.core.domain.usecase.PhotoWidgetWorkerScheduler
import blue.starry.mitsubachi.core.domain.usecase.SignOutUseCase
import blue.starry.mitsubachi.core.domain.usecase.UserSettingsRepository
import blue.starry.mitsubachi.core.ui.compose.error.SnackbarErrorPresenter
import blue.starry.mitsubachi.core.ui.compose.error.onException
import blue.starry.mitsubachi.core.ui.compose.formatter.RelativeDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
  private val signOutUseCase: SignOutUseCase,
  foursquareAccountRepository: FoursquareAccountRepository,
  private val applicationSettingsRepository: ApplicationSettingsRepository,
  private val userSettingsRepository: UserSettingsRepository,
  private val snackbarErrorHandler: SnackbarErrorPresenter,
  private val photoWidgetWorkerScheduler: PhotoWidgetWorkerScheduler,
  val applicationConfig: ApplicationConfig,
  relativeDateTimeFormatter: RelativeDateTimeFormatter,
) : ViewModel(), RelativeDateTimeFormatter by relativeDateTimeFormatter {
  sealed interface UiState {
    data object Loading : UiState

    data class Loaded(
      val account: FoursquareAccount,
      val applicationSettings: ApplicationSettings,
      val userSettings: UserSettings,
    ) : UiState
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  val state = foursquareAccountRepository.primary
    .filterNotNull()
    .flatMapLatest { account ->
      combine(
        applicationSettingsRepository.flow,
        userSettingsRepository.flow(account),
      ) { applicationSettings, userSettings ->
        UiState.Loaded(account, applicationSettings, userSettings)
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5.seconds),
      initialValue = UiState.Loading,
    )

  fun updateApplicationSettings(block: (ApplicationSettings) -> ApplicationSettings): Job {
    return viewModelScope.launch {
      runCatching {
        applicationSettingsRepository.update(block)
      }.onException { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }

  fun updateUserSettings(block: (UserSettings) -> UserSettings): Job {
    return viewModelScope.launch {
      runCatching {
        val account = (state.value as? UiState.Loaded)?.account
        account?.also {
          userSettingsRepository.update(it, block)
        }
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

  fun onUpdatePhotoWidgetSchedule() {
    viewModelScope.launch {
      runCatching {
        photoWidgetWorkerScheduler.enqueue()
      }.onException { e ->
        snackbarErrorHandler.handle(e)
      }
    }
  }
}

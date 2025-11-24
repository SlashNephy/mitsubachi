package blue.starry.mitsubachi

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.ApplicationSettings
import blue.starry.mitsubachi.core.domain.usecase.ApplicationSettingsRepository
import blue.starry.mitsubachi.core.ui.common.deeplink.DeepLink
import blue.starry.mitsubachi.core.ui.common.deeplink.DeepLinkSerializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val deepLinkSerializer: DeepLinkSerializer,
  private val applicationSettingsRepository: ApplicationSettingsRepository,
) : ViewModel() {
  sealed interface UiState {
    data object Initializing : UiState
    data class Ready(val applicationSettings: ApplicationSettings) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Initializing)
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      applicationSettingsRepository.flow.collect { settings ->
        _state.value = UiState.Ready(settings)
      }
    }
  }

  fun buildInitialRouteKeys(intent: Intent): List<RouteKey> {
    val link = intent.data?.let { deepLinkSerializer.deserialize(it) }

    return when (link) {
      is DeepLink.CheckIn -> {
        listOf(
          RouteKey.Home, // ホームに戻れるようにする
          RouteKey.CheckInDetail.ById(id = link.id),
        )
      }

      null -> {
        listOf(RouteKey.Welcome)
      }
    }
  }
}

package blue.starry.mitsubachi.ui.feature.checkin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Immutable
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.usecase.SearchNearVenuesUseCase
import blue.starry.mitsubachi.ui.AccountEventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
@OptIn(FlowPreview::class)
class NearbyVenuesScreenViewModel @Inject constructor(
  @param:ApplicationContext private val context: Context,
  private val searchNearVenuesUseCase: SearchNearVenuesUseCase,
) : ViewModel(), AccountEventHandler {
  @Immutable
  sealed interface UiState {
    data class PermissionRequesting(val anyOf: List<String>) : UiState
    data object PermissionRequestDenied : UiState
    data object Loading : UiState
    data class Success(val venues: List<Venue>, val isRefreshing: Boolean) : UiState
    data class Error(val message: String) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state = _state.asStateFlow()

  init {
    refresh()
  }

  fun refresh(query: String? = null): Job {
    return viewModelScope.launch {
      fetch(query)
    }
  }

  private suspend fun fetch(query: String? = null) {
    if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION,
      ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION,
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      _state.value = UiState.PermissionRequesting(
        anyOf = listOf(
          Manifest.permission.ACCESS_COARSE_LOCATION,
          Manifest.permission.ACCESS_FINE_LOCATION,
        ),
      )
      return
    }

    val currentState = _state.value
    if (currentState is UiState.Success) {
      // 2回目以降の更新は isRefreshing=true
      _state.value = currentState.copy(isRefreshing = true)
    } else {
      _state.value = UiState.Loading
    }

    try {
      withTimeout(10.seconds) {
        runCatching {
          searchNearVenuesUseCase(query = query)
        }.onSuccess { data ->
          _state.value = UiState.Success(data, isRefreshing = false)
        }.onFailure { e ->
          _state.value = UiState.Error(e.localizedMessage ?: "unknown error")
        }
      }
    } catch (e: TimeoutCancellationException) {
      Timber.w(e, "fetch timeout")

      // タイムアウトした場合は前回の状態に戻す
      _state.value = currentState
    }
  }

  fun onPermissionResult(result: Map<String, Boolean>) {
    // ACCESS_COARSE_LOCATION と ACCESS_FINE_LOCATION のいずれかが許可されていればOK
    val anyGranted = result.any { it.value }
    if (anyGranted) {
      refresh()
      return
    }

    _state.value = UiState.PermissionRequestDenied
  }

  override fun onAccountDeleted() {
    _state.value = UiState.Loading
  }
}

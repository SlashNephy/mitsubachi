package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.usecase.CalculatePrefectureCompletionsUseCase
import blue.starry.mitsubachi.core.domain.usecase.FindFoursquareAccountUseCase
import blue.starry.mitsubachi.core.domain.usecase.PrefectureBoundaryRepository
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLevelRepository
import blue.starry.mitsubachi.core.ui.compose.error.onException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrefectureCompletionScreenViewModel @Inject constructor(
  private val calculatePrefectureCompletionsUseCase: CalculatePrefectureCompletionsUseCase,
  private val prefectureBoundaryRepository: PrefectureBoundaryRepository,
  private val prefectureLevelRepository: PrefectureLevelRepository,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
) : ViewModel() {
  sealed interface UiState {
    data object Loading : UiState

    data class Success(
      val summary: PrefectureCompletionSummary,
      val boundaries: ImmutableList<PrefectureBoundary>,
      val isRefreshing: Boolean,
    ) : UiState

    data class Error(val exception: Exception) : UiState
  }

  private val _state = MutableStateFlow<UiState>(UiState.Loading)
  val state: StateFlow<UiState> = _state.asStateFlow()

  init {
    refresh()
  }

  fun refresh(): Job {
    return viewModelScope.launch {
      fetch()
    }
  }

  fun setLevel(prefecture: Prefecture, level: PrefectureLevel): Job {
    return viewModelScope.launch {
      val account = findFoursquareAccountUseCase()
      if (account != null) {
        prefectureLevelRepository.set(account, prefecture, level)
        // 上書きはキャッシュ済みのベニュー履歴で再計算できるのでネットワークには行かない
        fetch(policy = FetchPolicy.CacheOrNetwork, keepPreviousState = true)
      }
    }
  }

  fun clearLevel(prefecture: Prefecture): Job {
    return viewModelScope.launch {
      val account = findFoursquareAccountUseCase()
      if (account != null) {
        prefectureLevelRepository.clear(account, prefecture)
        fetch(policy = FetchPolicy.CacheOrNetwork, keepPreviousState = true)
      }
    }
  }

  private suspend fun fetch(
    policy: FetchPolicy? = null,
    keepPreviousState: Boolean = false,
  ) {
    val currentState = state.value
    val isRefreshing = currentState is UiState.Success

    if (isRefreshing) {
      _state.value = currentState.copy(isRefreshing = !keepPreviousState)
    } else {
      _state.value = UiState.Loading
    }

    runCatching {
      // 初回読み込みはキャッシュを使い、リフレッシュ時はネットワークから取得する
      val effectivePolicy = policy
        ?: if (isRefreshing) FetchPolicy.NetworkOnly else FetchPolicy.CacheOrNetwork
      calculatePrefectureCompletionsUseCase(effectivePolicy) to
        prefectureBoundaryRepository.findAll().toImmutableList()
    }.onSuccess { (summary, boundaries) ->
      _state.value = UiState.Success(
        summary = summary,
        boundaries = boundaries,
        isRefreshing = false,
      )
    }.onException { e ->
      _state.value = UiState.Error(e)
    }
  }
}

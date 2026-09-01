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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

  // refresh / setLevel / clearLevel は独立に viewModelScope へ launch されるため、
  // 直列化しないと後発の書き込みを先発の再計算が上書きして表示が巻き戻る。
  // 書き込みと再計算を同じ区間に入れて、操作した順に永続化されるようにする
  private val mutex = Mutex()

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
      // 書き込みも同じロックの下に置く。外に出すと、素早く連続で操作したときに
      // 先発の書き込みが後発の書き込みの後に完了し、古いレベルが永続化されうる
      mutex.withLock {
        val account = findFoursquareAccountUseCase()
        if (account != null) {
          prefectureLevelRepository.set(account, prefecture, level)
          // 上書きはキャッシュ済みのベニュー履歴で再計算できるのでネットワークには行かない
          fetchLocked(policy = FetchPolicy.CacheOrNetwork, showRefreshIndicator = false)
        }
      }
    }
  }

  fun clearLevel(prefecture: Prefecture): Job {
    return viewModelScope.launch {
      mutex.withLock {
        val account = findFoursquareAccountUseCase()
        if (account != null) {
          prefectureLevelRepository.clear(account, prefecture)
          fetchLocked(policy = FetchPolicy.CacheOrNetwork, showRefreshIndicator = false)
        }
      }
    }
  }

  // 同時に走ると後発の書き込みを先発の再計算が上書きしてしまうので直列化する
  private suspend fun fetch(
    policy: FetchPolicy? = null,
    showRefreshIndicator: Boolean = true,
  ) {
    mutex.withLock {
      fetchLocked(policy = policy, showRefreshIndicator = showRefreshIndicator)
    }
  }

  // [mutex] を取得済みの呼び出し元から使う。Mutex は再入不可なのでここでは取らない
  //
  // policy: null なら初回はキャッシュ、リフレッシュ時はネットワークを使う
  // showRefreshIndicator: 再取得中のインジケータを出すか。
  //   手動上書き後のキャッシュ再計算は一瞬で終わるので出さない
  private suspend fun fetchLocked(
    policy: FetchPolicy?,
    showRefreshIndicator: Boolean,
  ) {
    val currentState = state.value
    val isRefreshing = currentState is UiState.Success

    if (isRefreshing) {
      _state.value = currentState.copy(isRefreshing = showRefreshIndicator)
    } else if (showRefreshIndicator) {
      // インジケータを出さない指定のときは、エラー表示のまま静かに再計算する
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

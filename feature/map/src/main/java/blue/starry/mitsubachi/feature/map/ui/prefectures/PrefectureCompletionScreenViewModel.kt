package blue.starry.mitsubachi.feature.map.ui.prefectures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import blue.starry.mitsubachi.core.domain.usecase.CalculatePrefectureCompletionsUseCase
import blue.starry.mitsubachi.core.domain.usecase.FetchUserVenueHistoriesUseCase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class PrefectureCompletionScreenViewModel @Inject constructor(
  private val fetchUserVenueHistoriesUseCase: FetchUserVenueHistoriesUseCase,
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
  // ロックで囲むのは「手動上書きの読み書き」「再計算」「状態の publish」だけで、
  // ベニュー履歴のネットワーク取得は外に出す。こうすると、リフレッシュのネットワーク待ちに
  // 手動上書きが引きずられない。再計算はロックの中で上書きをリポジトリから読み直すため、
  // ネットワークが返るのが後になっても、その時点の上書きを含んだ結果が publish される
  private val mutex = Mutex()

  // 直近に取得したベニュー履歴。手動上書き後の再計算に使う。[mutex] の中でだけ読み書きする
  private var histories: List<VenueHistory>? = null

  init {
    refresh()
  }

  fun refresh(): Job {
    return viewModelScope.launch {
      val isRefreshing = state.value is UiState.Success
      _state.update { current ->
        if (current is UiState.Success) current.copy(isRefreshing = true) else UiState.Loading
      }

      runCatching {
        // 初回読み込みはキャッシュを使い、リフレッシュ時はネットワークから取得する。
        // ここはロックの外なので、待っている間も手動上書きは先に進める
        fetchUserVenueHistoriesUseCase(
          if (isRefreshing) FetchPolicy.NetworkOnly else FetchPolicy.CacheOrNetwork,
        )
      }.onSuccess { fetched ->
        mutex.withLock {
          publishLocked(fetched, isRefreshing = false)
        }
      }.onException { e ->
        mutex.withLock {
          _state.value = UiState.Error(e)
        }
      }
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
          recalculateLocked()
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
          recalculateLocked()
        }
      }
    }
  }

  // 手動上書きを書き換えた後の再計算。取得済みの履歴だけを使うのでネットワークには行かない。
  //
  // 履歴が未取得なのは初回読み込みが終わっていないときだけで、そのときは画面もまだ
  // 一覧を出していない。進行中の読み込みがロックを取った時点で上書きを読み直すため、
  // ここで何もしなくても、いま書き込んだ上書きを含んだ結果が publish される
  private suspend fun recalculateLocked() {
    val fetched = histories
    if (fetched != null) {
      // リフレッシュ中に上書きした場合は、インジケータを消さずに再計算だけ反映する
      val isRefreshing = (state.value as? UiState.Success)?.isRefreshing == true
      publishLocked(fetched, isRefreshing = isRefreshing)
    }
  }

  // [mutex] を取得済みの呼び出し元から使う。Mutex は再入不可なのでここでは取らない。
  // 手動上書きはここで初めて読まれるので、ロックを取るまでに完了した書き込みが必ず反映される
  private suspend fun publishLocked(fetched: List<VenueHistory>, isRefreshing: Boolean) {
    histories = fetched

    runCatching {
      calculatePrefectureCompletionsUseCase(fetched) to
        prefectureBoundaryRepository.findAll().toImmutableList()
    }.onSuccess { (summary, boundaries) ->
      _state.value = UiState.Success(
        summary = summary,
        boundaries = boundaries,
        isRefreshing = isRefreshing,
      )
    }.onException { e ->
      _state.value = UiState.Error(e)
    }
  }
}

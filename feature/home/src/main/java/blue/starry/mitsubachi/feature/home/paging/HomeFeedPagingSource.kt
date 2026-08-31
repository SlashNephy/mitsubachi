package blue.starry.mitsubachi.feature.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.usecase.FetchFeedUseCase
import javax.inject.Inject

/**
 * ホームフィードのページング。
 *
 * キーは次に取得するページの先頭を指すマーカー (Swarm の `beforeMarker`) を表す。
 * Foursquare の `/checkins/recent` はページングに対応していないため、この場合は常に 1 ページで終端となる。
 */
class HomeFeedPagingSource @Inject constructor(
  private val fetchFeedUseCase: FetchFeedUseCase,
) : PagingSource<String, CheckIn>() {
  override fun getRefreshKey(state: PagingState<String, CheckIn>): String? {
    // 常に先頭から読み直す
    return null
  }

  @Suppress("TooGenericExceptionCaught")
  override suspend fun load(params: LoadParams<String>): LoadResult<String, CheckIn> {
    return try {
      val page = fetchFeedUseCase(
        limit = params.loadSize,
        beforeMarker = params.key,
        // リフレッシュ時は最新の状態を取得する
        policy = if (params is LoadParams.Refresh) FetchPolicy.NetworkOnly else FetchPolicy.CacheOrNetwork,
      )

      LoadResult.Page(
        data = page.checkIns,
        prevKey = null, // 前方向のページングのみ対応する
        nextKey = page.nextMarker,
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}

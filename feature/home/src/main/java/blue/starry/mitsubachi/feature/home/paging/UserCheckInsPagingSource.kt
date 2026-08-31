package blue.starry.mitsubachi.feature.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.usecase.FetchUserCheckInsUseCase
import javax.inject.Inject

/**
 * `/users/{userId}/checkins` は `limit` と `offset` によるページングに対応している。
 * キーは次に読み込むアイテムの offset を表す。
 */
class UserCheckInsPagingSource @Inject constructor(
  private val fetchUserCheckInsUseCase: FetchUserCheckInsUseCase,
) : PagingSource<Int, CheckIn>() {
  override fun getRefreshKey(state: PagingState<Int, CheckIn>): Int? {
    // 常に先頭から読み直す
    return null
  }

  @Suppress("TooGenericExceptionCaught")
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CheckIn> {
    val offset = params.key ?: 0

    return try {
      val checkIns = fetchUserCheckInsUseCase(
        limit = params.loadSize,
        offset = offset,
        // ページごとに offset が変わるためキャッシュを利用できるが、
        // リフレッシュ時は最新の状態を取得する
        policy = if (params is LoadParams.Refresh) FetchPolicy.NetworkOnly else FetchPolicy.CacheOrNetwork,
      )

      LoadResult.Page(
        data = checkIns,
        prevKey = null, // 前方向のページングのみ対応する
        // 返却件数が要求件数に満たない場合は末尾に到達したとみなす
        nextKey = if (checkIns.size < params.loadSize) null else offset + checkIns.size,
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}

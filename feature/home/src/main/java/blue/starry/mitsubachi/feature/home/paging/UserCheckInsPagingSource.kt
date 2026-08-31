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
        // offset は一覧の世代に依存する。キャッシュは offset を含む URL をキーとするため、
        // 追加読み込みでキャッシュを使うとリフレッシュ後の先頭ページと古い世代のページが
        // 混在し、項目の欠落や重複が起きる。そのため常にネットワークから取得する
        policy = FetchPolicy.NetworkOnly,
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

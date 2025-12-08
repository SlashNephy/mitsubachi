package blue.starry.mitsubachi.feature.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.usecase.FetchFeedUseCase
import java.time.ZonedDateTime
import javax.inject.Inject

class HomeFeedPagingSource @Inject constructor(
  private val fetchFeedUseCase: FetchFeedUseCase,
) : PagingSource<ZonedDateTime, CheckIn>() {
  override fun getRefreshKey(state: PagingState<ZonedDateTime, CheckIn>): ZonedDateTime? {
    // Return null to always start from the beginning on refresh
    return null
  }

  @Suppress("TooGenericExceptionCaught")
  override suspend fun load(params: LoadParams<ZonedDateTime>): LoadResult<ZonedDateTime, CheckIn> {
    return try {
      val after = params.key
      // Use NetworkOnly for refresh (when key is null), CacheOrNetwork for pagination
      val policy = if (params is LoadParams.Refresh && after == null) {
        FetchPolicy.NetworkOnly
      } else {
        FetchPolicy.CacheOrNetwork
      }

      val checkIns = fetchFeedUseCase(
        limit = params.loadSize,
        after = after,
        policy = policy,
      )

      LoadResult.Page(
        data = checkIns,
        prevKey = null, // Only support forward pagination
        // Use the last item's timestamp as the next key
        // The API should handle "after" as exclusive, so duplicates should not occur
        nextKey = if (checkIns.isEmpty()) null else checkIns.lastOrNull()?.timestamp,
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}

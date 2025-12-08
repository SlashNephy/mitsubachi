package blue.starry.mitsubachi.feature.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import blue.starry.mitsubachi.core.domain.model.CheckIn
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
      val checkIns = fetchFeedUseCase(
        limit = params.loadSize,
        after = after,
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

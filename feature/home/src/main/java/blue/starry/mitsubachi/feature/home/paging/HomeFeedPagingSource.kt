package blue.starry.mitsubachi.feature.home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import blue.starry.mitsubachi.core.domain.model.CheckIn
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.usecase.FetchFeedUseCase
import java.time.ZonedDateTime
import javax.inject.Inject

/**
 * Pagination key that contains both timestamp and the last check-in ID
 * to avoid duplicate keys when multiple check-ins have the same timestamp.
 */
data class PaginationKey(
  val timestamp: ZonedDateTime,
  val lastCheckInId: String,
)

class HomeFeedPagingSource @Inject constructor(
  private val fetchFeedUseCase: FetchFeedUseCase,
) : PagingSource<PaginationKey, CheckIn>() {
  override fun getRefreshKey(state: PagingState<PaginationKey, CheckIn>): PaginationKey? {
    // Return null to always start from the beginning on refresh
    return null
  }

  @Suppress("TooGenericExceptionCaught")
  override suspend fun load(params: LoadParams<PaginationKey>): LoadResult<PaginationKey, CheckIn> {
    return try {
      val key = params.key
      // Use NetworkOnly for refresh (when key is null), CacheOrNetwork for pagination
      val policy = if (params is LoadParams.Refresh && key == null) {
        FetchPolicy.NetworkOnly
      } else {
        FetchPolicy.CacheOrNetwork
      }

      val checkIns = fetchFeedUseCase(
        limit = params.loadSize,
        after = key?.timestamp,
        policy = policy,
      )

      // Filter out items that were already loaded (same timestamp, same or earlier ID)
      // This is necessary because the API might return some items with the same timestamp
      // as the pagination key when multiple check-ins occur at the same second
      val filteredCheckIns = if (key != null) {
        checkIns.filter { checkIn ->
          // Keep items with timestamp after the key timestamp, or
          // items with the same timestamp but ID lexicographically after the last seen ID
          // (Foursquare check-in IDs are hex strings that can be compared lexicographically)
          val isAfterKeyTimestamp = checkIn.timestamp > key.timestamp
          val isSameTimestampButAfterKeyId = checkIn.timestamp == key.timestamp && checkIn.id > key.lastCheckInId
          isAfterKeyTimestamp || isSameTimestampButAfterKeyId
        }
      } else {
        checkIns
      }

      LoadResult.Page(
        data = filteredCheckIns,
        prevKey = null, // Only support forward pagination
        // Use composite key with timestamp and ID to avoid duplicates
        nextKey = if (filteredCheckIns.isEmpty()) {
          null
        } else {
          filteredCheckIns.lastOrNull()?.let { lastCheckIn ->
            PaginationKey(
              timestamp = lastCheckIn.timestamp,
              lastCheckInId = lastCheckIn.id,
            )
          }
        },
      )
    } catch (e: Exception) {
      LoadResult.Error(e)
    }
  }
}

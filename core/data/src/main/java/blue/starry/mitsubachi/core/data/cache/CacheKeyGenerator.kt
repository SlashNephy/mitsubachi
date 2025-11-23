package blue.starry.mitsubachi.core.data.cache

/**
 * Generates cache keys for API requests.
 */
internal object CacheKeyGenerator {
  private const val PARAM_SEPARATOR = ":"
  private const val LIMIT_PARAM = "limit"
  private const val OFFSET_PARAM = "offset"

  fun forRecentCheckIns(
    limit: Int?,
    after: Long?,
    coordinates: String?,
  ): String = buildString {
    append("recent_checkins")
    limit?.let { append(formatLimitParam(it)) }
    after?.let { append("${PARAM_SEPARATOR}after=$it") }
    coordinates?.let { append("${PARAM_SEPARATOR}ll=$it") }
  }

  fun forCheckIn(checkInId: String): String = "checkin$PARAM_SEPARATOR$checkInId"

  fun forSearchNearVenues(
    coordinates: String,
    query: String?,
  ): String = buildString {
    append("search_venues$PARAM_SEPARATOR$coordinates")
    query?.let { append("${PARAM_SEPARATOR}query=$it") }
  }

  fun forSearchVenueRecommendations(
    coordinates: String,
  ): String = "venue_recommendations$PARAM_SEPARATOR$coordinates"

  fun forUser(userId: String): String = "user$PARAM_SEPARATOR$userId"

  fun forUserVenueHistories(userId: String): String = "user_venue_histories$PARAM_SEPARATOR$userId"

  fun forUserCheckIns(
    userId: String,
    limit: Int?,
    offset: Int?,
  ): String = buildString {
    append("user_checkins$PARAM_SEPARATOR$userId")
    limit?.let { append(formatLimitParam(it)) }
    offset?.let { append(formatOffsetParam(it)) }
  }

  fun forUserPhotos(
    userId: String,
    limit: Int?,
    offset: Int?,
  ): String = buildString {
    append("user_photos$PARAM_SEPARATOR$userId")
    limit?.let { append(formatLimitParam(it)) }
    offset?.let { append(formatOffsetParam(it)) }
  }

  private fun formatLimitParam(limit: Int) = "$PARAM_SEPARATOR$LIMIT_PARAM=$limit"
  private fun formatOffsetParam(offset: Int) = "$PARAM_SEPARATOR$OFFSET_PARAM=$offset"
}

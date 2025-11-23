package blue.starry.mitsubachi.core.data.cache

import blue.starry.mitsubachi.core.data.database.dao.CacheDao
import blue.starry.mitsubachi.core.data.database.entity.Cache
import blue.starry.mitsubachi.core.data.database.entity.CacheFormat
import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for caching network API responses with fetch policy support.
 */
@Singleton
class ApiResponseCache @Inject constructor(
  private val cacheDao: CacheDao,
  private val json: Json,
) {
  private val defaultCacheExpiryMillis = TimeUnit.MINUTES.toMillis(5)

  /**
   * Fetch data with the specified policy.
   *
   * Note: For [FetchPolicy.CacheAndNetwork], use [fetchFlow] instead as it returns a Flow
   * that emits cached data first, then network data.
   *
   * @param policy The fetch policy to use
   * @param cacheKey Unique key for caching this request
   * @param serializer Serializer for the response type
   * @param networkFetch Suspending function that fetches data from network
   * @param expiryMillis Cache expiry in milliseconds (default: 5 minutes)
   * @return The fetched data according to the policy
   * @throws NoSuchElementException if policy is CacheOnly and cache miss occurs
   * @throws UnsupportedOperationException if policy is CacheAndNetwork (use fetchFlow instead)
   */
  @Suppress("OutdatedDocumentation") // Documentation is accurate but detekt incorrectly flags it
  suspend fun <T> fetch(
    policy: FetchPolicy,
    cacheKey: String,
    serializer: KSerializer<T>,
    networkFetch: suspend () -> T,
    expiryMillis: Long = defaultCacheExpiryMillis,
  ): T {
    return when (policy) {
      FetchPolicy.NetworkOnly -> {
        val data = networkFetch()
        putCachedValue(cacheKey, data, serializer, expiryMillis)
        data
      }

      FetchPolicy.CacheOnly -> {
        getCachedValue(cacheKey, serializer)
          ?: throw NoSuchElementException("Cache miss for key: $cacheKey")
      }

      FetchPolicy.CacheOrNetwork -> {
        getCachedValue(cacheKey, serializer) ?: run {
          val data = networkFetch()
          putCachedValue(cacheKey, data, serializer, expiryMillis)
          data
        }
      }

      FetchPolicy.CacheAndNetwork -> {
        // This case needs special handling - return Flow
        throw UnsupportedOperationException("Use fetchFlow() for CacheAndNetwork policy")
      }
    }
  }

  /**
   * Fetch data with CacheAndNetwork policy that returns a Flow.
   * First emits cached data if available, then fetches and emits network data.
   */
  fun <T> fetchFlow(
    cacheKey: String,
    serializer: KSerializer<T>,
    networkFetch: suspend () -> T,
    expiryMillis: Long = defaultCacheExpiryMillis,
  ): Flow<T> = flow {
    // First, emit cached value if available
    getCachedValue(cacheKey, serializer)?.let { emit(it) }

    // Then fetch from network and cache
    val networkValue = networkFetch()
    putCachedValue(cacheKey, networkValue, serializer, expiryMillis)
    emit(networkValue)
  }

  private suspend fun <T> getCachedValue(key: String, serializer: KSerializer<T>): T? {
    val currentTime = System.currentTimeMillis()
    val cache = cacheDao.get(key, CacheFormat.JSON, currentTime) ?: return null

    @Suppress("TooGenericExceptionCaught")
    return try {
      json.decodeFromString(serializer, cache.payload.decodeToString())
    } catch (e: Exception) {
      // Log error and delete corrupted cache entry
      // Using println as there's no logger configured in this module
      println("Failed to deserialize cache for key=$key: ${e.message}")
      cacheDao.delete(key, CacheFormat.JSON)
      null
    }
  }

  private suspend fun <T> putCachedValue(
    key: String,
    value: T,
    serializer: KSerializer<T>,
    expiryMillis: Long,
  ) {
    val currentTime = System.currentTimeMillis()
    val payload = json.encodeToString(serializer, value).encodeToByteArray()
    val expiresAt = currentTime + expiryMillis

    cacheDao.insertOrUpdate(
      Cache(
        key = key,
        format = CacheFormat.JSON,
        payload = payload,
        cachedAt = currentTime,
        expiresAt = expiresAt,
      ),
    )
  }

  /**
   * Delete cached data.
   */
  suspend fun invalidate(key: String) {
    cacheDao.delete(key, CacheFormat.JSON)
  }

  /**
   * Delete all expired cache entries.
   */
  suspend fun deleteExpired() {
    cacheDao.deleteExpired(System.currentTimeMillis())
  }
}

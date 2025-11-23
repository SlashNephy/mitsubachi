package blue.starry.mitsubachi.core.domain.model

/**
 * Defines the caching strategy for API requests.
 */
enum class FetchPolicy {
  /**
   * Always fetch from network, ignore cache.
   */
  NetworkOnly,

  /**
   * Only fetch from cache, fail if not cached.
   */
  CacheOnly,

  /**
   * Try cache first, fall back to network if not available.
   */
  CacheOrNetwork,

  /**
   * Return cached data immediately if available, then fetch from network and update cache.
   */
  CacheAndNetwork,
}

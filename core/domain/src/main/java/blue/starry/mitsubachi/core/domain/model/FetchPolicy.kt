package blue.starry.mitsubachi.core.domain.model

/**
 * Defines the caching strategy for API requests.
 */
enum class FetchPolicy {
  NetworkOnly,

  CacheOnly,

  CacheOrNetwork,

  // TODO: 後で対応
  // CacheAndNetwork,
}

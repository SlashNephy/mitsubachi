package blue.starry.mitsubachi.core.domain.usecase

interface FoursquareApiClientFactory {
  fun create(bearerTokenSource: FoursquareBearerTokenSource): FoursquareApiClient
}

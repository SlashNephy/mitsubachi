package blue.starry.mitsubachi.domain.usecase

interface FoursquareApiClientFactory {
  fun create(bearerTokenSource: FoursquareBearerTokenSource): FoursquareApiClient
}

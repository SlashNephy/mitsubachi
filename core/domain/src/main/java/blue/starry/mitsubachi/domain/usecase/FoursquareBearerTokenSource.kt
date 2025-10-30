package blue.starry.mitsubachi.domain.usecase

fun interface FoursquareBearerTokenSource {
  suspend fun load(): String
}

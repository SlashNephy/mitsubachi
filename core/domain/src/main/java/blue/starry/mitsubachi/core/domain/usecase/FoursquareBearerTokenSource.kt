package blue.starry.mitsubachi.core.domain.usecase

fun interface FoursquareBearerTokenSource {
  suspend fun load(): String
}

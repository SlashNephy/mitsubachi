package blue.starry.mitsubachi.core.data.di

import io.ktor.client.HttpClientConfig

internal fun interface KtorConfig {
  fun HttpClientConfig<*>.configure()
}

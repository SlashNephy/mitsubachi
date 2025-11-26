package blue.starry.mitsubachi.core.data.di

import io.ktor.client.HttpClientConfig

fun interface KtorConfig {
  fun HttpClientConfig<*>.apply()
}

package blue.starry.mitsubachi.core.data.di

import io.ktor.client.HttpClientConfig

internal object ReleaseKtorConfig : KtorConfig {
  override fun HttpClientConfig<*>.apply() {}
}

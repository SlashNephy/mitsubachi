package blue.starry.mitsubachi.core.data.di

import io.ktor.client.HttpClientConfig

@Suppress("EmptyFunctionBlock")
internal object ReleaseKtorConfig : KtorConfig {
  override fun HttpClientConfig<*>.configure() {}
}

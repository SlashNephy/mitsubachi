package blue.starry.mitsubachi.core.data.network.cache

import io.ktor.client.HttpClient
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.ClientPluginBuilder
import io.ktor.client.plugins.api.ClientPluginInstance
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.util.AttributeKey

abstract class KtorClientPluginImpl<PluginConfig : Any>(
  name: String,
  createConfiguration: () -> PluginConfig,
) : ClientPlugin<PluginConfig> {
  private val underlying by lazy {
    createClientPlugin(name, createConfiguration) {
      block()
    }
  }

  abstract fun ClientPluginBuilder<PluginConfig>.block()

  final override val key: AttributeKey<ClientPluginInstance<PluginConfig>> = underlying.key

  final override fun install(plugin: ClientPluginInstance<PluginConfig>, scope: HttpClient) {
    underlying.install(plugin, scope)
  }

  final override fun prepare(block: PluginConfig.() -> Unit): ClientPluginInstance<PluginConfig> {
    return underlying.prepare(block)
  }
}

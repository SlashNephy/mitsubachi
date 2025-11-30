package blue.starry.mitsubachi.core.data.network

import io.ktor.client.plugins.api.createClientPlugin

private const val FOURSQUARE_API_VERSION = "20251012"

val FoursquareApiVersionPlugin = createClientPlugin(
  "FoursquareApiVersion",
  ::FoursquareApiVersionPluginConfig,
) {
  val version = pluginConfig.version

  onRequest { request, _ ->
    if ("v" !in request.url.parameters) {
      request.url.parameters.append("v", version)
    }
  }
}

class FoursquareApiVersionPluginConfig(var version: String = FOURSQUARE_API_VERSION)

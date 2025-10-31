package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.domain.error.NetworkTimeoutError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException

@Suppress("TooGenericExceptionCaught")
internal inline fun <T> runNetwork(block: () -> T): T {
  return try {
    block()
  } catch (e: Exception) {
    when (e) {
      is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> {
        throw NetworkTimeoutError(e)
      }

      else -> {
        throw e
      }
    }
  }
}

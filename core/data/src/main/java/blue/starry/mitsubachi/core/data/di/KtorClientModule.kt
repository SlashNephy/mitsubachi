package blue.starry.mitsubachi.core.data.di

import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.os.ConfigurationCompat
import blue.starry.mitsubachi.core.domain.error.NetworkTimeoutError
import blue.starry.mitsubachi.core.domain.error.NetworkUnavailableError
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpCallValidator
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
internal object KtorClientModule {
  @Provides
  @Singleton
  @Suppress("ThrowsCount")
  fun provide(
    config: ApplicationConfig,
    connectivityManager: ConnectivityManager,
  ): HttpClient {
    return HttpClient(OkHttp) {
      expectSuccess = true

      install(HttpTimeout) {
        val timeout = 10.seconds.inWholeMilliseconds
        requestTimeoutMillis = timeout
        connectTimeoutMillis = timeout
        socketTimeoutMillis = timeout
      }

      install(UserAgent) {
        agent =
          "Mitsubachi/${config.versionName}-${config.versionCode}-${config.buildType}-${config.flavor} (Android; +https://github.com/SlashNephy/mitsubachi)"
      }

      defaultRequest {
        header(HttpHeaders.AcceptLanguage, findPrimaryLanguageTag())
      }

      install(ContentNegotiation) {
        json(
          Json {
            ignoreUnknownKeys = true
            explicitNulls = false
          },
        )
      }

      if (config.isDebugBuild) {
        install(Logging) {
          logger = Logger.ANDROID
          format = LoggingFormat.Default
          level = LogLevel.ALL

          val sensitiveHeaders = listOf(
            HttpHeaders.Authorization,
            HttpHeaders.Cookie,
            HttpHeaders.SetCookie,
          )
          sanitizeHeader(predicate = sensitiveHeaders::contains)
        }
      }

      install("PreRequestCheck") {
        sendPipeline.intercept(HttpSendPipeline.Monitoring) {
          if (!connectivityManager.isNetworkAvailable()) {
            throw NetworkUnavailableError()
          }

          proceed()
        }
      }

      install(HttpCallValidator) {
        handleResponseExceptionWithRequest { throwable, _ ->
          when (throwable) {
            is HttpRequestTimeoutException, is ConnectTimeoutException, is SocketTimeoutException -> {
              throw NetworkTimeoutError(throwable)
            }

            else -> {
              throw throwable
            }
          }
        }
      }
    }
  }

  private fun findPrimaryLanguageTag(): String {
    val locales = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
    val primaryLocale = locales.get(0)

    return primaryLocale?.toLanguageTag() ?: "en-US"
  }

  private fun ConnectivityManager.isNetworkAvailable(): Boolean {
    val network = activeNetwork ?: return false
    val capabilities = getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && capabilities.hasCapability(
      NetworkCapabilities.NET_CAPABILITY_VALIDATED,
    )
  }
}

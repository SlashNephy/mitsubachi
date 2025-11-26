package blue.starry.mitsubachi.core.data.di

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.http.HttpHeaders

internal object DebugKtorConfig : KtorConfig {
    override fun HttpClientConfig<*>.configure() {
        // リリースビルドのサイズを削減するために設定を分割

        install(Logging) {
            logger = Logger.ANDROID
            format = LoggingFormat.Default
            level = LogLevel.ALL

            sanitizeHeader(predicate = sensitiveHeaders::contains)
        }
    }

    private val sensitiveHeaders = listOf(
        HttpHeaders.Authorization,
        HttpHeaders.Cookie,
        HttpHeaders.SetCookie,
    )
}

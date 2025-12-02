package blue.starry.mitsubachi.core.data.network

import android.content.Context
import android.content.pm.PackageManager
import blue.starry.mitsubachi.core.data.network.model.toDomain
import blue.starry.mitsubachi.core.domain.model.GoogleWebFont
import blue.starry.mitsubachi.core.domain.usecase.GoogleWebFontClient
import dagger.hilt.android.qualifiers.ApplicationContext
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GoogleWebFontClientImpl @Inject constructor(
  private val httpClient: HttpClient,
  @ApplicationContext context: Context,
) : GoogleWebFontClient {
  private val network = Ktorfit.Builder()
    .httpClient(httpClient)
    .build()
    .createGoogleWebFontNetwork()

  private val apiKey = context.getMetadata("com.google.webfonts.API_KEY")

  override suspend fun listWebFonts(): List<GoogleWebFont> {
    return network.listWebFonts(apiKey).toDomain()
  }

  private fun Context.getMetadata(key: String): String {
    val applicationInfo = packageManager.getApplicationInfo(
      packageName,
      PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
    )

    return applicationInfo.metaData?.getString(key) ?: error("Metadata not found: $key")
  }
}

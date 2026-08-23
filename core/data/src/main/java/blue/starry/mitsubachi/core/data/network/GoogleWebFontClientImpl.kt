package blue.starry.mitsubachi.core.data.network

import android.content.Context
import android.content.pm.PackageManager
import blue.starry.mitsubachi.core.data.network.model.toDomain
import blue.starry.mitsubachi.core.domain.model.GoogleWebFont
import blue.starry.mitsubachi.core.domain.usecase.GoogleWebFontClient
import dagger.hilt.android.qualifiers.ApplicationContext
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import java.security.MessageDigest
import java.util.HexFormat
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
  private val androidPackage = context.packageName
  private val androidCert = context.getSigningCertificateSha1()

  override suspend fun listWebFonts(): List<GoogleWebFont> {
    return network.listWebFonts(apiKey, androidPackage, androidCert).toDomain()
  }

  private fun Context.getMetadata(key: String): String {
    val applicationInfo = packageManager.getApplicationInfo(
      packageName,
      PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
    )

    return applicationInfo.metaData?.getString(key) ?: error("Metadata not found: $key")
  }

  // 署名証明書のフィンガープリントを実行時に求めることで、
  // ビルドバリアントごとの署名の差異 (debug 証明書 / リリース鍵) を自動的に吸収する。
  private fun Context.getSigningCertificateSha1(): String {
    val signingInfo = packageManager.getPackageInfo(
      packageName,
      PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong()),
    ).signingInfo ?: error("Signing info not found: $packageName")

    val certificate = signingInfo.apkContentsSigners.first()
    val digest = MessageDigest.getInstance("SHA-1").digest(certificate.toByteArray())

    return HexFormat.of().withUpperCase().formatHex(digest)
  }
}

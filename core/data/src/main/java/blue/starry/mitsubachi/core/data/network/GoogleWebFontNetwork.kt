package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.model.GoogleWebFontListResponse
import de.jensklingenberg.ktorfit.core.NoDelegation
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header

internal const val GOOGLE_API_KEY_HEADER = "X-Goog-Api-Key"

interface GoogleWebFontNetwork : @NoDelegation NetworkApi {
  /**
   * API キーに設定した Android アプリの制限は、リクエストヘッダー X-Android-Package と
   * X-Android-Cert の組をサーバー側で検証することで実現されている。
   * Maps SDK for Android は SDK 内部でこれらを付与するが、素の HTTP 呼び出しでは付与されないため、
   * ここで明示的に送る。付与しない場合、制限付きキーは 403 で拒否される。
   *
   * API キー自体もクエリパラメータではなくヘッダーで送り、URL がログ等に残らないようにする。
   * ヘッダー値は DebugKtorConfig の sanitizeHeader でマスクする。
   */
  @GET("https://www.googleapis.com/webfonts/v1/webfonts")
  suspend fun listWebFonts(
    @Header(GOOGLE_API_KEY_HEADER) apiKey: String,
    @Header("X-Android-Package") androidPackage: String,
    @Header("X-Android-Cert") androidCert: String,
  ): GoogleWebFontListResponse
}

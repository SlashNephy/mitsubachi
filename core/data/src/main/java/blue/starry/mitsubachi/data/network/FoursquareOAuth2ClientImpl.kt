package blue.starry.mitsubachi.data.network

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import blue.starry.mitsubachi.data.network.model.FoursquareApiResponse
import blue.starry.mitsubachi.data.network.model.FoursquareUserDetailsResponse
import blue.starry.mitsubachi.data.network.model.toDomain
import blue.starry.mitsubachi.domain.model.ApplicationConfig
import blue.starry.mitsubachi.domain.model.FoursquareAccount
import blue.starry.mitsubachi.domain.usecase.FoursquareOAuth2Client
import dagger.hilt.android.qualifiers.ApplicationContext
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoursquareOAuth2ClientImpl @Inject constructor(
  @ApplicationContext context: Context,
  private val httpClient: HttpClient,
  private val config: ApplicationConfig,
) : FoursquareOAuth2Client, Closeable {
  private companion object {
    const val AUTHORIZATION_ENDPOINT = "https://foursquare.com/oauth2/authorize"
    const val TOKEN_ENDPOINT = "https://foursquare.com/oauth2/access_token"
    const val REDIRECT_URI = "blue.starry.mitsubachi://oauth2/callback"
  }

  private val ktorfit =
    Ktorfit
      .Builder()
      .baseUrl("https://foursquare.com", checkUrl = false)
      .httpClient(httpClient)
      .build()
      .createFoursquareOAuth2NetworkApi()

  private val configuration = AuthorizationServiceConfiguration(
    AUTHORIZATION_ENDPOINT.toUri(),
    TOKEN_ENDPOINT.toUri(),
  )
  private val service = AuthorizationService(context)

  override fun createAuthorizationIntent(): Intent {
    val request = AuthorizationRequest
      .Builder(
        configuration,
        config.foursquareClientId,
        ResponseTypeValues.CODE,
        REDIRECT_URI.toUri(),
      )
      .build()
    return service.getAuthorizationRequestIntent(
      request,
      // TODO: AuthTabIntent を使うようにしたい。脱 AppAuth も検討。
      service
        .createCustomTabsIntentBuilder()
        .setUrlBarHidingEnabled(true)
        .setShowTitle(false)
        .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
        .build(),
    )
  }

  override suspend fun exchangeToken(authorizationResult: Intent): FoursquareAccount {
    val authorizationException = AuthorizationException.fromIntent(authorizationResult)
    if (authorizationException != null) {
      throw authorizationException
    }

    val authorizationResponse = AuthorizationResponse.fromIntent(authorizationResult)
    if (authorizationResponse == null) {
      error("Both AuthorizationResponse and AuthorizationException are null.")
    }

    val tokenRequest = authorizationResponse.createTokenExchangeRequest()
    val tokenResponse = postTokenRequest(tokenRequest)

    val authState = AuthState(authorizationResponse, tokenResponse, null)
    val accessToken = checkNotNull(authState.accessToken)
    val userDetails = getUserDetails(accessToken)
    val user = userDetails.response.user.toDomain()

    return FoursquareAccount(
      id = user.id,
      displayName = user.displayName,
      iconUrl = user.iconUrl,
      accessToken = accessToken,
      authStateJson = authState.jsonSerializeString(),
    )
  }

  private suspend fun postTokenRequest(request: TokenRequest): TokenResponse {
    // AppAuth で実装したいが、必須フィールドがトークンレスポンスに含まれないので諦めて自前で実装
    // org.json.JSONException: field "token_type" not found in json object
    // val clientAuthentication = ClientSecretBasic(CLIENT_SECRET)
    //
    // return suspendCancellableCoroutine { continuation ->
    //   service.performTokenRequest(request, clientAuthentication) { tokenResponse, tokenException ->
    //     when {
    //       tokenException != null -> {
    //         continuation.resumeWithException(tokenException)
    //       }
    //
    //       tokenResponse != null -> {
    //         continuation.resume(tokenResponse)
    //       }
    //
    //       else -> {
    //         continuation.cancel()
    //       }
    //     }
    //   }
    // }

    val data = ktorfit.getAccessToken(
      clientId = config.foursquareClientId,
      clientSecret = config.foursquareClientSecret,
      grantType = request.grantType,
      redirectUri = request.redirectUri.toString(),
      code = checkNotNull(request.authorizationCode),
      codeVerifier = checkNotNull(request.codeVerifier),
    )
    return TokenResponse
      .Builder(request)
      .setRequest(request)
      .setAccessToken(data.accessToken)
      .setTokenType("Bearer")
      .build()
  }

  // TODO: このメソッドを FoursquareApiClientImpl 側に移動する
  private suspend fun getUserDetails(accessToken: String): FoursquareApiResponse<FoursquareUserDetailsResponse> {
    val response = httpClient.get("https://api.foursquare.com/v2/users/self?v=20251020") {
      headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
    }
    return response.body()
  }

  override fun close() {
    service.dispose()
  }
}

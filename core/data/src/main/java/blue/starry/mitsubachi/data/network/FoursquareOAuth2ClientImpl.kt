package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareTokenResponse
import blue.starry.mitsubachi.domain.model.ApplicationConfig
import blue.starry.mitsubachi.domain.model.FoursquareAccount
import blue.starry.mitsubachi.domain.model.OAuth2AuthorizationRequest
import blue.starry.mitsubachi.domain.model.OAuth2AuthorizationResponse
import blue.starry.mitsubachi.domain.usecase.FoursquareApiClientFactory
import blue.starry.mitsubachi.domain.usecase.FoursquareOAuth2Client
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.http.buildUrl
import io.ktor.http.takeFrom
import org.apache.commons.lang3.RandomStringUtils
import java.net.URI
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoursquareOAuth2ClientImpl @Inject constructor(
  httpClient: HttpClient,
  private val config: ApplicationConfig,
  private val apiClientFactory: FoursquareApiClientFactory,
) : FoursquareOAuth2Client {
  private val ktorfit = Ktorfit
    .Builder()
    .baseUrl("https://foursquare.com", checkUrl = false)
    .httpClient(httpClient)
    .build()
    .createFoursquareOAuth2NetworkApi()

  override fun createAuthorizationRequest(): OAuth2AuthorizationRequest {
    val generator = RandomStringUtils.secureStrong()
    val state = generator.nextAlphanumeric(32)
    val codeVerifier = generator.nextAlphanumeric(64)
    val codeChallenge = MessageDigest.getInstance("SHA-256").run {
      val input = codeVerifier.toByteArray(Charsets.UTF_8)
      val hash = digest(input)
      Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }

    val url = buildUrl {
      takeFrom("https://foursquare.com/oauth2/authorize")

      parameters.append("client_id", config.foursquareClientId)
      parameters.append("response_type", "code")
      parameters.append("redirect_uri", config.foursquareRedirectUri)
      parameters.append("state", state)
      parameters.append("code_challenge", codeChallenge)
      parameters.append("code_challenge_method", "S256")
    }.toString()

    return OAuth2AuthorizationRequest(
      authorizeUrl = url,
      redirectScheme = URI.create(config.foursquareRedirectUri).scheme,
      state = state,
      codeVerifier = codeVerifier,
    )
  }

  override suspend fun exchangeToken(response: OAuth2AuthorizationResponse): FoursquareAccount {
    check(response.state == response.request.state)

    val token = getAccessToken(response.code, response.request.codeVerifier)
    val foursquare = apiClientFactory.create { token.accessToken }
    val user = foursquare.getUser()

    return FoursquareAccount(
      id = user.id,
      displayName = user.displayName,
      iconUrl = user.iconUrl,
      accessToken = token.accessToken,
    )
  }

  private suspend fun getAccessToken(
    code: String,
    codeVerifier: String,
  ): FoursquareTokenResponse {
    return ktorfit.runNetwork {
      getAccessToken(
        clientId = config.foursquareClientId,
        clientSecret = config.foursquareClientSecret,
        grantType = "authorization_code",
        redirectUri = config.foursquareRedirectUri,
        code = code,
        codeVerifier = codeVerifier,
      )
    }
  }
}

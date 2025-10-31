package blue.starry.mitsubachi.data.network

import blue.starry.mitsubachi.data.network.model.FoursquareTokenResponse
import de.jensklingenberg.ktorfit.core.NoDelegation
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.POST

interface FoursquareOAuth2NetworkApi : @NoDelegation NetworkApi {
  @FormUrlEncoded
  @POST("/oauth2/access_token")
  suspend fun getAccessToken(
    @Field("client_id") clientId: String,
    @Field("client_secret") clientSecret: String,
    @Field("grant_type") grantType: String,
    @Field("redirect_uri") redirectUri: String,
    @Field("code") code: String,
    @Field("code_verifier") codeVerifier: String,
  ): FoursquareTokenResponse
}

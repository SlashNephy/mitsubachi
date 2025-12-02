package blue.starry.mitsubachi.core.data.network

import blue.starry.mitsubachi.core.data.network.model.GoogleWebFontListResponse
import de.jensklingenberg.ktorfit.core.NoDelegation
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface GoogleWebFontNetwork : @NoDelegation NetworkApi {
  @GET("https://www.googleapis.com/webfonts/v1/webfonts")
  suspend fun listWebFonts(@Query key: String): GoogleWebFontListResponse
}

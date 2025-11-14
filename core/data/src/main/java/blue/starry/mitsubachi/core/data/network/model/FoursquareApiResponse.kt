package blue.starry.mitsubachi.core.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class FoursquareApiResponse<R>(
  val meta: Meta,
  val response: R,
) {
  @Serializable
  data class Meta(
    val code: Int, // 200
    val requestId: String, // 68ec676677cbac59a321259d
  )
}

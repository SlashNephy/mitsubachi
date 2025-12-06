package blue.starry.mitsubachi.core.ui.common.deeplink

import android.net.Uri
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeepLinkSerializerImpl @Inject constructor(
  private val applicationConfig: ApplicationConfig,
) : DeepLinkSerializer {
  private companion object {
    const val CHECK_IN_AUTHORITY = "check_in"
    const val CREATE_CHECK_IN_AUTHORITY = "create_check_in"
  }

  override fun serialize(link: DeepLink): Uri {
    return Uri.Builder()
      .scheme(applicationConfig.applicationId)
      .apply {
        when (link) {
          is DeepLink.CheckIn -> {
            authority(CHECK_IN_AUTHORITY)
            appendPath(link.id)
          }

          is DeepLink.CreateCheckIn -> {
            authority(CREATE_CHECK_IN_AUTHORITY)
          }
        }
      }
      .build()
  }

  override fun deserialize(uri: Uri): DeepLink? {
    if (uri.scheme != applicationConfig.applicationId) {
      return null
    }

    Timber.d("deserializing deep link: $uri")

    return when (uri.authority) {
      CHECK_IN_AUTHORITY -> {
        val id = uri.pathSegments.firstOrNull() ?: return null
        DeepLink.CheckIn(id)
      }

      CREATE_CHECK_IN_AUTHORITY -> {
        DeepLink.CreateCheckIn
      }

      else -> {
        null
      }
    }
  }
}

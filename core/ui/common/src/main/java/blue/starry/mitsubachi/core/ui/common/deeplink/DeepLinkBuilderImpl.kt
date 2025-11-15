package blue.starry.mitsubachi.core.ui.common.deeplink

import android.net.Uri
import blue.starry.mitsubachi.core.domain.model.ApplicationConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeepLinkBuilderImpl @Inject constructor(
  private val applicationConfig: ApplicationConfig,
) : DeepLinkBuilder {
  private companion object {
    const val CHECK_IN_AUTHORITY = "check_in"
  }

  override fun parseLink(uri: Uri): DeepLink? {
    if (uri.scheme != applicationConfig.applicationId) {
      return null
    }

    return when (uri.authority) {
      CHECK_IN_AUTHORITY -> {
        val id = uri.pathSegments.firstOrNull() ?: return null
        DeepLink.CheckIn(id)
      }

      else -> {
        null
      }
    }
  }

  override fun buildCheckInLink(checkInId: String): Uri {
    return Uri.Builder()
      .scheme(applicationConfig.applicationId)
      .authority(CHECK_IN_AUTHORITY)
      .appendPath(checkInId)
      .build()
  }
}

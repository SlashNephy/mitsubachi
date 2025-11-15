package blue.starry.mitsubachi.core.ui.common.deeplink

import android.net.Uri

interface DeepLinkBuilder {
  fun parseLink(uri: Uri): DeepLink?

  fun buildCheckInLink(checkInId: String): Uri
}

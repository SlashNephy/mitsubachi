package blue.starry.mitsubachi.core.ui.common.deeplink

import android.net.Uri

interface DeepLinkSerializer {
  fun serialize(link: DeepLink): Uri

  fun deserialize(uri: Uri): DeepLink?
}

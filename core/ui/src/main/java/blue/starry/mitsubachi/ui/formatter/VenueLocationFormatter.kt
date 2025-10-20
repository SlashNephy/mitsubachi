package blue.starry.mitsubachi.ui.formatter

import blue.starry.mitsubachi.domain.model.VenueLocation
import java.util.Locale

object VenueLocationFormatter {
  fun formatAddress(location: VenueLocation, includeCrossStreet: Boolean = true): String =
    buildString {
      val base = when {
        location.neighborhood != null -> {
          location.neighborhood
        }

        location.state != null && location.city != null && location.state != location.city -> {
          when (location.countryCode) {
            "JP", "CN", "KR", "TW", "VN" -> {
              // 日本、韓国、中国、台湾、ベトナムは「都道府県＋市区町村」の順番
              "${location.state}${location.city}"
            }

            else -> {
              // それ以外は「市区町村＋都道府県」の順番
              "${location.city}, ${location.state}"
            }
          }
        }

        location.city != null -> {
          location.city
        }

        location.state != null -> {
          location.state
        }

        else -> {
          location.country
        }
      }
      append(base)

      if (includeCrossStreet && location.crossStreet != null) {
        append(" (")
        append(location.crossStreet)
        append(')')
      }
    }

  fun formatDistance(meters: Int): String {
    // 10m 単位で丸める
    val meters = (meters / 10.0).toInt() * 10

    return if (meters < 1000) {
      "${meters}m"
    } else {
      String.format(Locale.getDefault(), "%.1fkm", meters / 1000.0)
    }
  }
}

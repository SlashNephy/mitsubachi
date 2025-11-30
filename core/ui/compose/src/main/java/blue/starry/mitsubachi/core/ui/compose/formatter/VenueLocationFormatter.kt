package blue.starry.mitsubachi.core.ui.compose.formatter

import blue.starry.mitsubachi.core.domain.model.VenueLocation
import java.util.Locale

object VenueLocationFormatter {
  fun formatAddress(
    location: VenueLocation,
    locale: Locale = Locale.getDefault(),
    withCountry: Boolean = location.countryCode != locale.country,
    withPostalCode: Boolean = withCountry,
    withCrossStreet: Boolean = true,
    withAddress: Boolean = withCrossStreet,
  ): String {
    return buildString {
      when (locale.country) {
        // 漢字圏
        // 〒$postalCode $state$city$address $crossStreet ($country)
        "JP", "CN", "TW", "HK", "MO" -> {
          appendCjkFormat(location, withCountry, withPostalCode, withCrossStreet, withAddress)
        }

        // 非漢字圏のアジア・東欧
        // $postalCode $state $city $address $crossStreet ($country)
        "KR", "HU" -> {
          appendAsianEasternEuropeanFormat(location, withCountry, withPostalCode, withCrossStreet, withAddress)
        }

        // ヨーロッパ大陸形式
        // $crossStreet, $address, $postalCode $city, $state, $country
        "DE", "FR", "IT", "ES", "NL", "SE", "NO", "FI", "DK", "BR" -> {
          appendContinentalEuropeanFormat(location, withCountry, withPostalCode, withCrossStreet, withAddress)
        }

        // アングロサクソン形式
        // $crossStreet, $address, $city, $state $postalCode, $country
        else -> {
          appendAngloSaxonFormat(location, withCountry, withPostalCode, withCrossStreet, withAddress)
        }
      }
    }.trim().ifEmpty { location.country }
  }

  private fun StringBuilder.appendCjkFormat(
    location: VenueLocation,
    withCountry: Boolean,
    withPostalCode: Boolean,
    withCrossStreet: Boolean,
    withAddress: Boolean,
  ) {
    if (withPostalCode && location.postalCode != null) {
      append('〒')
      append(location.postalCode)
      append(' ')
    }

    location.state?.also {
      append(it)
    }

    location.city?.also {
      append(it)
    }

    if (withAddress) {
      location.address?.also {
        append(it)
      }
    }

    if (withCrossStreet) {
      location.crossStreet?.also {
        append(' ')
        append(it)
      }
    }

    if (withCountry) {
      append(" (")
      append(location.country)
      append(')')
    }
  }

  private fun StringBuilder.appendAsianEasternEuropeanFormat(
    location: VenueLocation,
    withCountry: Boolean,
    withPostalCode: Boolean,
    withCrossStreet: Boolean,
    withAddress: Boolean,
  ) {
    if (withPostalCode) {
      location.postalCode?.also {
        append(it)
        append(' ')
      }
    }

    location.state?.also {
      append(it)
      append(' ')
    }

    location.city?.also {
      append(it)
      append(' ')
    }

    if (withAddress) {
      location.address?.also {
        append(it)
        append(' ')
      }
    }

    if (withCrossStreet) {
      location.crossStreet?.also {
        append(it)
        append(' ')
      }
    }

    if (withCountry) {
      append("(${location.country})")
    }
  }

  private fun StringBuilder.appendContinentalEuropeanFormat(
    location: VenueLocation,
    withCountry: Boolean,
    withPostalCode: Boolean,
    withCrossStreet: Boolean,
    withAddress: Boolean,
  ) {
    if (withCrossStreet) {
      location.crossStreet?.also {
        append(it)
        append(", ")
      }
    }

    if (withAddress) {
      location.address?.also {
        append(it)
        append(", ")
      }
    }

    if (withPostalCode) {
      location.postalCode?.also {
        append(it)
        append(' ')
      }
    }

    location.city?.also {
      append(it)
      append(", ")
    }

    location.state?.also {
      append(it)
      append(", ")
    }

    if (withCountry) {
      append(location.country)
    }
  }

  private fun StringBuilder.appendAngloSaxonFormat(
    location: VenueLocation,
    withCountry: Boolean,
    withPostalCode: Boolean,
    withCrossStreet: Boolean,
    withAddress: Boolean,
  ) {
    if (withCrossStreet) {
      location.crossStreet?.also {
        append(it)
        append(", ")
      }
    }

    if (withAddress) {
      location.address?.also {
        append(it)
        append(", ")
      }
    }

    location.city?.also {
      append(it)
      append(", ")
    }

    location.state?.also {
      append(it)
      append(' ')
    }

    if (withPostalCode) {
      location.postalCode?.also {
        append(it)
        append(", ")
      }
    }

    if (withCountry) {
      append(location.country)
    }
  }
}

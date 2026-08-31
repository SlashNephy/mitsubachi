package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletion
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.model.Venue
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private const val JAPAN_COUNTRY_CODE = "JP"

/**
 * ベニュー履歴・境界ポリゴン・手動上書きを束ねて 47 都道府県分の踏破度を判定する。
 *
 * 都道府県の判定は次の優先順で行う。
 * 1. `countryCode` が JP でなければ海外のベニューとして国コードのみを記録し、都道府県判定は行わない
 * 2. `VenueLocation.state` を [PrefectureNameResolver] で解決する。Foursquare が住所から導出した値であり、
 *    県境の座標より信頼できる
 * 3. state が解決できない場合のみ [PrefectureLocator] で座標から判定する
 */
@Singleton
class CalculatePrefectureCompletionsUseCase @Inject constructor(
  private val fetchUserVenueHistoriesUseCase: FetchUserVenueHistoriesUseCase,
  private val prefectureBoundaryRepository: PrefectureBoundaryRepository,
  private val prefectureLevelRepository: PrefectureLevelRepository,
  private val findFoursquareAccountUseCase: FindFoursquareAccountUseCase,
) {
  suspend operator fun invoke(
    policy: FetchPolicy = FetchPolicy.CacheOrNetwork,
  ): PrefectureCompletionSummary {
    val histories = fetchUserVenueHistoriesUseCase(policy)
    val locator = PrefectureLocator(prefectureBoundaryRepository.findAll())

    val venueCounts = mutableMapOf<Prefecture, Int>()
    val stayed = mutableSetOf<Prefecture>()
    val countryCodes = mutableSetOf<String>()

    for (history in histories) {
      val venue = history.venue
      if (!venue.location.countryCode.equals(JAPAN_COUNTRY_CODE, ignoreCase = true)) {
        countryCodes += venue.location.countryCode.uppercase()
        continue
      }

      val prefecture = locate(venue, locator)
      if (prefecture != null) {
        venueCounts[prefecture] = (venueCounts[prefecture] ?: 0) + 1
        if (StayVenueCategories.matches(venue)) {
          stayed += prefecture
        }
      }
    }

    val overrides = findFoursquareAccountUseCase()
      ?.let { prefectureLevelRepository.flow(it).first() }
      .orEmpty()

    val completions = Prefecture.entries.map { prefecture ->
      val count = venueCounts[prefecture] ?: 0
      PrefectureCompletion(
        prefecture = prefecture,
        automaticLevel = automaticLevelOf(count, prefecture in stayed),
        manualLevel = overrides[prefecture],
        venueCount = count,
      )
    }

    return PrefectureCompletionSummary(
      completions = completions.toImmutableList(),
      visitedCountryCodes = countryCodes.toImmutableSet(),
    )
  }

  // state は Foursquare が住所から導出した値で、県境では座標より信頼できるため先に見る
  private fun locate(venue: Venue, locator: PrefectureLocator): Prefecture? {
    PrefectureNameResolver.resolve(venue.location.state)?.also {
      return it
    }
    return locator.locate(
      latitude = venue.location.latitude,
      longitude = venue.location.longitude,
    )
  }

  private fun automaticLevelOf(venueCount: Int, hasStayVenue: Boolean): PrefectureLevel {
    return when {
      venueCount == 0 -> PrefectureLevel.Unvisited
      hasStayVenue -> PrefectureLevel.Stayed
      else -> PrefectureLevel.Visited
    }
  }
}

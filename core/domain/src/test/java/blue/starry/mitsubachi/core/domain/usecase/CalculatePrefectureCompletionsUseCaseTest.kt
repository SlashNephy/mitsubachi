package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FetchPolicy
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.model.PrefectureCompletionSummary
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.model.Venue
import blue.starry.mitsubachi.core.domain.model.VenueCategory
import blue.starry.mitsubachi.core.domain.model.VenueLocation
import blue.starry.mitsubachi.core.domain.model.foursquare.VenueHistory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import java.time.ZonedDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class CalculatePrefectureCompletionsUseCaseTest {
  private val account = mockk<FoursquareAccount>(relaxed = true)
  private val fetchUserVenueHistories = mockk<FetchUserVenueHistoriesUseCase>()
  private val boundaryRepository = mockk<PrefectureBoundaryRepository>()
  private val levelRepository = mockk<PrefectureLevelRepository>()
  private val findFoursquareAccount = mockk<FindFoursquareAccountUseCase>()

  private val useCase = CalculatePrefectureCompletionsUseCase(
    fetchUserVenueHistoriesUseCase = fetchUserVenueHistories,
    prefectureBoundaryRepository = boundaryRepository,
    prefectureLevelRepository = levelRepository,
    findFoursquareAccountUseCase = findFoursquareAccount,
  )

  private fun setUp(
    histories: List<VenueHistory>,
    overrides: Map<Prefecture, PrefectureLevel> = emptyMap(),
  ) {
    every { account.id } returns "account-1"
    coEvery { findFoursquareAccount() } returns account
    coEvery { fetchUserVenueHistories(any()) } returns histories
    coEvery { boundaryRepository.findAll() } returns listOf(
      PrefectureBoundary(Prefecture.Tokyo, listOf(square(139.0, 35.0, 140.0, 36.0))),
    )
    every { levelRepository.flow(account) } returns flowOf(overrides)
  }

  @Test
  fun prefectureWithCheckInBecomesVisited() = runTest {
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
    assertEquals(3, summary.totalScore)
  }

  @Test
  fun prefectureWithoutCheckInStaysUnvisited() = runTest {
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(47, summary.completions.size)
    assertEquals(PrefectureLevel.Unvisited, summary.levelOf(Prefecture.Okinawa))
  }

  @Test
  fun prefectureWithLodgingVenueBecomesStayed() = runTest {
    setUp(
      listOf(
        history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5),
        history(
          state = "Tokyo Prefecture",
          latitude = 35.6,
          longitude = 139.6,
          categoryId = "4bf58dd8d48988d1fa931735",
          categoryName = "ホテル",
          categoryIconPath = "travel/hotel_",
        ),
      ),
    )

    val summary = useCase()

    assertEquals(PrefectureLevel.Stayed, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun nonLodgingCategoryStopsAtVisited() = runTest {
    setUp(
      listOf(
        history(
          state = "Tokyo Prefecture",
          latitude = 35.5,
          longitude = 139.5,
          categoryId = "55a59bace4b013909087cb24",
          categoryName = "ラーメン屋",
          categoryIconPath = "food/ramen_",
        ),
        // 「ホテル」を部分文字列として含む名前でも、宿泊系の ID・アイコンでなければ拾わないこと
        history(
          state = "Tokyo Prefecture",
          latitude = 35.51,
          longitude = 139.51,
          categoryId = "4bf58dd8d48988d116941735",
          categoryName = "ホテルバー",
          categoryIconPath = "nightlife/pub_",
        ),
      ),
    )

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun manualLevelTakesPrecedenceOverAutomaticLevel() = runTest {
    setUp(
      histories = listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)),
      overrides = mapOf(Prefecture.Tokyo to PrefectureLevel.Lived),
    )

    val summary = useCase()

    assertEquals(PrefectureLevel.Lived, summary.levelOf(Prefecture.Tokyo))
    assertEquals(5, summary.totalScore)
  }

  @Test
  fun doesNotUsePolygonWhenStateIsResolvable() = runTest {
    // ポリゴンの外にある座標でも state で東京都に解決される
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 12.0, longitude = 100.0)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun fallsBackToCoordinatesWhenStateIsUnresolvable() = runTest {
    setUp(listOf(history(state = null, latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun fallsBackToCoordinatesForCompositeState() = runTest {
    setUp(listOf(history(state = "東京都/北海道", latitude = 35.5, longitude = 139.5)))

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
  }

  @Test
  fun venuesOutsideJapanAreCountedAsCountriesNotPrefectures() = runTest {
    setUp(
      listOf(
        history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5),
        history(state = "Seoul", latitude = 37.5665, longitude = 126.978, countryCode = "KR"),
        history(state = "CA", latitude = 37.7749, longitude = -122.4194, countryCode = "US"),
        history(state = "Taoyuan", latitude = 25.033, longitude = 121.5654, countryCode = "TW"),
      ),
    )

    val summary = useCase()

    assertEquals(3, summary.totalScore)
    assertEquals(setOf("KR", "US", "TW"), summary.visitedCountryCodes)
  }

  @Test
  fun maxTotalScoreIs235() = runTest {
    setUp(emptyList())

    val summary = useCase()

    assertEquals(235, summary.maxScore)
    assertEquals(0, summary.totalScore)
  }

  @Test
  fun usesAutomaticLevelsWhenNoAccountIsSignedIn() = runTest {
    setUp(listOf(history(state = "Tokyo Prefecture", latitude = 35.5, longitude = 139.5)))
    // サインイン済みアカウントがない場合は手動上書きを読めないので自動判定だけになる
    coEvery { findFoursquareAccount() } returns null

    val summary = useCase()

    assertEquals(PrefectureLevel.Visited, summary.levelOf(Prefecture.Tokyo))
    assertEquals(3, summary.totalScore)
  }

  @Test
  fun passesFetchPolicyThrough() = runTest {
    setUp(emptyList())

    useCase(FetchPolicy.NetworkOnly)

    coVerify { fetchUserVenueHistories(FetchPolicy.NetworkOnly) }
  }

  private fun PrefectureCompletionSummary.levelOf(prefecture: Prefecture): PrefectureLevel {
    return completions.first { it.prefecture == prefecture }.effectiveLevel
  }

  private fun history(
    state: String?,
    latitude: Double,
    longitude: Double,
    countryCode: String = "JP",
    categoryId: String = "4bf58dd8d48988d129951735",
    categoryName: String = "鉄道駅",
    categoryIconPath: String = "travel/trainstation_",
  ): VenueHistory {
    return VenueHistory(
      venue = Venue(
        id = "venue-$latitude-$longitude-$categoryName",
        name = "venue",
        location = VenueLocation(
          latitude = latitude,
          longitude = longitude,
          distance = null,
          country = countryCode,
          countryCode = countryCode,
          postalCode = null,
          state = state,
          city = null,
          address = null,
          crossStreet = null,
          neighborhood = null,
        ),
        createdAt = ZonedDateTime.parse("2020-01-01T00:00:00+09:00"),
        categories = listOf(
          VenueCategory(
            id = categoryId,
            name = categoryName,
            iconUrl = "https://ss3.4sqi.net/img/categories_v2/${categoryIconPath}64.png",
            isPrimary = true,
          ),
        ),
      ),
      count = 1,
    )
  }

  private fun square(
    west: Double,
    south: Double,
    east: Double,
    north: Double,
  ): List<DoubleArray> {
    return listOf(
      doubleArrayOf(west, south),
      doubleArrayOf(east, south),
      doubleArrayOf(east, north),
      doubleArrayOf(west, north),
      doubleArrayOf(west, south),
    )
  }
}

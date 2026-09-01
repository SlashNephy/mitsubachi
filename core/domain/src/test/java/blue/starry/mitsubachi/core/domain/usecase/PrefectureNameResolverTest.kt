package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Prefecture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PrefectureNameResolverTest {
  @Test
  fun resolvesEnglishNameWithPrefectureSuffix() {
    // 実データで最多の表記
    assertEquals(Prefecture.Tokyo, PrefectureNameResolver.resolve("Tokyo Prefecture"))
    assertEquals(Prefecture.Tochigi, PrefectureNameResolver.resolve("Tochigi Prefecture"))
    assertEquals(Prefecture.Kyoto, PrefectureNameResolver.resolve("Kyoto Prefecture"))
  }

  @Test
  fun resolvesEnglishNameWithoutSuffix() {
    assertEquals(Prefecture.Miyagi, PrefectureNameResolver.resolve("Miyagi"))
    assertEquals(Prefecture.Chiba, PrefectureNameResolver.resolve("Chiba"))
    assertEquals(Prefecture.Hyogo, PrefectureNameResolver.resolve("Hyogo"))
  }

  @Test
  fun resolvesRomajiNameWithMacron() {
    // 実データに Hokkaidō が 240 件ある
    assertEquals(Prefecture.Hokkaido, PrefectureNameResolver.resolve("Hokkaidō"))
    assertEquals(Prefecture.Osaka, PrefectureNameResolver.resolve("Ōsaka"))
    assertEquals(Prefecture.Kochi, PrefectureNameResolver.resolve("Kōchi"))
  }

  @Test
  fun resolvesJapaneseName() {
    assertEquals(Prefecture.Okinawa, PrefectureNameResolver.resolve("沖縄県"))
    assertEquals(Prefecture.Hokkaido, PrefectureNameResolver.resolve("北海道"))
    assertEquals(Prefecture.Tokyo, PrefectureNameResolver.resolve("東京都"))
  }

  @Test
  fun resolvesJapaneseNameFollowedByMunicipalityByPrefix() {
    // 実データに 沖縄県伊良部町 がある
    assertEquals(Prefecture.Okinawa, PrefectureNameResolver.resolve("沖縄県伊良部町"))
  }

  @Test
  fun doesNotResolveSlashSeparatedCompositeValue() {
    // 実データに 東京都_北海道 のような値がある。どちらかに寄せず座標判定へ回す
    assertNull(PrefectureNameResolver.resolve("東京都/北海道"))
    assertNull(PrefectureNameResolver.resolve("千葉県/東京都"))
    assertNull(PrefectureNameResolver.resolve("群馬県/栃木県"))
  }

  @Test
  fun doesNotResolveNullOrBlank() {
    assertNull(PrefectureNameResolver.resolve(null))
    assertNull(PrefectureNameResolver.resolve(""))
    assertNull(PrefectureNameResolver.resolve("   "))
  }

  @Test
  fun doesNotResolveNonJapaneseState() {
    assertNull(PrefectureNameResolver.resolve("Seoul"))
    assertNull(PrefectureNameResolver.resolve("CA"))
    assertNull(PrefectureNameResolver.resolve("City of Manila"))
  }

  @Test
  fun prefectureNameDataIsComplete() {
    // resolve に自分自身のフィールドを戻す往復テストは typo を検出できないため、データ側を検査する
    val romajiNames = Prefecture.entries.map { it.romajiName }
    assertEquals(romajiNames.size, romajiNames.toSet().size, "romajiName is duplicated")

    for (prefecture in Prefecture.entries) {
      assertTrue(
        prefecture.romajiName.all { it in 'a'..'z' },
        "${prefecture.name} has a non lowercase ascii romajiName: ${prefecture.romajiName}",
      )
      assertTrue(
        prefecture.japaneseName.last() in "都道府県",
        "${prefecture.name} has an unexpected japaneseName: ${prefecture.japaneseName}",
      )
    }
  }
}

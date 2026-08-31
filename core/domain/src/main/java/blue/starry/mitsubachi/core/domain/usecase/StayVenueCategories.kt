package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Venue

/**
 * 宿泊施設に相当する Foursquare のカテゴリ判定。
 *
 * カテゴリ ID は Foursquare 側で増減するため、カテゴリ名で判定する。
 * ホテルのラウンジに立ち寄っただけでも宿泊と判定されうるが、手動上書きで修正できる前提とする。
 */
object StayVenueCategories {
  // 語として一致させるキーワード。inn は Dinner のような語に部分一致してしまうため語単位で見る
  private val wordKeywords = setOf(
    "hotel",
    "hotels",
    "hostel",
    "motel",
    "inn",
    "ryokan",
    "resort",
    "guesthouse",
    "capsule",
    "lodge",
    "lodging",
    "minshuku",
  )

  // 空白を含むので語分割では拾えないもの。連結した文字列に対する部分一致で見る
  private val phraseKeywords = listOf(
    "bed & breakfast",
    "bed and breakfast",
    "guest house",
  )

  private val wordSeparator = Regex("[^a-z&]+")

  fun matches(venue: Venue): Boolean {
    return venue.categories.any { category ->
      val name = category.name.lowercase()
      phraseKeywords.any { name.contains(it) } ||
        name.split(wordSeparator).any { it in wordKeywords }
    }
  }
}

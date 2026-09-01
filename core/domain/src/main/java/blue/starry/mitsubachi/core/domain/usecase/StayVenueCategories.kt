package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.Venue

/**
 * 宿泊施設に相当する Foursquare のカテゴリ判定。
 *
 * 以前はカテゴリ ID が Foursquare 側で増減することを嫌ってカテゴリ名で判定していたが、
 * この前提はロケール依存で壊れる。Foursquare はリクエストのロケールに応じてカテゴリ名を
 * 翻訳して返すため、実機の日本語環境では「ホテル」「B&Bホテル」「下宿屋」のように日本語で返り、
 * 英語キーワードによる判定はチェックイン 2640 件に対して 1 件も一致しなかった。
 * そのため、ロケールに依存しない値だけで判定する。
 *
 * 1. カテゴリ ID の完全一致を主とする。実データに現れた宿泊系カテゴリを列挙する
 * 2. 補助として、カテゴリアイコンのパスでも拾う。ID の列挙だけでは Foursquare が今後追加する
 *    宿泊カテゴリを取りこぼすため、宿泊系にしか使われていないアイコンのパスを併用する
 *
 * カテゴリ名による判定は行わない。日本語には語境界がないため、「ホテル」の部分一致は
 * 「ホテルバー」のような宿泊でないカテゴリまで拾ってしまい、安全に補助として使えない。
 *
 * ホテルのラウンジに立ち寄っただけでも宿泊と判定されうるが、手動上書きで修正できる前提とする。
 */
object StayVenueCategories {
  // 実データに現れた宿泊系カテゴリの ID。コメントは実データで観測した名前
  private val categoryIds = setOf(
    "4bf58dd8d48988d1fa931735", // ホテル / Hotel
    "4bf58dd8d48988d1f8931735", // B&Bホテル
    "4f4530a74b9074f6e4fb0100", // 下宿屋
    "4bf58dd8d48988d12f951735", // リゾート
    "63be6904847c3692a84b9c27", // ロッジ
    "4bf58dd8d48988d1eb941735", // スキーロッジ
  )

  // 宿泊系カテゴリにしか使われていないアイコンのパス。
  // ロッジとスキーロッジのアイコンは parks_outdoors 配下でハイキングコースや公園と共用のため含めない
  private val iconPaths = listOf(
    "/travel/hotel_",
    "/travel/bedandbreakfast_",
    "/travel/resort_",
  )

  fun matches(venue: Venue): Boolean {
    return venue.categories.any { category ->
      category.id in categoryIds || iconPaths.any { category.iconUrl.contains(it) }
    }
  }
}

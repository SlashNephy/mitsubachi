package blue.starry.mitsubachi.core.domain.model

import androidx.compose.runtime.Immutable

/**
 * 47 都道府県。[code] は JIS X 0401 の都道府県コード。
 */
@Immutable
enum class Prefecture(val code: Int, val japaneseName: String, val romajiName: String) {
  Hokkaido(1, "北海道", "hokkaido"),
  Aomori(2, "青森県", "aomori"),
  Iwate(3, "岩手県", "iwate"),
  Miyagi(4, "宮城県", "miyagi"),
  Akita(5, "秋田県", "akita"),
  Yamagata(6, "山形県", "yamagata"),
  Fukushima(7, "福島県", "fukushima"),
  Ibaraki(8, "茨城県", "ibaraki"),
  Tochigi(9, "栃木県", "tochigi"),
  Gunma(10, "群馬県", "gunma"),
  Saitama(11, "埼玉県", "saitama"),
  Chiba(12, "千葉県", "chiba"),
  Tokyo(13, "東京都", "tokyo"),
  Kanagawa(14, "神奈川県", "kanagawa"),
  Niigata(15, "新潟県", "niigata"),
  Toyama(16, "富山県", "toyama"),
  Ishikawa(17, "石川県", "ishikawa"),
  Fukui(18, "福井県", "fukui"),
  Yamanashi(19, "山梨県", "yamanashi"),
  Nagano(20, "長野県", "nagano"),
  Gifu(21, "岐阜県", "gifu"),
  Shizuoka(22, "静岡県", "shizuoka"),
  Aichi(23, "愛知県", "aichi"),
  Mie(24, "三重県", "mie"),
  Shiga(25, "滋賀県", "shiga"),
  Kyoto(26, "京都府", "kyoto"),
  Osaka(27, "大阪府", "osaka"),
  Hyogo(28, "兵庫県", "hyogo"),
  Nara(29, "奈良県", "nara"),
  Wakayama(30, "和歌山県", "wakayama"),
  Tottori(31, "鳥取県", "tottori"),
  Shimane(32, "島根県", "shimane"),
  Okayama(33, "岡山県", "okayama"),
  Hiroshima(34, "広島県", "hiroshima"),
  Yamaguchi(35, "山口県", "yamaguchi"),
  Tokushima(36, "徳島県", "tokushima"),
  Kagawa(37, "香川県", "kagawa"),
  Ehime(38, "愛媛県", "ehime"),
  Kochi(39, "高知県", "kochi"),
  Fukuoka(40, "福岡県", "fukuoka"),
  Saga(41, "佐賀県", "saga"),
  Nagasaki(42, "長崎県", "nagasaki"),
  Kumamoto(43, "熊本県", "kumamoto"),
  Oita(44, "大分県", "oita"),
  Miyazaki(45, "宮崎県", "miyazaki"),
  Kagoshima(46, "鹿児島県", "kagoshima"),
  Okinawa(47, "沖縄県", "okinawa"),
  ;

    companion object {
    private val byCode = entries.associateBy { it.code }

    fun fromCode(code: Int): Prefecture? {
      return byCode[code]
    }
  }
}

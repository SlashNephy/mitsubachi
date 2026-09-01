package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import kotlinx.coroutines.flow.Flow

/** 都道府県ごとの手動上書きを読み書きする。 */
interface PrefectureLevelRepository {
  fun flow(account: FoursquareAccount): Flow<Map<Prefecture, PrefectureLevel>>

  suspend fun set(account: FoursquareAccount, prefecture: Prefecture, level: PrefectureLevel)

  /** 手動上書きを取り消し、自動判定に戻す。 */
  suspend fun clear(account: FoursquareAccount, prefecture: Prefecture)
}

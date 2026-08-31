package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.dao.PrefectureLevelOverrideDao
import blue.starry.mitsubachi.core.data.database.entity.PrefectureLevelOverride
import blue.starry.mitsubachi.core.domain.model.FoursquareAccount
import blue.starry.mitsubachi.core.domain.model.Prefecture
import blue.starry.mitsubachi.core.domain.model.PrefectureLevel
import blue.starry.mitsubachi.core.domain.usecase.PrefectureLevelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PrefectureLevelRepositoryImpl @Inject constructor(
  private val dao: PrefectureLevelOverrideDao,
) : PrefectureLevelRepository {
  override fun flow(account: FoursquareAccount): Flow<Map<Prefecture, PrefectureLevel>> {
    return dao.findByFoursquareAccountId(account.id).map { entities ->
      entities.mapNotNull { it.toDomainPairOrNull() }.toMap()
    }
  }

  private fun PrefectureLevelOverride.toDomainPairOrNull(): Pair<Prefecture, PrefectureLevel>? {
    val prefecture = Prefecture.fromCode(prefectureCode) ?: return null
    val level = PrefectureLevel.fromScore(this.level) ?: return null
    return prefecture to level
  }

  override suspend fun set(
    account: FoursquareAccount,
    prefecture: Prefecture,
    level: PrefectureLevel,
  ) {
    dao.insertOrUpdate(
      PrefectureLevelOverride(
        foursquareAccountId = account.id,
        prefectureCode = prefecture.code,
        level = level.score,
      ),
    )
  }

  override suspend fun clear(account: FoursquareAccount, prefecture: Prefecture) {
    dao.delete(accountId = account.id, prefectureCode = prefecture.code)
  }
}

package blue.starry.mitsubachi.core.domain.usecase

import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary

interface PrefectureBoundaryRepository {
  /** 47 都道府県分の境界ポリゴンを返す。読み込みに失敗した場合は例外を投げる。 */
  suspend fun findAll(): List<PrefectureBoundary>
}

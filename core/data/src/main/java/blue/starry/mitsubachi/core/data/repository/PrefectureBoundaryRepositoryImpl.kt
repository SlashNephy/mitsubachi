package blue.starry.mitsubachi.core.data.repository

import android.content.Context
import blue.starry.mitsubachi.core.data.asset.PrefectureBoundaryParser
import blue.starry.mitsubachi.core.domain.model.PrefectureBoundary
import blue.starry.mitsubachi.core.domain.usecase.PrefectureBoundaryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

private const val ASSET_FILE_NAME = "prefectures.json"

@Singleton
internal class PrefectureBoundaryRepositoryImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
) : PrefectureBoundaryRepository {
  private val mutex = Mutex()
  private var cache: List<PrefectureBoundary>? = null

  override suspend fun findAll(): List<PrefectureBoundary> {
    cache?.also {
      return it
    }

    return mutex.withLock {
      cache ?: withContext(Dispatchers.IO) {
        val text = context.assets.open(ASSET_FILE_NAME).bufferedReader().use { it.readText() }
        PrefectureBoundaryParser.parse(text)
      }.also {
        cache = it
      }
    }
  }
}

package blue.starry.mitsubachi.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.core.data.database.entity.PrefectureLevelOverride
import kotlinx.coroutines.flow.Flow

@Dao
interface PrefectureLevelOverrideDao {
  @Query("SELECT * FROM `prefecture_levels` WHERE `foursquare_account_id` = :accountId")
  fun findByFoursquareAccountId(accountId: String): Flow<List<PrefectureLevelOverride>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: PrefectureLevelOverride)

  @Query(
    "DELETE FROM `prefecture_levels` " +
      "WHERE `foursquare_account_id` = :accountId AND `prefecture_code` = :prefectureCode",
  )
  suspend fun delete(accountId: String, prefectureCode: Int)
}

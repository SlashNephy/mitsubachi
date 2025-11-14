package blue.starry.mitsubachi.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.core.data.database.entity.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
  @Query("SELECT * FROM `settings` WHERE `foursquare_account_id` = :accountId")
  fun findByFoursquareAccountId(accountId: String): Flow<Settings?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: Settings)
}

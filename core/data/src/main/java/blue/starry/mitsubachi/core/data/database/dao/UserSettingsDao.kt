package blue.starry.mitsubachi.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.core.data.database.entity.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
  @Query("SELECT * FROM `user_settings` WHERE `foursquare_account_id` = :accountId")
  fun findByFoursquareAccountId(accountId: String): Flow<UserSettings?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: UserSettings)
}

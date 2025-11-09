package blue.starry.mitsubachi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.data.database.entity.AppSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
  @Query("SELECT * FROM `app_settings` WHERE `userId` = :userId")
  fun getByUserId(userId: String): Flow<AppSettings?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: AppSettings)
}

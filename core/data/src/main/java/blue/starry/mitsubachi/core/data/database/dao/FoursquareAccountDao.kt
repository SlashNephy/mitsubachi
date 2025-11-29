package blue.starry.mitsubachi.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface FoursquareAccountDao {
  @Query("SELECT * FROM `foursquare_accounts` WHERE `id` = :id")
  suspend fun getById(id: String): FoursquareAccount?

  @get:Query("SELECT * FROM `foursquare_accounts` WHERE `is_primary` = true LIMIT 1")
  val primary: Flow<FoursquareAccount?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: FoursquareAccount)

  @Query("DELETE FROM `foursquare_accounts` WHERE `id` = :id")
  suspend fun deleteById(id: String)
}

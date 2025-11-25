package blue.starry.mitsubachi.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.core.data.database.entity.Cache

@Dao
interface CacheDao {
  @Query("SELECT * FROM `caches` WHERE `key` = :key AND `expires_at` > :now")
  suspend fun get(key: String, now: Long): Cache?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: Cache)

  @Query("DELETE FROM `caches` WHERE `key` = :key")
  suspend fun delete(key: String)
}

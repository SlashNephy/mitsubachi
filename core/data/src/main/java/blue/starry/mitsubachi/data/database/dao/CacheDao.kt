package blue.starry.mitsubachi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.data.database.entity.Cache
import blue.starry.mitsubachi.data.database.entity.CacheFormat

@Dao
interface CacheDao {
  @Query("SELECT * FROM `caches` WHERE `key` = :key AND format = :format")
  suspend fun get(key: String, format: CacheFormat): Cache?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: Cache)

  @Query("DELETE FROM `caches` WHERE `key` = :key AND format = :format")
  suspend fun delete(key: String, format: CacheFormat)
}

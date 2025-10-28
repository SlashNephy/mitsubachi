package blue.starry.mitsubachi.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import blue.starry.mitsubachi.data.database.entity.Cache
import blue.starry.mitsubachi.data.database.entity.CacheType

@Dao
interface CacheDao {
  @Query("SELECT * FROM `caches` WHERE `key` = :key AND `type` = :type")
  suspend fun get(key: String, type: CacheType): Cache?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(entity: Cache)

  @Query("DELETE FROM `caches` WHERE `key` = :key AND `type` = :type")
  suspend fun delete(key: String, type: CacheType)
}

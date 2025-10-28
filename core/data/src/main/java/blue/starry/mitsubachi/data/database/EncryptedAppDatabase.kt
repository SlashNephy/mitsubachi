package blue.starry.mitsubachi.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import blue.starry.mitsubachi.data.database.dao.CacheDao
import blue.starry.mitsubachi.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.data.database.entity.Cache
import blue.starry.mitsubachi.data.database.entity.CacheTypeConverter
import blue.starry.mitsubachi.data.database.entity.FoursquareAccount

@Database(version = 2, entities = [FoursquareAccount::class, Cache::class])
@TypeConverters(CacheTypeConverter::class)
abstract class EncryptedAppDatabase : RoomDatabase() {
  abstract fun foursquareAccount(): FoursquareAccountDao
  abstract fun cache(): CacheDao
}

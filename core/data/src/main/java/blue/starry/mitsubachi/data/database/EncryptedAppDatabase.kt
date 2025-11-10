package blue.starry.mitsubachi.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import blue.starry.mitsubachi.data.database.dao.CacheDao
import blue.starry.mitsubachi.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.data.database.dao.SettingsDao
import blue.starry.mitsubachi.data.database.entity.Cache
import blue.starry.mitsubachi.data.database.entity.CacheFormatConverter
import blue.starry.mitsubachi.data.database.entity.FoursquareAccount
import blue.starry.mitsubachi.data.database.entity.Settings

@Database(version = 4, entities = [FoursquareAccount::class, Cache::class, Settings::class])
@TypeConverters(CacheFormatConverter::class)
abstract class EncryptedAppDatabase : RoomDatabase() {
  abstract fun foursquareAccount(): FoursquareAccountDao
  abstract fun cache(): CacheDao
  abstract fun settings(): SettingsDao
}

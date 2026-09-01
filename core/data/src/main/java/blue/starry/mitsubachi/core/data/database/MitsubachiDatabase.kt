package blue.starry.mitsubachi.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import blue.starry.mitsubachi.core.data.database.dao.CacheDao
import blue.starry.mitsubachi.core.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.core.data.database.dao.PrefectureLevelOverrideDao
import blue.starry.mitsubachi.core.data.database.dao.UserSettingsDao
import blue.starry.mitsubachi.core.data.database.entity.Cache
import blue.starry.mitsubachi.core.data.database.entity.FoursquareAccount
import blue.starry.mitsubachi.core.data.database.entity.PrefectureLevelOverride
import blue.starry.mitsubachi.core.data.database.entity.UserSettings
import blue.starry.mitsubachi.core.data.database.entity.converter.InstantConverter
import blue.starry.mitsubachi.core.data.database.entity.converter.UserSettingsPayloadConverter

@Database(
  version = 7,
  entities = [
    FoursquareAccount::class,
    Cache::class,
    UserSettings::class,
    PrefectureLevelOverride::class,
  ],
)
@TypeConverters(InstantConverter::class, UserSettingsPayloadConverter::class)
internal abstract class MitsubachiDatabase : RoomDatabase() {
  abstract fun foursquareAccount(): FoursquareAccountDao
  abstract fun cache(): CacheDao
  abstract fun userSettings(): UserSettingsDao
  abstract fun prefectureLevelOverride(): PrefectureLevelOverrideDao
}

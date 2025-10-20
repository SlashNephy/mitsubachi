package blue.starry.mitsubachi.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import blue.starry.mitsubachi.data.database.dao.FoursquareAccountDao
import blue.starry.mitsubachi.data.database.entity.FoursquareAccount

@Database(version = 1, entities = [FoursquareAccount::class])
abstract class EncryptedAppDatabase : RoomDatabase() {
  abstract fun foursquareAccount(): FoursquareAccountDao
}

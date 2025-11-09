package blue.starry.mitsubachi.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
  @PrimaryKey val userId: String,
  @ColumnInfo(name = "is_firebase_crashlytics_enabled") val isFirebaseCrashlyticsEnabled: Boolean,
) {
  companion object
}

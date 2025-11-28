package blue.starry.mitsubachi.core.data.database.entity.converter

import androidx.room.TypeConverter
import blue.starry.mitsubachi.core.data.database.entity.UserSettings
import kotlinx.serialization.json.Json

internal object UserSettingsPayloadConverter : RoomTypeConverter<String, UserSettings.Payload> {
  @TypeConverter
  override fun toEntity(value: String): UserSettings.Payload {
    return Json.decodeFromString<UserSettings.Payload>(value)
  }

  @TypeConverter
  override fun fromEntity(entity: UserSettings.Payload): String {
    return Json.encodeToString(entity)
  }
}

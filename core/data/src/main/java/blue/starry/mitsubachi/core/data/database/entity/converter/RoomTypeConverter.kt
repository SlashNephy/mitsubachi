package blue.starry.mitsubachi.core.data.database.entity.converter

import androidx.room.TypeConverter

internal interface RoomTypeConverter<ColumnValue, Entity> {
  @TypeConverter
  fun toEntity(value: ColumnValue): Entity

  @TypeConverter
  fun fromEntity(entity: Entity): ColumnValue
}

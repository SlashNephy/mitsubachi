package blue.starry.mitsubachi.core.data.database.entity.converter

import androidx.room.TypeConverter
import java.time.Instant

internal object InstantConverter : RoomTypeConverter<Long, Instant> {
    @TypeConverter
    override fun toEntity(value: Long): Instant {
        return Instant.ofEpochMilli(value)
    }

    @TypeConverter
    override fun fromEntity(entity: Instant): Long {
        return entity.toEpochMilli()
    }
}

package blue.starry.mitsubachi.data.database.entity

import androidx.room.Entity
import androidx.room.TypeConverter

@Entity(tableName = "caches", primaryKeys = ["key", "format"])
data class Cache(
  val key: String,
  val format: CacheFormat,
  val payload: ByteArray,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Cache

    if (key != other.key) return false
    if (format != other.format) return false
    if (!payload.contentEquals(other.payload)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = key.hashCode()
    result = 31 * result + format.hashCode()
    result = 31 * result + payload.contentHashCode()
    return result
  }
}

enum class CacheFormat {
  JSON,
}

object CacheFormatConverter {
  @TypeConverter
  fun toCacheFormat(value: String): CacheFormat {
    return enumValueOf(value)
  }

  @TypeConverter
  fun fromCacheFormat(cacheFormat: CacheFormat): String {
    return cacheFormat.name
  }
}

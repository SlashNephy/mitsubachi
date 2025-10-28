package blue.starry.mitsubachi.data.database.entity

import androidx.room.Entity
import androidx.room.TypeConverter

@Entity(tableName = "caches", primaryKeys = ["key", "type"])
data class Cache(
  val key: String,
  val type: CacheType,
  val payload: ByteArray,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Cache

    if (key != other.key) return false
    if (type != other.type) return false
    if (!payload.contentEquals(other.payload)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = key.hashCode()
    result = 31 * result + type.hashCode()
    result = 31 * result + payload.contentHashCode()
    return result
  }
}

enum class CacheType {
  JSON,
}

object CacheTypeConverter {
  @TypeConverter
  fun toCacheType(value: String): CacheType {
    return CacheType.valueOf(value)
  }

  @TypeConverter
  fun fromCacheType(cacheType: CacheType): String {
    return cacheType.name
  }
}

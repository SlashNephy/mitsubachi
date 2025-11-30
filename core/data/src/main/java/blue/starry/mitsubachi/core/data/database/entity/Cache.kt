package blue.starry.mitsubachi.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "caches")
class Cache(
  @PrimaryKey val key: String,
  val payload: ByteArray,
  @ColumnInfo(name = "created_at") val createdAt: Instant,
  @ColumnInfo(name = "expires_at") val expiresAt: Instant,
)

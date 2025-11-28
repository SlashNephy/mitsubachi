package blue.starry.mitsubachi.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foursquare_accounts")
data class FoursquareAccount(
  @PrimaryKey val id: String,
  @ColumnInfo(name = "display_name") val displayName: String,
  @ColumnInfo(name = "icon_url") val iconUrl: String,
  @ColumnInfo(name = "access_token") val accessToken: String,
  @ColumnInfo(name = "is_primary") val isPrimary: Boolean,
)

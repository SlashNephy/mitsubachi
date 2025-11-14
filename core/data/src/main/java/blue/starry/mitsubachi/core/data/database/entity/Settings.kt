package blue.starry.mitsubachi.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
  @PrimaryKey @ColumnInfo("foursquare_account_id") val foursquareAccountId: String,
)

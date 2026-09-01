package blue.starry.mitsubachi.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
  tableName = "prefecture_levels",
  primaryKeys = ["foursquare_account_id", "prefecture_code"],
)
data class PrefectureLevelOverride(
  @ColumnInfo("foursquare_account_id") val foursquareAccountId: String,
  // JIS X 0401 の都道府県コード (1..47)
  @ColumnInfo("prefecture_code") val prefectureCode: Int,
  // PrefectureLevel.score (0..5)
  @ColumnInfo("level") val level: Int,
)

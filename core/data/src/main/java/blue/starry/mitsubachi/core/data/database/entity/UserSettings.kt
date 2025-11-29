package blue.starry.mitsubachi.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user_settings")
data class UserSettings(
  @PrimaryKey @ColumnInfo("foursquare_account_id") val foursquareAccountId: String,
  val payload: Payload,
) {
  @Serializable
  data class Payload(
    val useSwarmCompatibilityMode: Boolean,
    val swarmOAuthToken: String?,
    val uniqueDevice: String?,
    val wsid: String?,
    val userAgent: String?,
  )
}

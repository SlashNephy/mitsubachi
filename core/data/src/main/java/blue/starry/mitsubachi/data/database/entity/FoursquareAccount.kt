package blue.starry.mitsubachi.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import blue.starry.mitsubachi.domain.model.FoursquareAccount as DomainFoursquareAccount

@Entity(tableName = "foursquare_accounts")
data class FoursquareAccount(
  @PrimaryKey val id: String,
  @ColumnInfo(name = "display_name") val displayName: String,
  @ColumnInfo(name = "icon_url") val iconUrl: String,
  @ColumnInfo(name = "access_token") val accessToken: String, // TODO: 暗号化したい
  @ColumnInfo(name = "auth_state_json") val authStateJson: String,
) {
  companion object
}

internal fun FoursquareAccount.toDomain(): DomainFoursquareAccount {
  return DomainFoursquareAccount(
    id = id,
    displayName = displayName,
    iconUrl = iconUrl,
    accessToken = accessToken,
    authStateJson = authStateJson,
  )
}

internal fun FoursquareAccount.Companion.fromDomain(domain: DomainFoursquareAccount): FoursquareAccount {
  return FoursquareAccount(
    id = domain.id,
    displayName = domain.displayName,
    iconUrl = domain.iconUrl,
    accessToken = domain.accessToken,
    authStateJson = domain.authStateJson,
  )
}

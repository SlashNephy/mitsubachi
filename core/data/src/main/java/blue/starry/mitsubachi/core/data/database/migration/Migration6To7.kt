package blue.starry.mitsubachi.core.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * `prefecture_levels` を追加する。
 *
 * 既存の DatabaseModule は fallbackToDestructiveMigration を指定しているため、
 * マイグレーションを登録しないと version を上げた時点で foursquare_accounts ごと全テーブルが破棄され、
 * 既存ユーザーが再ログインを強いられる。テーブル追加だけでもマイグレーションを必ず登録すること。
 */
internal object Migration6To7 : Migration(6, 7) {
  override fun migrate(connection: SQLiteConnection) {
    connection.execSQL(
      "CREATE TABLE IF NOT EXISTS `prefecture_levels` (" +
        "`foursquare_account_id` TEXT NOT NULL, " +
        "`prefecture_code` INTEGER NOT NULL, " +
        "`level` INTEGER NOT NULL, " +
        "PRIMARY KEY(`foursquare_account_id`, `prefecture_code`))",
    )
  }
}

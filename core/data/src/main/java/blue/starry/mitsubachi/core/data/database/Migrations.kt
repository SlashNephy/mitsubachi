package blue.starry.mitsubachi.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
  override fun migrate(db: SupportSQLiteDatabase) {
    // Add cachedAt and expiresAt columns to caches table
    db.execSQL("ALTER TABLE caches ADD COLUMN cachedAt INTEGER NOT NULL DEFAULT 0")
    db.execSQL("ALTER TABLE caches ADD COLUMN expiresAt INTEGER DEFAULT NULL")
  }
}
